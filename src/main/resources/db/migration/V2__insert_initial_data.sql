INSERT INTO users (username, firstname,lastname,password, email, amount, role)

VALUES (
           'ajay',
           'Ajay',
           'Roy',
           '$2a$12$8bE6Kp13ySFLAObO7WG6XO65/6VAf5VO6dpY1Wbcyad1IeaYUq/ES',
           'ajay@example.com',
           100000.00,
           'USER'
       ),
       ('admin','Admin','Sir', '$2a$12$9dxWJKw16MxhaiuoL184Ou3AxxAe.VBpVUE3byIXMnuhOsycA1cIq', 'admin@example.com', 100000.00, 'ADMIN'),

        ('ajayroy','AJAY','ROY', '$2a$12$lNnrPojpUGBi7ShsZEY6weo260eN6XspeYqjMUWzNuQtBpxSAH3/m','AjayRy7439@gmail.com',1000000,'ADMIN');
-- STOCK DATA
INSERT INTO stock (symbol, companyname, currentprice, changepercent, lastupdate) VALUES
                                                                                     ('AAPL', 'Apple Inc.', 197.20, 1.23, CURRENT_TIMESTAMP),
                                                                                     ('GOOGL', 'Alphabet Inc.', 2800.50, 0.78, CURRENT_TIMESTAMP),
                                                                                     ('MSFT', 'Microsoft Corp.', 325.10, 1.01, CURRENT_TIMESTAMP),
                                                                                     ('TSLA', 'Tesla Inc.', 699.60, -0.45, CURRENT_TIMESTAMP),
                                                                                     ('AMZN', 'Amazon.com Inc.', 3450.25, 2.00, CURRENT_TIMESTAMP),
                                                                                     ('DIS', 'Walt Disney Co.', 89.50, 0.41, CURRENT_TIMESTAMP),
                                                                                     ('SBUX', 'Starbucks Corp.', 99.50, 0.15, CURRENT_TIMESTAMP),
                                                                                     ('NKE', 'Nike Inc.', 108.00, 0.20, CURRENT_TIMESTAMP),
                                                                                     ('PFE', 'Pfizer Inc.', 35.60, -0.12, CURRENT_TIMESTAMP),
                                                                                     ('MRK', 'Merck & Co.', 111.80, 0.30, CURRENT_TIMESTAMP),
                                                                                     ('JNJ', 'Johnson & Johnson', 161.20, 0.10, CURRENT_TIMESTAMP),
                                                                                     ('WMT', 'Walmart Inc.', 158.40, 0.08, CURRENT_TIMESTAMP),
                                                                                     ('TGT', 'Target Corp.', 130.00, 0.11, CURRENT_TIMESTAMP),
                                                                                     ('COST', 'Costco Wholesale Corp.', 555.00, 0.22, CURRENT_TIMESTAMP),
                                                                                     ('CVS', 'CVS Health Corp.', 68.90, -0.09, CURRENT_TIMESTAMP),
                                                                                     ('MMM', '3M Co.', 103.00, 0.07, CURRENT_TIMESTAMP),
                                                                                     ('CAT', 'Caterpillar Inc.', 274.00, 0.13, CURRENT_TIMESTAMP),
                                                                                     ('XOM', 'Exxon Mobil Corp.', 105.20, -0.05, CURRENT_TIMESTAMP),
                                                                                     ('CVX', 'Chevron Corp.', 156.50, 0.18, CURRENT_TIMESTAMP),
                                                                                     ('TSM', 'Taiwan Semiconductor Mfg.', 136.00, 0.60, CURRENT_TIMESTAMP),
                                                                                     ('BIDU', 'Baidu Inc.', 124.00, 0.33, CURRENT_TIMESTAMP);

-- STOCK PRICES
INSERT INTO stock_price (symbol, open_price, close_price, high_price, low_price, timestamp) VALUES
                                                                                                ('AAPL', 195.50, 197.20, 198.00, 194.00, CURRENT_TIMESTAMP),
                                                                                                ('GOOGL', 2785.00, 2800.50, 2820.00, 2770.00, CURRENT_TIMESTAMP),
                                                                                                ('MSFT', 320.00, 325.10, 326.50, 319.00, CURRENT_TIMESTAMP),
                                                                                                ('TSLA', 705.00, 699.60, 710.00, 690.00, CURRENT_TIMESTAMP),
                                                                                                ('AMZN', 3400.00, 3450.25, 3460.00, 3390.00, CURRENT_TIMESTAMP),
                                                                                                ('DIS', 88.00, 89.50, 90.00, 87.00, CURRENT_TIMESTAMP);

-- TRANSACTIONS
INSERT INTO transactions (user_id, stocksymbol, currentprice, quantity, timestamp, type, total_amount) VALUES
                                                                                                           (1, 'AAPL', 197.20, 5, CURRENT_TIMESTAMP, 'BUY', 986.00),
                                                                                                           (2, 'GOOGL', 2800.50, 1, CURRENT_TIMESTAMP, 'BUY', 2800.50);

-- PORTFOLIO ITEMS
INSERT INTO portfolio_item (user_id, stocksymbol, quantity, averagebuyprice) VALUES
                                                                                 (1, 'AAPL', 5, 190.00),
                                                                                 (2, 'GOOGL', 1, 2780.00);


-- WATCHLIST
INSERT INTO watchlist (user_id, stocksymbol, add_time) VALUES
                                                           (1, 'TSLA', CURRENT_TIMESTAMP),
                                                           (2, 'AMZN', CURRENT_TIMESTAMP);
