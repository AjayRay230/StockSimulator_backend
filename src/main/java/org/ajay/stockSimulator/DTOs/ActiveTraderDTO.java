package org.ajay.stockSimulator.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveTraderDTO {
    private long userId;
    private String userName;
    private String email;
    private long tradeCount;
}
