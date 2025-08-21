package org.ajay.stockSimulator.Controller;

import org.ajay.stockSimulator.DTOs.WatchlistRequest;
import org.ajay.stockSimulator.model.Watchlist;
import org.ajay.stockSimulator.service.WatchListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin
public class WatchListController {
    @Autowired
    private WatchListService watchListService;
    @GetMapping("/greeting")
    public String greeting(){
        return "Hello User you're inside the WatchList controller right now";
    }
    @PostMapping("/add")
    public ResponseEntity<String> AddToWatchList(@RequestBody WatchlistRequest watchlistRequest) {
        try{
            watchListService.addToWatchlist(watchlistRequest.getStocksymbol(),watchlistRequest.getUserId());
            return ResponseEntity.ok().body("Successfully added stock to watchlist");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body( "Failed to add to the watchlist"+e.getMessage());
        }
    }
    @PostMapping("/remove")
    public ResponseEntity<String> RemoveFromWatchList(@RequestBody WatchlistRequest watchlistRequest) {
        try{
            watchListService.removeFromWatchlist(watchlistRequest.getUserId(),watchlistRequest.getStocksymbol());
            return ResponseEntity.ok().body("Successfully removed stock from watchlist");
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Failed to remove from the watchlist"+e.getMessage());
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<Watchlist>> getWatchListByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(watchListService.getUserWatchlist(userId));
    }
}
