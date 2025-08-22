CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     firstname VARCHAR(50),
    lastname VARCHAR(50),
    username VARCHAR(100),
    password VARCHAR(255),
    email VARCHAR(255),
    amount NUMERIC(15,2),
    role VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS stock (
                                     symbol VARCHAR(10) PRIMARY KEY,
    companyname VARCHAR(255),
    currentprice NUMERIC(10,2),
    changepercent NUMERIC(5,2),
    lastupdate TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS stock_price (
                                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                           symbol VARCHAR(10),
    open_price DOUBLE PRECISION,
    close_price DOUBLE PRECISION,
    high_price DOUBLE PRECISION,
    low_price DOUBLE PRECISION,
    timestamp TIMESTAMP,
    FOREIGN KEY (symbol) REFERENCES stock(symbol)
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                            user_id BIGINT,
                                            stocksymbol VARCHAR(10),
    currentprice NUMERIC(10,2),
    quantity INT,
    timestamp TIMESTAMP,
    type VARCHAR(20),
    total_amount NUMERIC(15,2),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS portfolio_item (
                                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                              user_id BIGINT NOT NULL,
                                              stocksymbol VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    averagebuyprice NUMERIC(10,2) NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS watchlist (
                                         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                         user_id BIGINT,
                                         stocksymbol VARCHAR(10),
    add_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    );
