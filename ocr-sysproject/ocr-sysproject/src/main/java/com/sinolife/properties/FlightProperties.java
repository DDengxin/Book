package com.sinolife.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ocr.flight")
public class FlightProperties {

    private  String  host;

    private  String  port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public FlightProperties(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public FlightProperties() {
    }
}
