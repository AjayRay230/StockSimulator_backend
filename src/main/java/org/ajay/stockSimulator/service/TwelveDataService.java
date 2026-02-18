package org.ajay.stockSimulator.service;

import org.ajay.stockSimulator.model.Stock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TwelveDataService {

    @Value("${twelvedata.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // ================================
    // Fetch FULL stock (used for search/buy)
    // ================================
    public Stock fetchFromTwelve(String query) {

        String normalized = query.trim().toUpperCase();

        String url = "https://api.twelvedata.com/symbol_search?symbol="
                + normalized + "&apikey=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        if (response == null || response.get("data") == null) {
            return null;
        }

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.get("data");

        if (data.isEmpty()) return null;

        Map<String, Object> bestMatch = data.get(0);

        String symbol = (String) bestMatch.get("symbol");
        String name = (String) bestMatch.get("instrument_name");

        if (symbol == null || !symbol.matches("^[A-Z]{1,5}$")) {
            return null; // prevent corrupted symbols like 4NVDA
        }

        // Fetch real price (critical fix)
        BigDecimal livePrice = fetchLivePrice(symbol);

        if (livePrice == null || livePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return null; // do NOT create zero-price stocks
        }

        Stock stock = new Stock();
        stock.setSymbol(symbol.toUpperCase());
        stock.setCompanyname(name);
        stock.setCurrentprice(livePrice);
        stock.setChangepercent(BigDecimal.ZERO); // unchanged behavior
        stock.setLastupdate(LocalDateTime.now());

        return stock;
    }

    // ================================
    // Fetch suggestions (autocomplete only)
    // ================================
    public List<Stock> fetchSuggestionsFromTwelve(String query) {

        String normalized = query.trim().toUpperCase();

        String url = "https://api.twelvedata.com/symbol_search?symbol="
                + normalized + "&apikey=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        if (response == null || response.get("data") == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.get("data");

        List<Stock> stocks = new ArrayList<>();

        for (Map<String, Object> item : data) {

            if (!"Common Stock".equals(item.get("instrument_type"))) {
                continue;
            }

            String symbol = (String) item.get("symbol");
            String name = (String) item.get("instrument_name");

            if (symbol == null || !symbol.matches("^[A-Z]{1,5}$")) {
                continue; // prevent invalid symbols
            }

            Stock stock = new Stock();
            stock.setSymbol(symbol.toUpperCase());
            stock.setCompanyname(name);

            // IMPORTANT: do NOT assign zero price
            stock.setCurrentprice(null);
            stock.setChangepercent(BigDecimal.ZERO);
            stock.setLastupdate(LocalDateTime.now());

            stocks.add(stock);
        }

        return stocks;
    }

    // ================================
    // Live price fetcher (new helper)
    // ================================
    BigDecimal fetchLivePrice(String symbol) {

        String url = "https://api.twelvedata.com/price?symbol="
                + symbol + "&apikey=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        if (response == null || response.get("price") == null) {
            return null;
        }

        try {
            return new BigDecimal(response.get("price").toString());
        } catch (Exception e) {
            return null;
        }
    }
}
