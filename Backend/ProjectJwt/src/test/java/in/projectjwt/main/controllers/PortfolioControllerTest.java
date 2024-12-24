package in.projectjwt.main.controllers;

import static org.mockito.Mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import in.projectjwt.main.entities.Portfolio;
import in.projectjwt.main.services.PortfolioService;

public class PortfolioControllerTest {

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private PortfolioController portfolioController;

    private MockMvc mockMvc;
    private Portfolio portfolio;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(portfolioController).build();

        portfolio = new Portfolio();
        portfolio.setPortfolioId(1L);
        portfolio.setPortfolioName("Tech Portfolio");
        portfolio.setInvestmentAgenda("Technology Stocks");
        portfolio.setBalance(10000.0);
    }

    @Test
    public void testCreatePortfolio() {
        // Arrange
        when(portfolioService.addPortfolio(anyLong(), any(Portfolio.class))).thenReturn(portfolio);

        // Act
        ResponseEntity<?> response = portfolioController.createPortfolio(portfolio, 1L);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetPortfolioById() {
        // Arrange
        when(portfolioService.getPortfolioById(1L)).thenReturn(portfolio);

        // Act
        ResponseEntity<Portfolio> response = portfolioController.getPortfolioById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tech Portfolio", response.getBody().getPortfolioName());
    }

    @Test
    public void testGetUserPortfolios() {
        // Arrange
        when(portfolioService.getPortfoliosByUser(1L)).thenReturn(List.of(portfolio));

        // Act
        List<Portfolio> portfolios = portfolioController.getUserPortfolios(1L);

        // Assert
        assertNotNull(portfolios);
        assertEquals(1, portfolios.size());
        assertEquals("Tech Portfolio", portfolios.get(0).getPortfolioName());
    }

    @Test
    public void testUpdatePortfolio() {
        // Arrange
        Portfolio updatedPortfolio = new Portfolio();
        updatedPortfolio.setPortfolioName("Updated Portfolio");

        when(portfolioService.updatePortfolio(1L, updatedPortfolio)).thenReturn(updatedPortfolio);

        // Act
        ResponseEntity<Portfolio> response = portfolioController.updatePortfolio(1L, updatedPortfolio);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Portfolio", response.getBody().getPortfolioName());
    }

    @Test
    public void testDeletePortfolio() {
        // Arrange
        doNothing().when(portfolioService).deletePortfolio(1L);

        // Act
        ResponseEntity<Void> response = portfolioController.deletePortfolio(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(portfolioService, times(1)).deletePortfolio(1L);
    }
}

