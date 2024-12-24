package in.projectjwt.main.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import in.projectjwt.main.entities.CoinDetails;
import in.projectjwt.main.entities.Portfolio;
import in.projectjwt.main.entities.Stock;
import in.projectjwt.main.repositories.PortfolioRepository;
import in.projectjwt.main.repositories.StockRepository;

import java.util.List;
import java.util.Map;

public class StockServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockApiService stockApiService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private StockService stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBuyStock_NewStock() {
        Long portfolioId = 1L;
        String stockSymbol = "AAPL";
        int noOfShares = 10;

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(portfolioId);
        portfolio.setBalance(10000);  // Some balance to allow stock purchase

        CoinDetails coinDetails = new CoinDetails("AAPL", "Apple", "url", 150.00, 1.5, 3.5, 0.5);

        // Mock dependencies
        when(portfolioRepository.findById(portfolioId)).thenReturn(java.util.Optional.of(portfolio));
        when(stockApiService.getCoinDetailedInfo(stockSymbol)).thenReturn(coinDetails);
        when(stockRepository.findByPortfolioAndStockSymbol(portfolio, stockSymbol)).thenReturn(null);
        when(stockRepository.save(any(Stock.class))).thenReturn(new Stock());

        // Call the method
        Stock result = stockService.buyStock(portfolioId, stockSymbol, noOfShares);

        // Validate the results
        assertNotNull(result);
        verify(stockRepository, times(1)).save(any(Stock.class));
        verify(emailService, times(1)).sendStockPurchaseEmail(eq(portfolio.getUser().getEmail()), eq(portfolio.getUser().getFullName()), eq(stockSymbol), eq(noOfShares), anyDouble());
    }

    @Test
    void testBuyStock_ExistingStock() {
        Long portfolioId = 1L;
        String stockSymbol = "AAPL";
        int noOfShares = 10;

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(portfolioId);
        Stock existingStock = new Stock();
        existingStock.setStockSymbol(stockSymbol);
        existingStock.setNoOfShares(5);
        existingStock.setAvgBuyPrice(140.00);

        CoinDetails coinDetails = new CoinDetails("AAPL", "Apple", "url", 150.00, 1.5, 3.5, 0.5);

        // Mock dependencies
        when(portfolioRepository.findById(portfolioId)).thenReturn(java.util.Optional.of(portfolio));
        when(stockRepository.findByPortfolioAndStockSymbol(portfolio, stockSymbol)).thenReturn(existingStock);
        when(stockApiService.getCoinDetailedInfo(stockSymbol)).thenReturn(coinDetails);
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);

        // Call the method
        Stock result = stockService.buyStock(portfolioId, stockSymbol, noOfShares);

        // Validate the results
        assertNotNull(result);
        verify(stockRepository, times(1)).save(any(Stock.class));
        verify(emailService, times(1)).sendStockPurchaseEmail(eq(portfolio.getUser().getEmail()), eq(portfolio.getUser().getFullName()), eq(stockSymbol), eq(noOfShares), anyDouble());
    }

    @Test
    void testGetStocksByPortfolio() {
        Long portfolioId = 1L;

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(portfolioId);

        Stock stock = new Stock();
        stock.setStockSymbol("AAPL");

        // Mock dependencies
        when(portfolioRepository.findById(portfolioId)).thenReturn(java.util.Optional.of(portfolio));
        when(stockRepository.findByPortfolio(portfolio)).thenReturn(List.of(stock));

        // Call the method
        List<Stock> result = stockService.getStocksByPortfolio(portfolioId);

        // Validate the results
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getStockSymbol());
    }

    @Test
    void testSellStock() {
        Long portfolioId = 1L;
        Long stockId = 1L;
        int quantitySell = 5;

        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(portfolioId);
        portfolio.setBalance(10000);

        Stock stock = new Stock();
        stock.setStockSymbol("AAPL");
        stock.setNoOfShares(10);
        stock.setAvgBuyPrice(150.00);
        stock.setHoldings(1500.00);

        CoinDetails coinDetails = new CoinDetails("AAPL", "Apple", "url", 160.00, 1.5, 3.5, 0.5);

        // Mock dependencies
        when(portfolioRepository.findById(portfolioId)).thenReturn(java.util.Optional.of(portfolio));
        when(stockRepository.findById(stockId)).thenReturn(java.util.Optional.of(stock));
        when(stockApiService.getCoinDetailedInfo(stock.getStockSymbol())).thenReturn(coinDetails);
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);
//        when(stockRepository.delete(any(Stock.class))).thenReturn(null);

        // Call the method
        stockService.sellStock(portfolioId, stockId, quantitySell);

        // Validate the results
        verify(stockRepository, times(1)).save(any(Stock.class));
        verify(stockRepository, times(1)).delete(stock);
        verify(emailService, times(1)).sendStockSaleEmail(eq(portfolio.getUser().getEmail()), eq(portfolio.getUser().getFullName()), eq(stock.getStockSymbol()), eq(quantitySell), anyDouble(), anyDouble());
    }
}

