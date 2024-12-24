package in.projectjwt.main.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.projectjwt.main.entities.CoinDetails;
import in.projectjwt.main.entities.Portfolio;
import in.projectjwt.main.entities.Stock;
import in.projectjwt.main.exceptions.ResourceNotFoundException;
import in.projectjwt.main.repositories.PortfolioRepository;
import in.projectjwt.main.repositories.StockRepository;
import jakarta.transaction.Transactional;

@Service
public class StockService {

	 private static final Logger logger = LoggerFactory.getLogger(StockService.class);
	
    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private StockApiService stockApiService; // Service to call CoinGecko API
    
    @Autowired
    private EmailService emailService;

 // Method to buy stock
    public Stock buyStock(Long portfolioId, String stockSymbol, int noOfShares) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        // Get the current coin details from the API
        CoinDetails coinDetails = stockApiService.getCoinDetailedInfo(stockSymbol);
        double currentPrice = coinDetails.getCurrentPrice();
        System.out.println("Current Price: "+currentPrice);
        String imageUrl = coinDetails.getImageUrl();
        String name = coinDetails.getName();
        double priceChange24h = coinDetails.getPriceChange24h();

        // Calculate total purchase price
        double purchasePrice = currentPrice * noOfShares;

        // Check if the stock already exists in the portfolio
        Stock existingStock = stockRepository.findByPortfolioAndStockSymbol(portfolio, stockSymbol);

        if (existingStock != null) {
            // Update existing stock: Buying more shares of the existing stock
            double totalShares = existingStock.getNoOfShares() + noOfShares;
            double newAvgBuyPrice = (existingStock.getAvgBuyPrice() * existingStock.getNoOfShares() + purchasePrice) / totalShares;
            double newHoldings = existingStock.getHoldings() + purchasePrice;

            existingStock.setNoOfShares((int) totalShares);
            existingStock.setAvgBuyPrice(newAvgBuyPrice);
            existingStock.setHoldings(newHoldings);
            existingStock.setStockcurrentPrice(currentPrice); // Update current price
            System.out.println("Current price updated");
            existingStock.setStockImg(imageUrl);
            existingStock.setPercentChange24h(priceChange24h);

            // Save the updated stock
            stockRepository.save(existingStock);
         // Send email notification
            emailService.sendStockPurchaseEmail(portfolio.getUser().getEmail(), portfolio.getUser().getFullName(), stockSymbol, noOfShares, purchasePrice);
            return existingStock;
        } else {
            // Create a new stock entry if it doesn't exist in the portfolio
            Stock newStock = new Stock();
            newStock.setStockSymbol(stockSymbol);
            newStock.setStockcurrentPrice(currentPrice);
            System.out.println("Current price stored");
            newStock.setHoldings(purchasePrice);  // Holdings are initialized with purchase price
            newStock.setInitialInvestment(purchasePrice); // Initial investment is the purchase price
            newStock.setAvgBuyPrice(currentPrice);  // Set initial avg buy price as the current price
            newStock.setNoOfShares(noOfShares);
            newStock.setPercentChange24h(priceChange24h); // Set the price change in 24h (from the API)
            newStock.setStockImg(imageUrl); // Set the image URL
            newStock.setPortfolio(portfolio);

            // Save the stock to the repository
         // Send email notification
            emailService.sendStockPurchaseEmail(portfolio.getUser().getEmail(), portfolio.getUser().getFullName(), stockSymbol, noOfShares, purchasePrice);
            System.out.println("Email send to : "+portfolio.getUser().getEmail());
            return stockRepository.save(newStock);
        }
    }
    public List<Stock> getStocksByPortfolio(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found"));
          return stockRepository.findByPortfolio(portfolio);
     
    }
    
    
    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd";
    
    public List<Map<String, Object>> getCoins() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(COINGECKO_API_URL, List.class);
    }
    @Transactional
    public void sellStock(Long portfolioId, Long stockId, int quantitySell) {
        // Retrieve the portfolio and stock
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        // Check if there are enough shares to sell
        if (stock.getNoOfShares() < quantitySell) {
            throw new RuntimeException("Not enough shares to sell");
        }

        // Fetch the current price of the stock from the external API
        CoinDetails coinDetails = stockApiService.getCoinDetailedInfo(stock.getStockSymbol());
        double currentPrice = coinDetails.getCurrentPrice();

        // Calculate the total sale value
        double saleValue = currentPrice * quantitySell;

        // Calculate the portion of the initial investment being sold
        double initialInvestment = stock.getAvgBuyPrice() * quantitySell;

        // Calculate profit or loss
        double profitOrLoss = saleValue - initialInvestment;

        // Update the stock's details
        stock.setProfitLoss(profitOrLoss);

        // Update the number of shares and holdings
        stock.setNoOfShares(stock.getNoOfShares() - quantitySell);
        stock.setHoldings(stock.getHoldings() - initialInvestment);

        // If there are remaining shares, update the average buy price
        if (stock.getNoOfShares() > 0) {
            double remainingShares = stock.getNoOfShares();
            double newAvgBuyPrice = (stock.getAvgBuyPrice() * (stock.getNoOfShares() + quantitySell) - initialInvestment) / remainingShares;
            stock.setAvgBuyPrice(newAvgBuyPrice);
        } else {
            // If no shares are left, delete the stock
            stockRepository.delete(stock);
            return;
        }

        // Save the updated stock to the repository
        stockRepository.save(stock);
     // Send email notification for the stock sale
        emailService.sendStockSaleEmail(portfolio.getUser().getEmail(), portfolio.getUser().getFullName(), 
                                        stock.getStockSymbol(), quantitySell, saleValue, profitOrLoss);
        System.out.println("Email send for Sold Confirmation to : "+ portfolio.getUser().getEmail());

        // Optionally update the portfolio balance
        portfolio.setBalance(portfolio.getBalance() + saleValue);
        portfolioRepository.save(portfolio);
        
    }

	 
	 
}
