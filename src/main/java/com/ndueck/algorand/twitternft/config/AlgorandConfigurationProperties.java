package com.ndueck.algorand.twitternft.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "algorand")
public class AlgorandConfigurationProperties {

    private String algodApiAddress;
    private int algodApiPort;
    private String algodApiToken;
}
