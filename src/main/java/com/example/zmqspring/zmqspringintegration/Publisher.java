package com.example.zmqspring.zmqspringintegration;

import org.zeromq.ZMQ;

public class Publisher {

    private ZMQ.Socket publisherSocket;
    private String host;
    private String port;

    public Publisher(String host, String port) {

        this.host = host;
        this.port = port;
    }

    public void publish(String channel, String message) {
        publisherSocket.sendMore (channel);
        publisherSocket.send (message);
    }

    public void setPublisherSocket(ZMQ.Socket publisherSocket) {
        this.publisherSocket = publisherSocket;
    }

    public String getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}
