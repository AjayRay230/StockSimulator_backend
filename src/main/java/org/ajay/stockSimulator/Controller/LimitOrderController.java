package org.ajay.stockSimulator.Controller;
import org.ajay.stockSimulator.model.*;
import org.ajay.stockSimulator.service.LimitOrderService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/limit-order")
public class LimitOrderController {

    private final LimitOrderService service;

    public LimitOrderController(LimitOrderService service) {
        this.service = service;
    }

    @PostMapping
    public LimitOrder placeOrder(
            @RequestParam String stockSymbol,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal targetPrice,
            @RequestParam TransactionType type,
            Principal principal
    ) {

        return service.placeLimitOrder(
                principal.getName(),  // secure username
                stockSymbol,
                quantity,
                targetPrice,
                type
        );
    }
    @DeleteMapping("/{id}")
    public void cancelOrder(@PathVariable Long id,
                            @RequestParam String username) {
        service.cancelOrder(id, username);
    }
    @GetMapping("/book/{symbol}")
    public List<LimitOrder> getBook(@PathVariable String symbol) {
        return service.getOrderBook(symbol);
    }
}