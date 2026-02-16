CREATE TABLE limit_order (
                             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             username VARCHAR(255) NOT NULL,
                             stock_symbol VARCHAR(50) NOT NULL,
                             quantity INT NOT NULL,
                             remaining_quantity INT NOT NULL,
                             price NUMERIC(19,2) NOT NULL,
                             type VARCHAR(20) NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             created_at TIMESTAMP NOT NULL,
                             version BIGINT
);