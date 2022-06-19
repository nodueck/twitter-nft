package com.ndueck.algorand.twitternft.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.algorand.algosdk.v2.client.common.AlgodClient;

@Configuration
public class AlgorandConfiguration {

    @Autowired
    private AlgorandConfigurationProperties properties;

    @Bean
    public AlgodClient algodClient() {
        return new AlgodClient(properties.getAlgodApiAddress(), properties.getAlgodApiPort(), properties.getAlgodApiToken());
    }
}
