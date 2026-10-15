CREATE TABLE categories (
    category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_users(user_id) ON DELETE CASCADE,
    name VARCHAR(60) NOT NULL,
    type VARCHAR(10) NOT NULL
        CHECK (type IN ('INCOME', 'EXPENSE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE (user_id, name, type),
    UNIQUE (category_id, user_id)
);
