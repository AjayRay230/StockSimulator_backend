package org.ajay.stockSimulator.Repo;

import org.ajay.stockSimulator.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepo extends JpaRepository<Stock,String> {
    List<Stock> findByCurrentprice(BigDecimal currentprice);




    // For partial and case-insensitive match
    @Query("""
   SELECT s FROM Stock s
   WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :query, '%'))
      OR LOWER(s.companyname) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    List<Stock> searchStockLike(@Param("query") String query, Pageable pageable);

    Stock findBySymbolIgnoreCaseOrCompanynameIgnoreCase(String symbol, String companyname);

   Optional<Stock> findBySymbol(String symbol);
    Optional<Stock> findBySymbolIgnoreCase(String symbol);

    List<Stock> findByCompanynameContainingIgnoreCase(String companyname);
    List<Stock> findBySymbolContainingIgnoreCase(String symbol);


}
