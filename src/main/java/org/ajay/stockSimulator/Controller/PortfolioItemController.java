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
    @GetMapping("/user")
    public ResponseEntity<List<PortfolioItemDtos>> getPortfolioItemByUser(
            Principal principal) {

        User user = userRepo.findByUsername(principal.getName());

        List<PortfolioItemDtos> items =
                portfolioItemService.getAllPortfolioItemById(user.getUserId());

        if (items.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(items);
    }

    @PostMapping("/user/add")
    public ResponseEntity<?> addPortfolioItem(
            @RequestBody PortfolioItemDTO dto,
            Principal principal) {

        try {
            // 1. Identify user from JWT
            String username = principal.getName();
            User user = userRepo.findByUsername(username);

            // 2. Use INTERNAL userId (not from frontend)
            PortfolioItem createdItem =
                    portfolioItemService.addNewPortfolioItem(
                            user.getUserId(),
                            dto
                    );

            return new ResponseEntity<>(createdItem, HttpStatus.CREATED);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error adding portfolio item: " + e.getMessage());
        }
    }


    @DeleteMapping("/user/{stocksymbol}/delete")
    public ResponseEntity<PortfolioItem> deletePortfolioItem(
            @PathVariable String stocksymbol,
            Principal principal) {

        User user = userRepo.findByUsername(principal.getName());

        PortfolioItem item =
                portfolioItemService.deletePortfioItem(
                        user.getUserId(), stocksymbol
                );

        if (item == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(item, HttpStatus.OK);
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
