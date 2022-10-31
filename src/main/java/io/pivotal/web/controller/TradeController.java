package io.pivotal.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.pivotal.web.domain.Account;
import io.pivotal.web.domain.CompanyInfo;
import io.pivotal.web.domain.Order;
import io.pivotal.web.domain.Portfolio;
import io.pivotal.web.domain.Quote;
import io.pivotal.web.domain.Search;
import io.pivotal.web.service.AccountService;
import io.pivotal.web.service.FlashService;
import io.pivotal.web.service.PortfolioService;
import io.pivotal.web.service.QuotesService;

@Controller
public class TradeController {

	private static final Logger logger = LoggerFactory
			.getLogger(TradeController.class);
	
	@Autowired
	private QuotesService marketService;
	
	@Autowired
	private PortfolioService portfolioService;
	
	@Autowired
	private AccountService accountService;

	@ModelAttribute("accounts")
	public List<Account> accounts() {
		return accountService.getAccounts();
	}

	@ModelAttribute("portfolio")
	public Portfolio portfolio() {
		return portfolioService.getPortfolio();
	}

	@ModelAttribute("search")
	public Search search() {
		return new Search();
	}

	@ModelAttribute("order")
	public Order order() {
		return new Order();
	}

	
	@RequestMapping(value = "/trade", method = RequestMethod.GET)
	public String showTrade(Model model) {
		return "trade";
	}

	@RequestMapping(value = "/trade", method = RequestMethod.POST)
	public String showTrade(Model model, @ModelAttribute("search") Search search) {
		logger.debug("/trade.POST - symbol: " + search.getName());
		
		//model.addAttribute("marketSummary", marketService.getMarketSummary());
		model.addAttribute("search", search);
		
		if (search.getName() == null || search.getName().equals("") ) {
			model.addAttribute("quotes", new ArrayList<Quote>());
		} else {
			List<Quote> newQuotes = getQuotes(search.getName());
			model.addAttribute("quotes", newQuotes);
		}

		return "trade";
	}
	
	@RequestMapping(value = "/order", method = RequestMethod.POST)
	public String buy(Model model, @ModelAttribute("order") Order order) {
		order.setCompletionDate(new Date());
		Order result = portfolioService.sendOrder(order);
		return FlashService.redirectWithMessage("/trade", "Order successful!");
	}
	
	
	private List<Quote> getQuotes(String companyName) {
		logger.debug("Fetching quotes for companies that have: " + companyName + " in name or symbol");
		List<CompanyInfo> companies = marketService.getCompanies(companyName);
		
		/*
		 * Sleuth currently doesn't work with parallelStreams
		 */
		//get distinct companyinfos and get their respective quotes in parallel.

		List<String> symbols = companies.stream().map(company -> company.getSymbol()).collect(Collectors.toList());
		logger.debug("symbols: fetching "+ symbols.size() + " quotes for following symbols: " + symbols);
		List<String> distinctsymbols = symbols.stream().distinct().collect(Collectors.toList());
		logger.debug("distinct: fetching "+ distinctsymbols.size() + " quotes for following symbols: " + distinctsymbols);
		List<Quote> quotes;
		if (distinctsymbols.size() > 0) {
			quotes = marketService.getMultipleQuotes(distinctsymbols)
					.stream()
					.distinct()
					.filter(quote -> quote.getName() != null && !"".equals(quote.getName()) && "SUCCESS".equals(quote.getStatus()))
					.collect(Collectors.toList());
		} else {
			quotes = new ArrayList<>();
		}
		return quotes;
	}
	
}
