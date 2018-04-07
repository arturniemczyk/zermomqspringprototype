package com.example.zmqspring.zmqspringintegration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ZMQSubscribe {

    String channel();

    String host() default "127.0.0.1";

    String port() default "5555";

}
