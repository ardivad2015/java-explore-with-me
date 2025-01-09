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
    CONSTRAINT event_fk FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE INDEX ON requests (event_id);
CREATE INDEX ON requests (user_id);

CREATE OR REPLACE FUNCTION location_distance(lat1 float, lon1 float, lat2 float, lon2 float)
    RETURNS float
AS
'
declare
    dist float = 0;
    rad_lat1 float;
    rad_lat2 float;
    theta float;
    rad_theta float;
BEGIN
    IF lat1 = lat2 AND lon1 = lon2
    THEN
        RETURN dist;
    ELSE
        -- переводим градусы широты в радианы
        rad_lat1 = pi() * lat1 / 180;
        -- переводим градусы долготы в радианы
        rad_lat2 = pi() * lat2 / 180;
        -- находим разность долгот
        theta = lon1 - lon2;
        -- переводим градусы в радианы
        rad_theta = pi() * theta / 180;
        -- находим длину ортодромии
        dist = sin(rad_lat1) * sin(rad_lat2) + cos(rad_lat1) * cos(rad_lat2) * cos(rad_theta);

        IF dist > 1
            THEN dist = 1;
        END IF;

        dist = acos(dist);
        -- переводим радианы в градусы
        dist = dist * 180 / pi();
        -- переводим градусы в километры
        dist = dist * 60 * 1.8524;

        RETURN dist;
    END IF;
END;
'
LANGUAGE PLPGSQL;