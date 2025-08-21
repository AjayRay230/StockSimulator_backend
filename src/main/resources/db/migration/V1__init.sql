CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     firstname VARCHAR(50),
    lastname VARCHAR(50),
    username VARCHAR(100),
    password VARCHAR(255),
    email VARCHAR(255),
    amount DECIMAL(15,2),
    role VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS stock (
                                     symbol VARCHAR(10) PRIMARY KEY,
    companyname VARCHAR(255),
    currentprice DECIMAL(10,2),
    changepercent DECIMAL(5,2),
    lastupdate TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS stock_price (
                                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                           symbol VARCHAR(10),
    open_price DOUBLE,
    close_price DOUBLE,
    high_price DOUBLE,
    low_price DOUBLE,
    timestamp TIMESTAMP,
    FOREIGN KEY (symbol) REFERENCES stock(symbol)
    );

CREATE TABLE IF NOT EXISTS transactions (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            user_id BIGINT,
                                            stocksymbol VARCHAR(10),
    currentprice DECIMAL(10,2),
    quantity INT,
    timestamp TIMESTAMP,
    type VARCHAR(20),
    total_amount DECIMAL(15,2),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS portfolio_item (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              user_id BIGINT NOT NULL,
                                              stocksymbol VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    averagebuyprice DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );


CREATE TABLE IF NOT EXISTS watchlist (
                                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                         user_id BIGINT,
                                         stocksymbol VARCHAR(10),
    add_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    );
