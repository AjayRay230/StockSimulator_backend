package org.ajay.stockSimulator.DTOs;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class BuyStockRequest {
    private String stocksymbol;
    private int quantity;

}
