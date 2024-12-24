package in.projectjwt.main.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoinDetails {
    private String id;
    private String name;
    private String imageUrl;

    @JsonProperty("current_price")
    private double currentPrice;
    
    @JsonProperty("price_change_percentage_24h")
    private double priceChange24h;

    private double priceChangePercentage7d;
    private double priceChangePercentage1h;

    // Constructor
    public CoinDetails(String id, String name, String imageUrl, double currentPrice, double priceChange24h, double priceChangePercentage7d, double priceChangePercentage1h) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.currentPrice = currentPrice;
        this.priceChange24h = priceChange24h;
        this.priceChangePercentage7d = priceChangePercentage7d;
        this.priceChangePercentage1h = priceChangePercentage1h;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPriceChange24h() {
        return priceChange24h;
    }

    public double getPriceChangePercentage7d() {
        return priceChangePercentage7d;
    }

    public double getPriceChangePercentage1h() {
        return priceChangePercentage1h;
    }
}
