CREATE TABLE goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    target_amount DECIMAL(19,2) NOT NULL,
    current_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
    target_date DATE NULL,
    wallet_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    CONSTRAINT fk_goals_wallet
       FOREIGN KEY (wallet_id) REFERENCES wallets(id)
);

CREATE INDEX idx_goals_wallet_id ON goals(wallet_id);
CREATE INDEX idx_goals_target_date ON goals(target_date);