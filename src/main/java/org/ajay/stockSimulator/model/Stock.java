package org.ajay.stockSimulator.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Stock {

    @Id
    private String symbol;

    private String companyname;

    private BigDecimal currentprice;

    private BigDecimal changepercent;

    private LocalDateTime lastupdate;
}
