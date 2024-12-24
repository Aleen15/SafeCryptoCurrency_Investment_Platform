package in.projectjwt.main.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.projectjwt.main.entities.Stock;
import in.projectjwt.main.services.StockService;




@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/auth/stock")
public class StockController {

	@Autowired
	private StockService stockService;
	@GetMapping("/getstock/{portfolioId}")
	public ResponseEntity<List<Stock>> getStocksByPortfolio(@PathVariable Long portfolioId) {
		List<Stock> stocks = stockService.getStocksByPortfolio(portfolioId);
		return ResponseEntity.ok(stocks);
	}
	
	@GetMapping("/coins")
    public List<Map<String, Object>> getCoins() {
        return stockService.getCoins();
    }
	
	@PostMapping("/{portfolioId}/buy-stock")
	public ResponseEntity<Stock> buyStock(@PathVariable Long portfolioId,@RequestParam String stockSymbol,@RequestParam int noOfShares) { // @RequestParam String
																								// stockSymbol,@RequestParam
																								// int noOfShares

		return ResponseEntity.ok(stockService.buyStock(portfolioId, stockSymbol,noOfShares));
	}

	@PostMapping("/sell-stock/{portfolioId}")
	public ResponseEntity<String> sellStock2(@PathVariable Long portfolioId, 
	                                        @RequestParam Long stockId, 
	                                        @RequestParam int quantitySell) {
	    try {
	        stockService.sellStock(portfolioId, stockId, quantitySell);
	        return ResponseEntity.ok("Stock sold successfully");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}
}
	