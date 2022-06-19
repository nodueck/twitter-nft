package com.ndueck.algorand.twitternft.ipfs.config;

import io.ipfs.api.IPFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IpfsConfiguration {

    @Autowired
    private IpfsConfigurationProperties properties;

    @Bean
    @ConditionalOnMissingBean(type = "IPFS")
    @ConditionalOnProperty(prefix = "ipfs", name = {"host", "port"})
    public IPFS ipfsClient() {
        return new IPFS(properties.getHost(), properties.getPort());
    }

    @Bean
    @ConditionalOnProperty(prefix = "ipfs", name = {"multiaddr"})
    public IPFS ipfsClientByMultiAddr() {
        return new IPFS(properties.getMultiAddr());
    }
}
