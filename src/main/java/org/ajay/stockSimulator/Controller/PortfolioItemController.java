package org.ajay.stockSimulator.Controller;


import org.ajay.stockSimulator.DTOs.PortfolioItemDTO;
import org.ajay.stockSimulator.DTOs.PortfolioItemDtos;
import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.PortfolioItem;
import org.ajay.stockSimulator.model.User;
import org.ajay.stockSimulator.service.PortfolioItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/portfolio")
@CrossOrigin
public class PortfolioItemController {
    @Autowired
    private PortfolioItemService portfolioItemService;
    @Autowired
    private UserRepo userRepo;
    @GetMapping("/greeting")
    public String greeting(){
        return "Hey user you're inside the portfolio! controller";
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PortfolioItemDtos>> getPortfolioItemByUserId(
            @PathVariable Long userId,
            Principal principal) {

        String username = principal.getName();
        User user = userRepo.findByUsername(username);

        if (!user.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<PortfolioItemDtos> items =
                portfolioItemService.getAllPortfolioItemById(userId);

        if (items.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/user/{userId}/{stocksymbol}")
    public ResponseEntity<PortfolioItem> getAllPortfolioItem(@PathVariable Long userId, @PathVariable String stocksymbol) {
        PortfolioItem portfolioItem = portfolioItemService.getPortfolioBySymbol(userId, stocksymbol);
        return portfolioItem == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) : new ResponseEntity<>(portfolioItem, HttpStatus.OK);

    }
    @PostMapping("/user/{userId}/add")
    public ResponseEntity<?> addPortfolioItem(
            @PathVariable Long userId,
            @RequestBody PortfolioItemDTO dto,
            Principal principal) {

        String username = principal.getName();
        User user = userRepo.findByUsername(username);

        if (!user.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            PortfolioItem createdItem =
                    portfolioItemService.addNewPortfolioItem(userId, dto);
            return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error adding portfolio item: " + e.getMessage());
        }
    }
    @DeleteMapping("/user/{userId}/{stocksymbol}/delete")
    public ResponseEntity<PortfolioItem> deletePortfolioItem(
            @PathVariable Long userId,
            @PathVariable String stocksymbol,
            Principal principal) {

        String username = principal.getName();
        User user = userRepo.findByUsername(username);

        if (!user.getUserId().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        PortfolioItem portfolioItem =
                portfolioItemService.deletePortfioItem(userId, stocksymbol);

        if (portfolioItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
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
