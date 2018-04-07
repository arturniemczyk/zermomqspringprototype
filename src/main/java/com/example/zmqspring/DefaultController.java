package com.example.zmqspring;

import com.example.zmqspring.zmqspringintegration.Publisher;
import com.example.zmqspring.zmqspringintegration.ZMQPublisher;
import com.example.zmqspring.zmqspringintegration.ZMQSubscribe;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @ZMQPublisher
    public Publisher publisher;

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String test(){

        publisher.publish("default", "Sample Message!!!");

        return  "Some Response";
    }

    @ZMQSubscribe(channel="default")
    public void onReceive(String str) {

        System.out.println("Message received: " + str);
    }

}
