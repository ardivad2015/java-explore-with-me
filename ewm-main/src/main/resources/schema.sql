CREATE TABLE IF NOT EXISTS categories
(
    category_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL,
    CONSTRAINT uq_category_name UNIQUE (category_name)
);

CREATE TABLE IF NOT EXISTS users (
	user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	user_name VARCHAR(250) NOT NULL,
	email VARCHAR(254) NOT NULL,
	CONSTRAINT uq_user_email UNIQUE (email),
	CONSTRAINT min_length_email CHECK (length(email) > 5),
	CONSTRAINT min_length_name CHECK (length(email) > 1)
);