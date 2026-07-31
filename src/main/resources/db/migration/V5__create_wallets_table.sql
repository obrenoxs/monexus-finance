CREATE TABLE wallets (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     currency VARCHAR(5) NOT NULL,
     user_id BIGINT NOT NULL,
     created_at DATETIME NOT NULL,
     updated_at DATETIME NOT NULL,
     CONSTRAINT uk_wallets_user_id UNIQUE (user_id),
     CONSTRAINT fk_wallets_user FOREIGN KEY (user_id) REFERENCES users (id)
);