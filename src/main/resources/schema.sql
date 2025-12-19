DROP TABLE IF EXISTS waitlist_entries;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS shifts;
DROP TABLE IF EXISTS sauna_event;
DROP TABLE IF EXISTS users;


CREATE TABLE users
(
    user_id           INT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100) NOT NULL,
    email             VARCHAR(100) NOT NULL UNIQUE,
    password_hash     VARCHAR(255) NOT NULL,
    role              VARCHAR(20)  NOT NULL DEFAULT 'NON_MEMBER',
    membership_status VARCHAR(20)  NOT NULL DEFAULT 'NON_MEMBER'
);

CREATE TABLE sauna_event
(
    event_id            INT AUTO_INCREMENT PRIMARY KEY,
    title               VARCHAR(255)   NOT NULL,
    description         TEXT,
    gusmester_name      VARCHAR(255),
    gusmester_image_url VARCHAR(255),
    start_time          TIMESTAMP      NOT NULL,
    duration_minutes    INT            NOT NULL,
    capacity            INT            NOT NULL,
    price               DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    current_bookings    INT            NOT NULL DEFAULT 0,
    available_spots     INT            NOT NULL DEFAULT 0,
    status              VARCHAR(20)    NOT NULL
);

CREATE TABLE bookings
(
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status     VARCHAR(20) NOT NULL,
    user_id    INT         NOT NULL,
    event_id   INT         NOT NULL,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES sauna_event (event_id)
);

CREATE TABLE waitlist_entries
(
    entry_id   INT AUTO_INCREMENT PRIMARY KEY,
    position   INT         NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    promoted   BOOLEAN     NOT NULL DEFAULT FALSE,
    type       VARCHAR(20) NOT NULL,
    user_id    INT         NOT NULL,
    event_id   INT         NOT NULL,
    CONSTRAINT fk_waitlist_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_waitlist_event FOREIGN KEY (event_id) REFERENCES sauna_event (event_id)
);

CREATE TABLE shifts
(
    shift_id   INT AUTO_INCREMENT PRIMARY KEY,
    date       DATE         NOT NULL,
    start_time TIME         NOT NULL,
    end_time   TIME         NOT NULL,
    label      VARCHAR(100) NOT NULL,
    user_id    INT          NOT NULL,
    CONSTRAINT fk_shift_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);
