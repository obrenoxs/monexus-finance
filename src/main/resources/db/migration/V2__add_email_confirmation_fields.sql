ALTER TABLE users
    ADD COLUMN confirmation_token VARCHAR(255) NULL,
    ADD COLUMN confirmation_token_expires_at DATETIME NULL;

CREATE INDEX idx_users_confirmation_token ON users (confirmation_token);