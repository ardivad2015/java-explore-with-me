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
	CONSTRAINT users_uq_user_email UNIQUE (email),
	CONSTRAINT users_min_length_email CHECK (length(email) >= 6),
	CONSTRAINT users_min_length_name CHECK (length(email) >= 2)
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
    CONSTRAINT events_category_fk FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    CONSTRAINT events_user_fk FOREIGN KEY (initiator_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    CONSTRAINT events_min_length_annotation CHECK (length(annotation) >= 20),
    CONSTRAINT events_min_length_description CHECK (length(description) >= 20),
    CONSTRAINT events_min_length_title CHECK (length(title) >= 3)
);

CREATE INDEX ON events (initiator_id);
CREATE INDEX ON events (category_id);
CREATE INDEX ON events (event_date);

create table if not exists requests
(
    request_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created timestamp without time zone NOT NULL,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(64) NOT NULL,
    CONSTRAINT requests_event_fk FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT requests_user_fk FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX ON requests (event_id);
CREATE INDEX ON requests (user_id);

create table if not exists compilations
(
    compilation_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN NOT NULL,
    title varchar(2048) NOT NULL,
    CONSTRAINT compilations_min_length_title CHECK (length(title) >= 1),
    CONSTRAINT compilations_uq_title UNIQUE (title)
);

create table if not exists compilations_events
(
    comp_event_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    CONSTRAINT comp_ev_compilation_id_fk foreign key (compilation_id) references compilations (compilation_id) ON DELETE CASCADE,
    CONSTRAINT comp_ev_event_id_fk foreign key (event_id) references events (event_id) ON DELETE CASCADE,
    CONSTRAINT ccomp_ev_omp_event_unq UNIQUE (compilation_id,event_id)
);

CREATE INDEX ON compilations_events (compilation_id);
CREATE INDEX ON compilations_events (event_id);