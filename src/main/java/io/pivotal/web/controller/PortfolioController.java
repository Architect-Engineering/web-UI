package io.pivotal.web.controller;

import io.pivotal.web.domain.Order;
import io.pivotal.web.service.AccountService;
import io.pivotal.web.service.MarketSummaryService;
import io.pivotal.web.service.PortfolioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class PortfolioController {
    private static final Logger logger = LoggerFactory
            .getLogger(PortfolioController.class);

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private MarketSummaryService summaryService;

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/portfolio", method = RequestMethod.GET)
    public String portfolio(Model model) {
        logger.debug("/portfolio");
        model.addAttribute("marketSummary", summaryService.getMarketSummary());
        model.addAttribute("portfolio", portfolioService.getPortfolio());
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("order", new Order());
        return "portfolio";
    }

}
