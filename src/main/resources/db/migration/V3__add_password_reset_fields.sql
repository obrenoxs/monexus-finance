ALTER TABLE users
    ADD COLUMN reset_password_token VARCHAR(255) NULL,
    ADD COLUMN reset_password_token_expires_at DATETIME NULL;

CREATE INDEX idx_users_reset_password_token ON users (reset_password_token);