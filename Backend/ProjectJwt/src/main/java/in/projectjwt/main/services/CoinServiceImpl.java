package in.projectjwt.main.services;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import in.projectjwt.main.entities.CoinData;
import in.projectjwt.main.repositories.CoinRepository;


@Service
public class CoinServiceImpl implements CoinService {
	

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CoinRepository coinRepository;

    private final String BASE_URL = "https://api.coingecko.com/api/v3";
    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd";
    
    @Override
    public List<Map<String, Object>> getCoins() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(COINGECKO_API_URL, List.class);
    }

    @Override
    public List<CoinData> getCoinList(int page) throws Exception {
        String url = BASE_URL + "/coins/markets?vs_currency=usd&per_page=10&page=" + page;
        RestTemplate restTemplate = new RestTemplate();

        try{
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String>("parameters",headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            List<CoinData> coinList = objectMapper.readValue(response.getBody(), new TypeReference<List<CoinData>>() {});

            return coinList;
        }
        catch (HttpClientErrorException | HttpServerErrorException e){
            throw new Exception( e.getMessage());
        }

    }

    @Override
    public String getMarketChart(String coinId, int days) throws Exception {
        String url = BASE_URL + "/coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days;
        RestTemplate restTemplate = new RestTemplate();

        try{
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String>("parameters",headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            return response.getBody();
        }
        catch (HttpClientErrorException | HttpServerErrorException e){
            throw new Exception(e.getMessage());
        }
    }
    
    @Override
    public String getCoinDetails(String coinId) throws Exception {
        String url = BASE_URL + "/coins/" + coinId;
        RestTemplate restTemplate = new RestTemplate();

        try{
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<String>("parameters",headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,entity,String.class);

            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            CoinData coin = new CoinData();
            coin.setId(jsonNode.get("id").asText());
            coin.setName(jsonNode.get("name").asText());
            coin.setSymbol(jsonNode.get("symbol").asText());
            coin.setImage(jsonNode.get("image").get("large").asText());

            JsonNode marketData = jsonNode.get("market_data");

            coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
            coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
            coin.setMarketCapRank(marketData.get("market_cap_rank").asInt());
            coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
            coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
            coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());
            coin.setPriceChange24h(marketData.get("price_change_24h").asDouble());//4:23
            coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
            coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").asLong());
            coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asLong());
            coin.setTotalSupply(marketData.get("total_supply").asLong());

            coinRepository.save(coin);

            return response.getBody();
        }
        catch (HttpClientErrorException | HttpServerErrorException e){
            throw new Exception(e.getMessage());
        }
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
//        return response.getBody();
    }

    @Override
    public CoinData findById(String coinId) throws Exception {
    	Optional<CoinData> optionalCoin = coinRepository.findById(coinId);
        if(optionalCoin.isEmpty())throw new Exception("coin not found");
        return optionalCoin.get();

    }
    @Override
    public String searchCoin(String keyword) throws Exception {
        String url = BASE_URL+"/search?query=" + keyword;
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception("Error while searching for coin: " + e.getMessage(), e);
        }

    }
    

    
    @Override
    public String getTop10CoinsByMarketCapRank() throws Exception {
        String url = BASE_URL + "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10";
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception("Error while fetching top 10 coins: " + e.getMessage(), e);
        }

    }
 
    
    //trending working
    @Override
    public String getTrendingCoins() throws Exception {
        String url = BASE_URL + "/search/trending";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        System.out.println("Fetched trending coins data successfully.");
        return response.getBody();
    }
    
    @Override
    public String getTop50CoinsByMarketCapRank() throws Exception {
        String url = BASE_URL + "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=50";
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new Exception("Error while fetching top 50 coins: " + e.getMessage(), e);
        }

    }
}

