package org.ajay.stockSimulator.Controller;

import org.ajay.stockSimulator.DTOs.WatchlistRequest;
import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.User;
import org.ajay.stockSimulator.model.Watchlist;
import org.ajay.stockSimulator.service.WatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin
public class WatchListController {
    @Autowired
    private WatchListService watchListService;
    @Autowired
    private UserRepo userRepo;

    @GetMapping("/greeting")
    public String greeting(){
        return "Hello User you're inside the WatchList controller right now";
    }
    @PostMapping("/add")
    public ResponseEntity<String> AddToWatchList(
            @RequestBody WatchlistRequest watchlistRequest,
            Principal principal) {

        try {
            String username = principal.getName();
            User user = userRepo.findByUsername(username);

            watchListService.addToWatchlist(
                    watchlistRequest.getStocksymbol(),
                    user.getUserId()
            );

            return ResponseEntity.ok("Successfully added stock to watchlist");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to add to the watchlist " + e.getMessage());
        }
    }


    @PostMapping("/remove")
    public ResponseEntity<String> RemoveFromWatchList(
            @RequestBody WatchlistRequest watchlistRequest,
            Principal principal) {

        try {
            String username = principal.getName();
            User user = userRepo.findByUsername(username);

            watchListService.removeFromWatchlist(
                    user.getUserId(),
                    watchlistRequest.getStocksymbol()
            );

            return ResponseEntity.ok("Successfully removed stock from watchlist");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to remove from the watchlist " + e.getMessage());
        }
    }
    @GetMapping("/me")
    public ResponseEntity<List<Watchlist>> getMyWatchlist(Principal principal) {

        String username = principal.getName();
        User user = userRepo.findByUsername(username);

        return ResponseEntity.ok(
                watchListService.getUserWatchlist(user.getUserId())
        );
    }



}
