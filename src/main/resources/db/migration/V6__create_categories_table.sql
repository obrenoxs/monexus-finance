CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    color VARCHAR(20) NOT NULL,
    icon VARCHAR(50) NOT NULL,
    wallet_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT fk_categories_wallet
        FOREIGN KEY (wallet_id) REFERENCES wallets(id),

    CONSTRAINT uk_categories_wallet_name_type
        UNIQUE (wallet_id, name, type)
);

CREATE INDEX idx_categories_wallet_id
    ON categories(wallet_id);