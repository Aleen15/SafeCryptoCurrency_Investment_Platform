package in.projectjwt.main.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import in.projectjwt.main.entities.Portfolio;
import in.projectjwt.main.entities.Stock;
import in.projectjwt.main.entities.User;
import in.projectjwt.main.repositories.PortfolioRepository;
import in.projectjwt.main.repositories.StockRepository;

public class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private PortfolioService portfolioService;

    private Portfolio portfolio;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1);
        user.setFullName("John Doe");

        portfolio = new Portfolio();
        portfolio.setPortfolioId(1L);
        portfolio.setPortfolioName("Tech Portfolio");
        portfolio.setInvestmentAgenda("Technology Stocks");
        portfolio.setBalance(10000.0);
        portfolio.setCreatedAt(LocalDateTime.now());
        portfolio.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testAddPortfolio() {
        // Arrange
        when(authenticationService.findById(1L)).thenReturn(user);
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);

        // Act
        Portfolio createdPortfolio = portfolioService.addPortfolio(1L, portfolio);

        // Assert
        assertNotNull(createdPortfolio);
        assertEquals("Tech Portfolio", createdPortfolio.getPortfolioName());
        assertEquals(10000.0, createdPortfolio.getBalance());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    public void testGetPortfolioById() {
        // Arrange
        when(portfolioRepository.findById(1L)).thenReturn(java.util.Optional.of(portfolio));

        // Act
        Portfolio result = portfolioService.getPortfolioById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Tech Portfolio", result.getPortfolioName());
        assertEquals(10000.0, result.getBalance());
    }

    @Test
    public void testUpdatePortfolio() {
        // Arrange
        Portfolio updatedPortfolio = new Portfolio();
        updatedPortfolio.setPortfolioName("Updated Portfolio");
        updatedPortfolio.setInvestmentAgenda("Updated Agenda");

        when(portfolioRepository.findById(1L)).thenReturn(java.util.Optional.of(portfolio));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(updatedPortfolio);

        // Act
        Portfolio result = portfolioService.updatePortfolio(1L, updatedPortfolio);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Portfolio", result.getPortfolioName());
        assertEquals("Updated Agenda", result.getInvestmentAgenda());
    }

    @Test
    public void testCalculatePortfolioSummary() {
        // Arrange
        when(portfolioRepository.findById(1L)).thenReturn(java.util.Optional.of(portfolio));
        when(stockRepository.findByPortfolio(portfolio)).thenReturn(List.of(new Stock()));

        // Act
        Map<String, Double> summary = portfolioService.calculatePortfolioSummary(1L);

        // Assert
        assertNotNull(summary);
        assertEquals(10000.0, summary.get("totalValue"));
        assertEquals(5000.0, summary.get("totalProfitLoss"));
    }

    @Test
    public void testDeletePortfolio() {
        // Arrange
        when(portfolioRepository.findById(1L)).thenReturn(java.util.Optional.of(portfolio));

        // Act
        portfolioService.deletePortfolio(1L);

        // Assert
        verify(portfolioRepository, times(1)).delete(any(Portfolio.class));
    }
}
