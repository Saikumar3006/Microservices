package com.shop.orderservice.config;

import com.shop.orderservice.client.InventoryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Bean
    public InventoryClient inventoryClient() {
        // TIMEOUTS ARE NOT OPTIONAL.
        // Default is "wait forever". If inventory-service accepts the TCP
        // connection but never replies, every order-service thread that calls it
        // blocks indefinitely - the thread pool fills, and order-service stops
        // answering ANY request. One slow dependency takes down a healthy
        // service. That is a cascading failure, and a timeout is the cheapest
        // defence against it.
        //
        // connect = how long to wait for the TCP connection to be established
        // read    = how long to wait for the response once connected
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(3));

        RestClient restClient = RestClient.builder()
                .baseUrl(inventoryServiceUrl)
                .requestFactory(requestFactory)
                .build();

        // Generates a proxy implementing InventoryClient, backed by that
        // RestClient. RestClientAdapter is the bridge between the declarative
        // @HttpExchange interface and the imperative client underneath - swap it
        // for a WebClientAdapter and the interface would not change at all.
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(InventoryClient.class);
    }
}
