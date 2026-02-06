-- Ensure no duplicate emails exist before adding constraint
-- (Flyway will fail if duplicates exist)

ALTER TABLE users
    ADD CONSTRAINT uq_users_email UNIQUE (email);
