package org.ajay.stockSimulator.Controller;

import org.ajay.stockSimulator.model.Stock;
import org.ajay.stockSimulator.model.StockPrice;
import org.ajay.stockSimulator.service.StockPriceService;
import org.ajay.stockSimulator.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;



@RestController
@RequestMapping("/api/stock")
@CrossOrigin
public class StockController {
    @Autowired
    private StockService stockService;
    @Autowired
    private StockPriceService stockPriceservice;
    @GetMapping("/greeting")
    public String greeting(){
        return "Hello User you're inside the Stock controller right now";
    }
    @PostMapping("/by-price")
    public ResponseEntity<List<Stock>> getAllStocksByPrice(
            @RequestParam("price") double price) {

        return ResponseEntity.ok(
                stockService.getAllStocksWithPrice(BigDecimal.valueOf(price))
        );
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<Stock> getStockBySymbol(@PathVariable  String symbol){
        Stock stock = stockService.getStockWithSymbol(symbol);
        if(stock == null){
            return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return new  ResponseEntity<>(stock,HttpStatus.OK);
        }


    }
    @PostMapping("/simulate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> simulateStock() {
        stockService.simulatePrice();
        return ResponseEntity.ok("Success");
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StockPrice> addStock(@RequestBody StockPrice stockPrice) {

        StockPrice stock = stockPriceservice.AddStockWithSymbol(stockPrice);

        if (stock == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(stock);
    }

     @GetMapping("/symbol")
     public ResponseEntity<List<Stock>> getAllStocks(){
        List<Stock> list = stockService.getAllStocks();
        if(list == null){
            return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(list,HttpStatus.OK);
        }
     }
     @GetMapping("/suggestions")
    public ResponseEntity<List<Stock>> SearchStock(@RequestParam String query)
     {
         List<Stock> stock  = stockService.SearchStock(query);
         if(stock == null){
             return new  ResponseEntity<>(HttpStatus.NOT_FOUND);

         }
         else  {
             return new ResponseEntity<>(stock,HttpStatus.OK);
         }
     }
     @GetMapping("/search")
    public ResponseEntity<Stock> searchStock(@RequestParam("query") String query){
        Stock stock = stockService.findStockBySymbolOrCompanyName(query);
        if(stock == null){
            return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(stock,HttpStatus.OK);
        }
     }
}
