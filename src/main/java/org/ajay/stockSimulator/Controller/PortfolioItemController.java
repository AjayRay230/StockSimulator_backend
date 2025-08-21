package org.ajay.stockSimulator.Controller;


import org.ajay.stockSimulator.DTOs.PortfolioItemDTO;
import org.ajay.stockSimulator.DTOs.PortfolioItemDtos;
import org.ajay.stockSimulator.model.PortfolioItem;
import org.ajay.stockSimulator.service.PortfolioItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin
public class PortfolioItemController {
    @Autowired
    private PortfolioItemService portfolioItemService;
    @GetMapping("/greeting")
    public String greeting(){
        return "Hey user you're inside the portfolio! controller";
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PortfolioItemDtos>> getPortfolioItemByUserId(@PathVariable Long userId) {
        List<PortfolioItemDtos> items = portfolioItemService.getAllPortfolioItemById(userId);

        if (items.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/{stocksymbol}")
    public ResponseEntity<PortfolioItem> getAllPortfolioItem(@PathVariable Long userId, @PathVariable String stocksymbol) {
        PortfolioItem portfolioItem = portfolioItemService.getPortfolioBySymbol(userId, stocksymbol);
        return portfolioItem == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(portfolioItem, HttpStatus.OK);

    }
    @PostMapping("/user/{userId}/add")
    public ResponseEntity<?> addPortfolioItem(@PathVariable Long userId, @RequestBody PortfolioItemDTO dto) {

        try{
             PortfolioItem createdItem = portfolioItemService.addNewPortfolioItem(userId,dto);

            return new ResponseEntity<>(createdItem,HttpStatus.CREATED);
        }
      catch (Exception e){
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                  .body("Error adding portfolio item: " + e.getMessage());
      }
    }
    @DeleteMapping("/user/{userId}/{stocksymbol}/delete")
    public ResponseEntity<PortfolioItem> deletePortfolioItem(@PathVariable Long userId, @PathVariable String stocksymbol) {
        PortfolioItem portfolioItem = portfolioItemService.deletePortfioItem(userId, stocksymbol);
        if (portfolioItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        else
            return new ResponseEntity<>(portfolioItem, HttpStatus.OK);
    }
//    @PostMapping("/user/add")
//    public ResponseEntity<PortfolioItem> addPortfolioItem(@RequestBody PortfolioItemDTO dto) {
//        PortfolioItem item = portfolioItemService.addPortfolioItem(dto.getQuantity(),dto.getAverageBuyPrice(),dto.getStockSymbol());
//        return new ResponseEntity<>(item,HttpStatus.CREATED);
//    }
    @GetMapping("/test")
    public String test() {
        return "working";
    }

}
