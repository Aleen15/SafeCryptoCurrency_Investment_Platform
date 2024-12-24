package in.projectjwt.main.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.projectjwt.main.entities.CoinDetails;



@Service
public class StockApiService {
	private final RestTemplate restTemplate = new RestTemplate();
	
	// Method to get coin details (ID, name, image, current price, and price changes)
    public Map<String, Object> getCoinDetails(String coinId) {
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Log the full response for debugging purposes
        System.out.println("API Response from getCoinDetails method: " + response.getBody());

        // Returning the response as a Map
        return response.getBody();
    }
    
    public CoinDetails getCoinDetailedInfo(String coinId) {
        Map<String, Object> coinData = getCoinDetails(coinId);

        // Extracting individual fields from the Map
        String id = (String) coinData.get("id");
        String name = (String) coinData.get("name");
        Map<String, String> image = (Map<String, String>) coinData.get("image");
        String imageUrl = image != null ? image.get("large") : null;

        // Extracting market data
        Map<String, Object> marketData = (Map<String, Object>) coinData.get("market_data");
        
        // Handling potential Integer or Double for the price change percentages
        Object priceChange24hObject = marketData.get("price_change_percentage_24h");
        double priceChange24h = (priceChange24hObject instanceof Number) ? ((Number) priceChange24hObject).doubleValue() : 0.0;

        Object priceChangePercentage7dObject = marketData.get("price_change_percentage_7d");
        double priceChangePercentage7d = (priceChangePercentage7dObject instanceof Number) ? ((Number) priceChangePercentage7dObject).doubleValue() : 0.0;

        // Handling price change percentage in 1h
        Object priceChangePercentage1hObject = ((Map<String, Object>) marketData.get("price_change_percentage_1h_in_currency")).get("usd");
        double priceChangePercentage1h = (priceChangePercentage1hObject instanceof Number) ? ((Number) priceChangePercentage1hObject).doubleValue() : 0.0;

        // Extracting current price
        Map<String, Object> currentPriceMap = (Map<String, Object>) marketData.get("current_price");
        double currentPrice = (currentPriceMap != null && currentPriceMap.get("usd") instanceof Number) 
            ? ((Number) currentPriceMap.get("usd")).doubleValue() 
            : 0.0;

        // Print for debugging
        System.out.println(currentPrice + " " + priceChange24h + " " + priceChangePercentage7d + " " + priceChangePercentage1h);

        // Return a CoinDetails object with the extracted information
        return new CoinDetails(id, name, imageUrl, currentPrice, priceChange24h, priceChangePercentage7d, priceChangePercentage1h);
    }

  

}