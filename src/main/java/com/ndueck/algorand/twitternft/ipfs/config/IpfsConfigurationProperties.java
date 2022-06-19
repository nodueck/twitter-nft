package com.ndueck.algorand.twitternft.ipfs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ipfs")
public class IpfsConfigurationProperties {

    private String host;
    private Integer port;

    private String multiAddr;
}
