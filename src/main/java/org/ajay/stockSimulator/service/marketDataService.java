package org.ajay.stockSimulator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class marketDataService {
     @Autowired
    private  RestTemplate restTemplate ;
    private  final String API_KEY = "b391d0af135c4e9b8e2cd8fe6842be62";

    public  List<Map<String, Object>> getIntradayPrices(String symbol) {
        String url = String.format(
                "https://api.twelvedata.com/time_series?symbol=%s&interval=1min&outputsize=30&apikey=%s",
                symbol, API_KEY
        );

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        System.out.println(response);

        return (List<Map<String, Object>>) response.get("values");
    }
}
