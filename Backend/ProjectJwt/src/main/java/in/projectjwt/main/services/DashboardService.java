package in.projectjwt.main.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service  // Ensure that Spring knows this is a service class
public class DashboardService {

    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);

    @Value("${news.api.url}")
    private String newsApiUrl;

    @Value("${news.api.key}")
    private String newsApiKey;

    // New method to fetch cryptocurrency-related news using NewsAPI
    public JsonNode getCryptoNews() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", newsApiKey);  // Add the News API key header
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Create the full URL with query parameters
            String fullUrl = newsApiUrl + "?q=cryptocurrency&apiKey=" + newsApiKey;

            // Log the API URL and headers for debugging
            logger.info("Request URL: {}", fullUrl);
            logger.info("Request Headers: {}", headers);

            // Sending the GET request to News API
            ResponseEntity<String> response = restTemplate.exchange(
                    fullUrl,  // Use the full URL with query parameters
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Log response status and body for debugging
            logger.info("Response Status Code: {}", response.getStatusCode());
            logger.info("Response Body: {}", response.getBody());

            // Handle potential 403 error explicitly
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                logger.error("403 Forbidden Error: Check API key and permissions.");
                throw new RuntimeException("403 Forbidden: Access denied by NewsAPI. Check API key and permissions.");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.getBody());
            JsonNode articles = rootNode.get("articles"); // Fetch the "articles" node from the response

            // Create a JsonNode to store the news articles
            JsonNode cryptoNews = mapper.createArrayNode();

            // Loop through the articles and extract the relevant data
            int count = 0;
            for (JsonNode articleNode : articles) {
                if (count >= 5) break; // Limit to 5 articles

                ((ArrayNode) cryptoNews).add(mapper.createObjectNode()
                        .put("title", articleNode.path("title").asText("No title available"))
                        .put("description", articleNode.path("description").asText("No description available"))
                        .put("url", articleNode.path("url").asText(""))
                        .put("publishedAt", articleNode.path("publishedAt").asText(""))
                        .put("source", articleNode.path("source").path("name").asText("Unknown source"))
                );
                count++;
            }

            // Return the cryptocurrency news articles data, limited to 5 articles
            return cryptoNews;

        } catch (Exception e) {
            logger.error("Error fetching cryptocurrency news: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch cryptocurrency news from NewsAPI: " + e.getMessage(), e);
        }
    }
}