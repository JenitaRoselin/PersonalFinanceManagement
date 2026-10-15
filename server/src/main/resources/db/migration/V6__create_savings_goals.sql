CREATE TABLE savings_goals (
    goal_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users(user_id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    target_amount NUMERIC(12,2) NOT NULL
        CHECK (target_amount > 0),
    current_amount NUMERIC(12,2) NOT NULL DEFAULT 0
        CHECK (current_amount >= 0),
    target_date DATE,
    status VARCHAR(15) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'COMPLETED', 'PAUSED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
