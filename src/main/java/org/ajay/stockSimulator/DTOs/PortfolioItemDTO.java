package org.ajay.stockSimulator.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class PortfolioItemDTO {
    private String stocksymbol;

    private int quantity;
    private double averagebuyprice;

}
