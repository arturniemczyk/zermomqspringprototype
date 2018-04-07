package com.example.zmqspring.zmqspringintegration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.zeromq.ZMQ;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ZMQSpringIntegration implements ApplicationListener, BeanPostProcessor {

    private List<Subscriber> subscribers = new ArrayList<>();

    private List<Publisher> publishers = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

        if (applicationEvent instanceof ApplicationStartedEvent) {

            ZMQ.Context context = ZMQ.context(1);

            for (Publisher publisher: publishers) {

                ZMQ.Socket publisherSocket = context.socket(ZMQ.PUB);

                publisherSocket.bind("tcp://" + publisher.getHost() + ":" + publisher.getPort());

                publisher.setPublisherSocket(publisherSocket);

            }

            Map<String, List<Subscriber>> subscriberGroups =
                    subscribers.stream().collect(Collectors.groupingBy(s -> s.getHost() + ":" + s.getPort()));

            ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

            taskExecutor.setCorePoolSize(subscriberGroups.size());

            taskExecutor.setWaitForTasksToCompleteOnShutdown(true);

            taskExecutor.initialize();

            for(Map.Entry<String, List<Subscriber>> entry : subscriberGroups.entrySet()) {

                String hostAndPort = entry.getKey();
                List<Subscriber> subscribers = entry.getValue();

                ZMQ.Socket subscriberSocket = context.socket(ZMQ.SUB);

                subscriberSocket.connect("tcp://" + hostAndPort);

                for (Subscriber subscriber: subscribers) {

                    subscriberSocket.subscribe(subscriber.getChannel().getBytes());
                }

                taskExecutor.execute(() -> {

                    while (true) {

                        String channel = subscriberSocket.recvStr();

                        String message = subscriberSocket.recvStr();

                        if (channel != null && message != null) {

                            for (Subscriber subscriber: subscribers) {

                                if (channel.equals(subscriber.getChannel())) {

                                    try {

                                        subscriber.getMethod().invoke(subscriber.getBean(), message);

                                    } catch (IllegalAccessException | InvocationTargetException e) {

                                        e.printStackTrace();
                                    }

                                }

                            }
                        }

                    }

                });

            }
        }

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Class<?> managedBeanClass = bean.getClass();
        ReflectionUtils.doWithMethods(managedBeanClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {

                if (method.isAnnotationPresent(ZMQSubscribe.class)) {

                    ZMQSubscribe subscribe = AnnotationUtils.getAnnotation(method, ZMQSubscribe.class);
                    Subscriber subscriber = new Subscriber(subscribe.host(), subscribe.port(), subscribe.channel(), method, bean);
                    subscribers.add(subscriber);

                }

            }
        });


        ReflectionUtils.doWithFields(managedBeanClass, new ReflectionUtils.FieldCallback() {

            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                if (field.isAnnotationPresent(ZMQPublisher.class)) {

                    if (field.getType().equals(Publisher.class)) {

                        ZMQPublisher publisherAnnotation = AnnotationUtils.getAnnotation(field, ZMQPublisher.class);

                        Publisher publisher = new Publisher(publisherAnnotation.host(), publisherAnnotation.port());

                        publishers.add(publisher);

                        ReflectionUtils.setField(field, bean, publisher);
                    }

                }

            }
        });

        return bean;
    }
}
