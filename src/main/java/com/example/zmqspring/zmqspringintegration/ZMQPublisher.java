package com.example.zmqspring.zmqspringintegration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ZMQPublisher {

    String host() default "*";

    String port() default "5555";

}
