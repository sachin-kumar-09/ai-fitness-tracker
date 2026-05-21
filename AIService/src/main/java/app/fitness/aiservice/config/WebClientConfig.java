package app.fitness.aiservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    // load balanced is used to call from service not from url
    public WebClient.Builder webClientBuilder() {
        log.info("Inside WebClientConfig :: webClientBuilder at {}", System.currentTimeMillis());
        return WebClient.builder();
    }


    @Bean
    @LoadBalanced
    public WebClient userServiceWebClient(WebClient.Builder webClientBuilder) {
        log.info("Inside WebClientConfig :: userServicwebClient at {}", System.currentTimeMillis());
        return webClientBuilder.baseUrl("http://USERSERVICE")
                .build();
    }
}
