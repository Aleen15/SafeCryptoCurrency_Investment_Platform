package in.projectjwt.main.services;

import java.util.List;
import java.util.Map;

import in.projectjwt.main.entities.CoinData;



public interface CoinService {
    List<CoinData> getCoinList(int page) throws Exception;

    String getMarketChart(String coinId, int days) throws Exception;

    String getCoinDetails(String coinId) throws Exception;

    CoinData findById(String coinId) throws Exception;

    String searchCoin(String keyword) throws Exception;

    String getTop50CoinsByMarketCapRank() throws Exception;

    String getTrendingCoins() throws Exception;

	String getTop10CoinsByMarketCapRank() throws Exception;

	List<Map<String, Object>> getCoins();
}