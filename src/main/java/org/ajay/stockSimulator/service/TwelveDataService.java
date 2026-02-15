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

    public Stock fetchFromTwelve(String query) {

        String url = "https://api.twelvedata.com/symbol_search?symbol="
                + query + "&apikey=" + apiKey;

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

        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setCompanyname(name);
        stock.setCurrentprice(BigDecimal.ZERO);
        stock.setChangepercent(BigDecimal.ZERO);
        stock.setLastupdate(LocalDateTime.now());

        return stock;
    }
    public List<Stock> fetchSuggestionsFromTwelve(String query) {

        String url = "https://api.twelvedata.com/symbol_search?symbol="
                + query + "&apikey=" + apiKey;

        Map response = restTemplate.getForObject(url, Map.class);

        if (response == null || response.get("data") == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.get("data");

        List<Stock> stocks = new ArrayList<>();

        for (Map<String, Object> item : data) {

            // Filter only real stocks
            if (!"Common Stock".equals(item.get("instrument_type"))) {
                continue;
            }

            String symbol = (String) item.get("symbol");
            String name = (String) item.get("instrument_name");

            Stock stock = new Stock();
            stock.setSymbol(symbol.toUpperCase());
            stock.setCompanyname(name);
            stock.setCurrentprice(BigDecimal.ZERO);
            stock.setChangepercent(BigDecimal.ZERO);
            stock.setLastupdate(LocalDateTime.now());

            stocks.add(stock);
        }

        return stocks;
    }
}