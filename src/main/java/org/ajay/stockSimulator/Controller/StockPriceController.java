package org.ajay.stockSimulator.Controller;

import org.ajay.stockSimulator.Repo.StockRepo;
import org.ajay.stockSimulator.model.StockPrice;
import org.ajay.stockSimulator.service.StockPriceService;
import org.ajay.stockSimulator.service.TwelveDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock-price")
@CrossOrigin
public class StockPriceController {
    @Autowired
    private StockPriceService stockPriceService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StockRepo stockRepo;

    @Value("${twelvedata.api.key}")
    private String API_key;



    @GetMapping("/{symbol}")
    public ResponseEntity<List<StockPrice>> getStockPrice(@PathVariable("symbol") String stocksymbol) {
        List<StockPrice> prices  = stockPriceService.getPricesBySymbolOrderByTimeAsc(stocksymbol);
        return prices.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(prices);
    }
    @GetMapping("/price")
    public ResponseEntity<List<Map<String, Object>>> getCandles(@RequestParam  String stocksymbol, @RequestParam(required = false,defaultValue ="1D") String range) {
//        if (!symbol.contains(".")) {
//            symbol += ".BSE"; // or .NS depending on exchange
//        }
        String interval = "";
        String outputsize = "";
        switch(range){
            case "1D":interval = "1min";outputsize = "390" ;break;
            case "5D":interval = "5min";outputsize = "390" ;break;
            case "1M":interval = "1day";outputsize = "30" ;break;
            case "1Y":interval = "1week";outputsize = "52" ;break;
            case "MAX":interval = "1month";outputsize = "120" ;break;
            default:
                interval = "1min";
                outputsize = "390";
                break;

        }
        String url = String.format(
                "https://api.twelvedata.com/time_series?symbol=%s&interval=%s&outputsize=%s&apikey=%s",
                stocksymbol, interval, outputsize, API_key
        );


        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map body = response.getBody();
        if (body != null && body.containsKey("values")) {
            List<Map<String, String>> values = (List<Map<String, String>>) body.get("values");
            List<Map<String, ? extends Serializable>> sparkline = values.stream().map(v -> Map.of(

                    "date", v.get("datetime"),
                    "openPrice", Double.parseDouble(v.get("open")),
                    "highPrice", Double.parseDouble(v.get("high")),
                    "lowPrice", Double.parseDouble(v.get("low")),
                    "closePrice", Double.parseDouble(v.get("close"))
            )).collect(Collectors.toList());
            List<Double> volumeList = values.stream().map(v->Double.parseDouble(v.get("volume"))).collect(Collectors.toList());
             double currentPrice = (double) sparkline.getFirst().get("closePrice");
             double firstPrice = (double) sparkline.getLast().get("closePrice");
             double change  = currentPrice - firstPrice;
             double changePercent = (change)/firstPrice*100;
             double highPrice =sparkline.stream().mapToDouble(s-> (double) s.get("highPrice")).max().orElse(0);
             double lowPrice = sparkline.stream().mapToDouble(s-> (double) s.get("lowPrice")).min().orElse(0);
            String quoteUrl = String.format(
                    "https://api.twelvedata.com/quote?symbol=%s&apikey=%s",
                    stocksymbol, API_key);
            ResponseEntity<Map> res =  restTemplate.getForEntity(quoteUrl, Map.class);
           Map<String,Object> map = res.getBody();
            double openPrice = Double.parseDouble((String) map.get("open"));

            double previousClose = Double.parseDouble((String) map.get("previous_close"));
           Map<String,String> week52 = (Map<String, String>) map.get("fifty_two_week");
           double fiftyTwoWeekHigh = Double.parseDouble((String) week52.get("high"));
           double fiftyTwoWeekLow = Double.parseDouble((String) week52.get("low"));


//               String peRatio = (String) map.get("pe_ratio");
//                String dividendYield = (String) map.get("dividend");
            Map<String, Object> result = Map.ofEntries(
                    Map.entry("currentPrice", currentPrice),
                    Map.entry("firstPrice", firstPrice),
                    Map.entry("change", change),
                    Map.entry("changePercent", changePercent),
                    Map.entry("highPrice", highPrice),
                    Map.entry("lowPrice", lowPrice),
                    Map.entry("sparkline", sparkline),
                    Map.entry("trend", change >= 0 ? "up" : "down"),
                    Map.entry("volume", volumeList),
                    Map.entry("previousClose", previousClose),
                    Map.entry("fiftyTwoWeekHigh", fiftyTwoWeekHigh),
                    Map.entry("fiftyTwoWeekLow", fiftyTwoWeekLow),
                    Map.entry("openPrice",openPrice)
//                    Map.entry("marketCap", marketCap),
//                    Map.entry("peRatio", peRatio),
//                    Map.entry("dividendYield", dividendYield)
            );
            return ResponseEntity.ok(List.of(result));
        }
        return ResponseEntity.badRequest().body(List.of(Map.of("error", "No candle data")));
    }

