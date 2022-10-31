package io.pivotal.web.controller;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.pivotal.web.domain.Account;
import io.pivotal.web.service.AccountService;
import io.pivotal.web.service.FlashService;
import io.pivotal.web.service.MarketSummaryService;

@Controller
public class AccountsController {
	private static final Logger logger = LoggerFactory
			.getLogger(AccountsController.class);
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private MarketSummaryService summaryService;
	
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public String accounts(Model model) {
	//public String accounts(Model model, @RegisteredOAuth2AuthorizedClient("pivotalbank") OAuth2AuthorizedClient oAuth2AuthorizedClient) {
		logger.debug("/accounts");
		model.addAttribute("marketSummary", summaryService.getMarketSummary());
		model.addAttribute("accounts",accountService.getAccounts());
		return "accounts";
	}
	
	@RequestMapping(value = "/openaccount", method = RequestMethod.GET)
	public String openAccount(Model model) {
		Account account = new Account();
		account.setOpenbalance(new BigDecimal(100000));
		model.addAttribute("newaccount",account);
		return "openaccount";
	}
	
	@RequestMapping(value = "/openaccount", method = RequestMethod.POST)
	public String saveAccount(Model model,@ModelAttribute(value="newaccount") Account account) {
		logger.debug("saveAccounts: creating account: " + account);
		account.setBalance(account.getOpenbalance());
		account.setCreationdate(new Date());
		
		logger.info("saveAccounts: saving account: " + account);
		
		accountService.createAccount(account);

		return FlashService.redirectWithMessage("/accounts", String.format("Account '%s' created successfully", account.getName()));
	}

}
