package org.ajay.stockSimulator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String symbol;

    private String companyname;

    private BigDecimal currentprice;

    private BigDecimal changepercent;

    private LocalDateTime lastupdate;
    @Column(name = "shares_outstanding")
    private BigDecimal sharesOutstanding;
}
