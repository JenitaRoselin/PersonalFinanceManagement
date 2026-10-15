CREATE TABLE budgets (
    budget_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users(user_id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL,
    budget_month DATE NOT NULL
        CHECK (EXTRACT(DAY FROM budget_month) = 1),
    limit_amount NUMERIC(12,2) NOT NULL
        CHECK (limit_amount > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id, user_id)
        REFERENCES categories(category_id, user_id),

    UNIQUE (user_id, category_id, budget_month)
);

CREATE INDEX idx_budgets_user_month
    ON budgets(user_id, budget_month);
