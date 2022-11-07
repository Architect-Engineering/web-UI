package io.pivotal.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.pivotal.web.service.AccountService;
import io.pivotal.web.service.MarketSummaryService;
import io.pivotal.web.service.PortfolioService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class UserController {
	
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
		model.addAttribute("accounts",accountService.getAccounts());
		model.addAttribute("portfolio",portfolioService.getPortfolio());
		return "index";
	}
}
