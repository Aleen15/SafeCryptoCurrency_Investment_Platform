package in.projectjwt.main.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import in.projectjwt.main.entities.Portfolio;

import in.projectjwt.main.services.PortfolioService;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/auth/portfolios")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;
       
    
    //creating portfolio by giving userId
    @PostMapping("/add/{userId}")
    public ResponseEntity<?> createPortfolio(@RequestBody Portfolio portfolio,@PathVariable Long userId) {
        Portfolio createdPortfolio = portfolioService.addPortfolio(userId,portfolio);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPortfolio);
        //return ResponseEntity.ok("Portfolio created successfully!");
    }
    
    

    
    
    @GetMapping("/{portfolioId}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable Long portfolioId) {
        Portfolio portfolio = portfolioService.getPortfolioById(portfolioId);
        return ResponseEntity.ok(portfolio);
    }
    
    
    //get portfolio by userId
    @GetMapping("/getuserport/{userId}")
    public List<Portfolio> getUserPortfolios(@PathVariable Long userId) {
    	//session.setAttribute("userId", userId); // Ensure this is correctly set
    	System.out.println("userId = "+userId);
        return portfolioService.getPortfoliosByUser(userId);
    }



    @PutMapping("/update/{portfolioId}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable Long portfolioId, @RequestBody Portfolio portfolio) {
        Portfolio updatedPortfolio = portfolioService.updatePortfolio(portfolioId, portfolio);
        return ResponseEntity.ok(updatedPortfolio);
    }
    


    @DeleteMapping("/delete/{portfolioId}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long portfolioId) {
        portfolioService.deletePortfolio(portfolioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{portfolioId}/summary")
    public ResponseEntity<Map<String, Double>> getPortfolioSummary(@PathVariable Long portfolioId) {
        Map<String, Double> summary = portfolioService.calculatePortfolioSummary(portfolioId);
        return ResponseEntity.ok(summary);
    }
}

