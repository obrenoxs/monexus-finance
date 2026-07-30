ALTER TABLE users
    ADD COLUMN pending_email VARCHAR(150) NULL,
    ADD COLUMN email_change_token VARCHAR(255) NULL,
    ADD COLUMN email_change_token_expires_at DATETIME NULL;

CREATE INDEX idx_users_email_change_token ON users (email_change_token);