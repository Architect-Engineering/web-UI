package io.pivotal.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.pivotal.web.domain.RegistrationRequest;
import io.pivotal.web.service.AccountService;
import io.pivotal.web.service.FlashService;
import io.pivotal.web.service.MarketSummaryService;
import io.pivotal.web.service.PortfolioService;
import io.pivotal.web.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private PortfolioService portfolioService;
	
	@Autowired
	private MarketSummaryService summaryService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showHome(Model model){
		model.addAttribute("marketSummary", summaryService.getMarketSummary());
		return "index";
	}

	@GetMapping("/home")
	public String authorizedHome(Model model) {
		model.addAttribute("marketSummary", summaryService.getMarketSummary());
		String currentUserName = "";
		model.addAttribute("accounts",accountService.getAccounts());
		model.addAttribute("portfolio",portfolioService.getPortfolio());
		model.addAttribute("user", userService.getUser(currentUserName));
		return "index";
	}


	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(Model model) {
		model.addAttribute("registration", new RegistrationRequest());
		return "registration";
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public String register(Model model, @Valid @ModelAttribute(value="registration") RegistrationRequest registrationRequest,
						   BindingResult bindingResult) {
		log.info("register: user:" + registrationRequest.getEmail());
		if (bindingResult.hasErrors()) {
			model.addAttribute("errors", bindingResult);
			return "registration";
		}
		this.userService.registerUser(registrationRequest);
		return FlashService.redirectWithMessage( "/", String.format("User %s successfully registered", registrationRequest.getEmail()));
	}
}
