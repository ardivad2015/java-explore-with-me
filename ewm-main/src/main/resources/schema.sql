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
	CONSTRAINT min_length_email CHECK (length(email) >= 6),
	CONSTRAINT min_length_name CHECK (length(email) >= 2)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL,
    lat REAL NOT NULL,
    lon REAL NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INT NOT NULL,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(50) NOT NULL,
    title VARCHAR(120) NOT NULL,
    CONSTRAINT category_fk FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    CONSTRAINT user_fk FOREIGN KEY (initiator_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    CONSTRAINT min_length_annotation CHECK (length(annotation) >= 20),
    CONSTRAINT min_length_description CHECK (length(description) >= 20),
    CONSTRAINT min_length_title CHECK (length(title) >= 3)
);