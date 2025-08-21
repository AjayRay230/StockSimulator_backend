package org.ajay.stockSimulator.Controller;

import org.ajay.stockSimulator.model.Stock;
import org.ajay.stockSimulator.model.StockPrice;
import org.ajay.stockSimulator.service.StockPriceService;
import org.ajay.stockSimulator.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@Controller
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
    public ResponseEntity<List<Stock>> getAllStocksByPrice(@RequestBody Stock stock){
        return ResponseEntity.ok(stockService.getAllStocksWithPrice(stock.getCurrentprice()));

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
    public ResponseEntity<String> simulateStock(){
        stockService.simulatePrice();
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
     @PostMapping("/add")
    public ResponseEntity<StockPrice> addStock(@RequestBody StockPrice stockPrice){
            StockPrice stock = stockPriceservice.AddStockWithSymbol(stockPrice);
            if(stock == null){
                return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            else {
                return new ResponseEntity<>(stockPrice,HttpStatus.OK);
            }
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