    private Map<String, Object> buildEmptyResponse(String symbol) {
        return Map.of(
                "meta", Map.of(
                        "symbol", symbol,
                        "companyname", symbol,
                        "currentPrice", 0,
                        "change", 0,
                        "changePercent", 0,
                        "trend", "neutral"
                ),
                "data", List.of()
        );
    }


    @Cacheable(
            value = "closingPrice",
            key = "#stocksymbol + '_' + #range"
    )
    @GetMapping("/closing-price")
    public Map<String, Object> getClosingPrice(
            @RequestParam String stocksymbol,
            @RequestParam(defaultValue = "1D") String range) {

        try {

            String interval;
            int outputSize;

            switch (range) {
                case "5D" -> { interval = "5min"; outputSize = 300; }
                case "1M" -> { interval = "1day"; outputSize = 30; }
                case "1Y" -> { interval = "1week"; outputSize = 52; }
                case "MAX" -> { interval = "1month"; outputSize = 120; }
                default -> { interval = "1min"; outputSize = 390; }
            }

            String url = String.format(
                    "https://api.twelvedata.com/time_series?symbol=%s&interval=%s&outputsize=%d&apikey=%s",
                    stocksymbol,
                    interval,
                    outputSize,
                    API_key
            );

            ResponseEntity<Map> response =
                    restTemplate.getForEntity(url, Map.class);

            Map<String, Object> body = response.getBody();
            System.out.println("TD Response: " + body);
            if (body == null || !body.containsKey("values")) {
                return buildEmptyResponse(stocksymbol);
            }

            List<Map<String, String>> values =
                    (List<Map<String, String>>) body.get("values");

            if (values.isEmpty()) {
                return buildEmptyResponse(stocksymbol);
            }

            Map<String, String> latest = values.get(0);
            Map<String, String> prev =
                    values.size() > 1 ? values.get(1) : latest;

            double close = Double.parseDouble(latest.get("close"));
            double prevClose = Double.parseDouble(prev.get("close"));

            double change = close - prevClose;
            double changePercent =
                    prevClose != 0 ? (change / prevClose) * 100 : 0;

            String trend = change >= 0 ? "up" : "down";

            List<Map<String, Object>> ohlcData =
                    values.stream()
                            .map(v -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("timestamp", v.get("datetime"));
                                map.put("openPrice", Double.parseDouble(v.get("open")));
                                map.put("highPrice", Double.parseDouble(v.get("high")));
                                map.put("lowPrice", Double.parseDouble(v.get("low")));
                                map.put("closePrice", Double.parseDouble(v.get("close")));
                                map.put("volume",
                                        v.containsKey("volume")
                                                ? Double.parseDouble(v.get("volume"))
                                                : 0.0);
                                return map;
                            })
                            .collect(Collectors.toList());


            return Map.of(
                    "meta", Map.of(
                            "symbol", stocksymbol,
                            "companyname", stocksymbol,
                            "currentPrice", close,
                            "change", change,
                            "changePercent", changePercent,
                            "timestamp", latest.get("datetime"),
                            "trend", trend
                    ),
                    "data", ohlcData
            );

        } catch (Exception e) {
            return buildEmptyResponse(stocksymbol);
        }
    }



    @GetMapping("/batch-live")
    public ResponseEntity<?> getBatchLive(@RequestParam List<String> symbols) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (String symbol : symbols) {

            stockRepo.findById(symbol.trim().toUpperCase())
                    .ifPresent(stock -> {

                        Map<String, Object> stockData = new HashMap<>();
                        stockData.put("stocksymbol", stock.getSymbol());
                        stockData.put("companyname", stock.getCompanyname());
                        stockData.put("price", stock.getCurrentprice());
                        stockData.put("change", 0);
                        stockData.put("percentChange", stock.getChangepercent());

                        result.add(stockData);
                    });
        }

        return ResponseEntity.ok(result);
    }


}
