CREATE TABLE transactions (
    transaction_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users(user_id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL,
    payment_method_id BIGINT,
    type VARCHAR(10) NOT NULL
        CHECK (type IN ('INCOME', 'EXPENSE')),
    amount NUMERIC(12,2) NOT NULL
        CHECK (amount > 0),
    transaction_date DATE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id, user_id)
        REFERENCES categories(category_id, user_id),

    FOREIGN KEY (payment_method_id, user_id)
        REFERENCES payment_methods(payment_method_id, user_id)
);

CREATE INDEX idx_transactions_user_date
    ON transactions(user_id, transaction_date);

CREATE INDEX idx_transactions_user_category_date
    ON transactions(user_id, category_id, transaction_date);
