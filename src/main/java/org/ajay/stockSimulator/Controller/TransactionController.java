package org.ajay.stockSimulator.Controller;

import org.ajay.stockSimulator.DTOs.*;

import org.ajay.stockSimulator.Repo.UserRepo;
import org.ajay.stockSimulator.model.Transaction;

import org.ajay.stockSimulator.model.User;
import org.ajay.stockSimulator.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@CrossOrigin
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserRepo userRepo;
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/greeting")
    public String greeting(){
        return "Hello User you're inside the Transaction controller right now";
    }
    @PostMapping("/buy")
    public ResponseEntity<String> buyStock(@RequestBody BuyStockRequest request, Principal principal) {

        System.out.println("Received request:");
        System.out.println("Symbol: " + request.getStocksymbol());
        System.out.println("Qty: " + request.getQuantity());


        try{
            String username = principal.getName();
            User user = userRepo.findByUsername(username);
            transactionService.buyStock(user,request.getStocksymbol(),request.getQuantity());
            return  new ResponseEntity<>(" purchase successful", HttpStatus.OK);
        }
        catch(RuntimeException e)
        {
            return ResponseEntity.badRequest().body( "purchase failed " +e.getMessage());
        }
    }
    @PostMapping("/sell")
    public ResponseEntity<String> sellStock(@RequestBody SellStockRequest request)
    {
        try{
            transactionService.sellStock(request.getId(),request.getStocksymbol(),request.getQuantity());
            return  new ResponseEntity<>(" selling successful", HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.badRequest().body( "selling failed " +e.getMessage());
        }
    }
    @GetMapping("/history/{id}")
    public ResponseEntity<List<Transaction>> userHistory(@PathVariable Long id){
        List<Transaction>  list = transactionService.getuserHistory(id);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }
    @GetMapping("/active-traders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ActiveTraderDTO>> getActiveTradersToday(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(transactionService.getActiveTradersToday(limit));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RecentTradeDTO>> getRecentTrades(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(transactionService.getRecentTransactions(limit));
    }
    @GetMapping("/top-stocks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TopStockDTO>> getTopStocks(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(transactionService.getTopStocks(limit));
    }

    @GetMapping("/executed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TradersExecutedDTO>> getTradersExecutedToday(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(transactionService.getTradesExecutedToday(limit));
    }

}
