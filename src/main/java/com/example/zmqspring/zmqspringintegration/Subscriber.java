package com.example.zmqspring.zmqspringintegration;

import java.lang.reflect.Method;

public class Subscriber {

    private String host;

    private String port;

    private String channel;

    private Method method;

    private Object bean;

    public Subscriber(String host, String port, String channel, Method method, Object bean ) {

        this.host = host;
        this.port = port;
        this.channel = channel;
        this.method = method;
        this.bean = bean;

    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getChannel() {
        return channel;
    }

    public Method getMethod() {
        return method;
    }

    public Object getBean() {
        return bean;
    }
}
