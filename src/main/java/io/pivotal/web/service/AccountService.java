package io.pivotal.web.service;

import com.newrelic.api.agent.Trace;
import io.pivotal.web.domain.Account;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.HystrixCommands;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service
@RefreshScope
public class AccountService {
    private static final Logger logger = LoggerFactory
            .getLogger(AccountService.class);

    @Autowired
    private WebClient webClient;

    @Value("${pivotal.accountsService.name}")
    private String accountsService;


    @Trace(async = true)
    public void createAccount(Account account) {
        logger.debug("Creating account ");
        String status = webClient
                .post()
                .uri("//" + accountsService + "/accounts/")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(account)
                .retrieve()
                .bodyToMono(String.class)
                .block();
       // String status = oAuth2RestTemplate.postForObject("//" + accountsService + "/accounts/", account, String.class);
        logger.info("Status from registering account is " + status);
    }


    @Trace(async = true)
    public List<Account> getAccounts() {
        logger.debug("Looking for accounts");
        ParameterizedTypeReference<List<Account>> typeRef = new ParameterizedTypeReference<List<Account>>() {};

        Publisher<List<Account>> accountsPublisher = webClient
                .get()
                .uri("//" + accountsService + "/accounts")
                .retrieve()
                .bodyToMono(typeRef);

        List<Account> accounts = HystrixCommands
                .from( accountsPublisher )
                .eager()
                .commandName("accounts")
                .fallback(Flux.just(getAccountsFallback()))
                .toMono()
                .block();
        return accounts;
    }

    public List<Account> getAccountsFallback() {
        logger.warn("Invoking fallback for getAccount");
        return getDummyAccounts();
    }
    
	private List<Account> getDummyAccounts() {
		List<Account> accounts = new ArrayList<>();
		Account acc1 = new Account();
		acc1.setId(1);
		acc1.setName("US Trade Account");
		acc1.setCurrency("USD");
		acc1.setBalance(new BigDecimal(5500000));
		accounts.add(acc1);
		
		Account acc2 = new Account();
		acc2.setId(2);
		acc2.setName("Global Trade Account");
		acc2.setCurrency("EUR");
		acc2.setBalance(new BigDecimal(3000000));
		accounts.add(acc2);
		
		return accounts;
	}


    @Trace(async = true)
    public List<Account> getAccountsByType(String type) {
        logger.debug("Looking for account with type: " + type);
        ParameterizedTypeReference<List<Account>> typeRef = new ParameterizedTypeReference<List<Account>>() {};
        Publisher<List<Account>> accountsPublisher = webClient
                .get()
                .uri("//" + accountsService + "/accounts?type=" + type)
                .retrieve()
                .bodyToMono(typeRef);

        List<Account> accounts = HystrixCommands
                .from( accountsPublisher )
                .eager()
                .commandName("accounts")
                .fallback(Flux.just(getAccountsFallback()))
                .toMono()
                .block();
       // Account[] accounts = oAuth2RestTemplate.getForObject("//" + accountsService + "/accounts?type={type}", Account[].class, type);
        return accounts;
    }

}
