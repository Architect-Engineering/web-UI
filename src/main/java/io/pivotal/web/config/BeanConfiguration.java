package io.pivotal.web.config;

import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfiguration {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder, LoadBalancerExchangeFilterFunction eff) {
        return webClientBuilder
                .filter(eff)
                .build();
    }

}
