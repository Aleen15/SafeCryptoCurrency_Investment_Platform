package in.projectjwt.main.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.projectjwt.main.entities.Portfolio;
import in.projectjwt.main.entities.Stock;
import in.projectjwt.main.entities.User;
import in.projectjwt.main.exceptions.ResourceNotFoundException;
import in.projectjwt.main.repositories.PortfolioRepository;
import in.projectjwt.main.repositories.StockRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockRepository stockRepository;
   
    @Autowired
    AuthenticationService userServicesImp;
    

    public Portfolio addPortfolio(Long userId, Portfolio portfolio) {
        // Fetch the user from the database
        User user = userServicesImp.findById(userId);
               
        
        // Set up the bidirectional relationship
        portfolio.setUser(user);
        user.getPortfolios().add(portfolio);
        
        // Set timestamps
        portfolio.setCreatedAt(LocalDateTime.now());
        portfolio.setUpdatedAt(LocalDateTime.now());
        
        // Save the portfolio to the database
        return portfolioRepository.save(portfolio);
    }

    
    
    //get portfolio by portfolio id
   public Portfolio getPortfolioById(Long portfolioId) {
        return portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
    }
  
    
    
    //update portfolio by portfolio id
    public Portfolio updatePortfolio(Long portfolioId, Portfolio portfolioDetails) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        portfolio.setPortfolioName(portfolioDetails.getPortfolioName());
        portfolio.setInvestmentAgenda(portfolioDetails.getInvestmentAgenda());
        portfolio.setUpdatedAt(LocalDateTime.now());
        return portfolioRepository.save(portfolio);
    }

    //deleter portfolio by portfolio id
    public void deletePortfolio(Long portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        portfolioRepository.delete(portfolio);
    }

    public Map<String, Double> calculatePortfolioSummary(Long portfolioId) {
        Portfolio portfolio = getPortfolioById(portfolioId);
        List<Stock> stocks = stockRepository.findByPortfolio(portfolio);

        double totalValue = stocks.stream()
                .mapToDouble(stock -> stock.getStockcurrentPrice() * stock.getHoldings())
                .sum();

        double totalProfitLoss = stocks.stream()
                .mapToDouble(stock -> stock.getProfitLoss())
                .sum();

        return Map.of(
            "totalValue", portfolio.getBalance(),
            "totalProfitLoss", totalProfitLoss
        );
    }


    // Get portfolios by user ID
    public List<Portfolio> getPortfoliosByUser(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        portfolios.forEach(portfolio -> System.out.println("portfolio name: " + portfolio.getPortfolioName()));
        portfolios.forEach(portfolio -> System.out.println("Investment Agenda: " + portfolio.getInvestmentAgenda()));
        return portfolios;
    }
    
    
    // Get portfolios by portfolio ID
    public List<Portfolio> getPortfolioByPortfolioId(Long portfolioId) {
        List<Portfolio> portfolios = portfolioRepository.findByPortfolioId(portfolioId);
        portfolios.forEach(portfolio -> System.out.println("portfolio name: " + portfolio.getPortfolioName()));
        portfolios.forEach(portfolio -> System.out.println("Investment Agenda: " + portfolio.getInvestmentAgenda()));
        return portfolios;
    }
    
    
    
}


