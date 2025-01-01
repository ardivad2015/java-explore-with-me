CREATE TABLE IF NOT EXISTS categories
(
    category_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL,
    CONSTRAINT uq_category_name UNIQUE (category_name)
);