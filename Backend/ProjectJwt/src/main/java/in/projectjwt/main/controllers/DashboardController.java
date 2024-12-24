package in.projectjwt.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import in.projectjwt.main.services.DashboardService;


@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/auth/dash/crypto-news")
    public JsonNode getCryptoNewsData() {
        return dashboardService.getCryptoNews(); // Endpoint for cryptocurrency news
    }
}
