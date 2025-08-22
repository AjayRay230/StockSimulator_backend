INSERT INTO users (username, firstname, lastname, password, email, amount, role)
VALUES
    ('ajayroy','AJAY','ROY','$2a$12$lNnrPojpUGBi7ShsZEY6weo260eN6XspeYqjMUWzNuQtBpxSAH3/m','AjayRy7439@gmail.com',1000000,'ADMIN')
    ON CONFLICT (username) DO UPDATE
                                  SET firstname = EXCLUDED.firstname,
                                  lastname  = EXCLUDED.lastname,
                                  password  = EXCLUDED.password,
                                  email     = EXCLUDED.email,
                                  amount    = EXCLUDED.amount,
                                  role      = EXCLUDED.role;
