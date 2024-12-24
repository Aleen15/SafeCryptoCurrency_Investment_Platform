package in.projectjwt.main.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.projectjwt.main.entities.CoinData;
import in.projectjwt.main.services.CoinService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@CrossOrigin(origins = "http://localhost:3000",allowCredentials = "true")
@RestController
@RequestMapping("/auth")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;
    
    
    @GetMapping("/coins")
    public List<Map<String, Object>> getCoins() {
        return coinService.getCoins();
    }
    /**
     * Get a paginated list of coins.
     * @throws Exception 
     */
    @GetMapping
    public ResponseEntity<List<CoinData>> getCoinList(@RequestParam(required=false,name="page") Integer page) throws Exception {
        if(page == null) {
        	page =1;
        }
    	List<CoinData> coins = coinService.getCoinList(page);
    	
        return new ResponseEntity<>(coins, HttpStatus.ACCEPTED);
    }
    /**
     * Get market chart data for a specific coin.
     * @throws Exception 
     */
    @GetMapping("/coins/{coinId}/chart")
    public ResponseEntity<JsonNode> getMarketChart(@PathVariable String coinId,
                                                   @RequestParam("days") int days) throws Exception {
        String coins = coinService.getMarketChart(coinId, days);
        JsonNode jsonNode = objectMapper.readTree(coins);

        return ResponseEntity.ok(jsonNode);
    }
    /**
     * Get detailed information about a specific coin.
     * @throws Exception 
     */
    @GetMapping("/coins/details/{coinId}")
    public ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
        String coinDetails = coinService.getCoinDetails(coinId);
        JsonNode jsonNode = objectMapper.readTree(coinDetails);
//
        return ResponseEntity.ok(jsonNode);
    }


    

    /**
     * Search for coins by keyword.
     * @throws Exception 
     */
    
    //working
    @GetMapping("/search")
    public ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws Exception {
        try {
        	String result = coinService.searchCoin(keyword);
            JsonNode jsonNode = objectMapper.readTree(result);

            return ResponseEntity.ok(jsonNode);
        	
        }
        catch(Exception e)
        {
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(objectMapper.createObjectNode().put("error", "Unable to search for coins."));
        }
    	
    }
  //Dashboard page sidebar Top cryptocurrency by marketcap
    @GetMapping("/coins/top10")
    public ResponseEntity<JsonNode> getTop10CoinsByMarketCapRank() {
        try {
            String coins = coinService.getTop10CoinsByMarketCapRank();
            JsonNode jsonNode = objectMapper.readTree(coins);

            return ResponseEntity.ok(jsonNode);
        } catch (Exception e) {
            // Logging error
            System.out.printf("Error fetching top 50 coins by market cap rank: {}", e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(objectMapper.createObjectNode().put("error", "Unable to fetch top 50 coins data"));
        }
    }

    
    //Market page sidebar Top cryptocurrency by marketcap
    @GetMapping("/coins/top50")
    public ResponseEntity<JsonNode> getTop50CoinsByMarketCapRank() {
        try {
            String coins = coinService.getTop50CoinsByMarketCapRank();
            JsonNode jsonNode = objectMapper.readTree(coins);

            return ResponseEntity.ok(jsonNode);
        } catch (Exception e) {
            // Logging error
            System.out.printf("Error fetching top 50 coins by market cap rank: {}", e.getMessage(),e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(objectMapper.createObjectNode().put("error", "Unable to fetch top 50 coins data"));
        }
    }


    //Dasboard page sidebar Top trending coins
    //working
    @GetMapping("/coins/trending")
    public ResponseEntity<JsonNode> getTrendingCoins() {
        try {
            String coins = coinService.getTrendingCoins();
            JsonNode jsonNode = objectMapper.readTree(coins);

            return ResponseEntity.ok(jsonNode);
        } catch (Exception e) {
            // Logging error
            System.out.printf("Error fetching trending coins: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(objectMapper.createObjectNode().put("error", "Unable to fetch trending coins data"));
        }
    }

    
    
}