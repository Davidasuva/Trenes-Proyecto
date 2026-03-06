package edu.project.model;

public class server {
    private String ip;
    private int port;
    private String serviceName;
    private String uri;

    public server(String ip, int port, String serviceName, String uri) {
        this.ip = ip;
        this.port = port;
        this.serviceName = serviceName;
        this.uri = uri;
    }
}
