package io.pivotal.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.newrelic.api.agent.Trace;

import io.pivotal.web.domain.RegistrationRequest;
import io.pivotal.web.domain.User;
import lombok.extern.slf4j.Slf4j;

@Service
@RefreshScope
@Slf4j
public class UserService {

    @Autowired(required = false)
    private WebClient webClient;

    @Value("${pivotal.userService.name}")
    private String userService;


    @Trace(async = true)
    public void registerUser(RegistrationRequest registrationRequest) {
        log.debug("Creating user with userId: " + registrationRequest.getEmail());
        User user = webClient
                .post()
                .uri("//" + userService + "/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(registrationRequest)
                .retrieve()
                .bodyToMono(User.class)
                .block();
        log.info("Status from registering account for " + registrationRequest.getEmail() + " is " + user.getId());
    }

    @Trace(async = true)
    public User getUser(String user) {
        log.debug("Looking for user with user name: " + user);
        User account = this.webClient
                .get()
                .uri("//" + userService + "/users/"+ user)
                .retrieve()
                .bodyToMono(User.class)
                .block();

        return account;
    }
}