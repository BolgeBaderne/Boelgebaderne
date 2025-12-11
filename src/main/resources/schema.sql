DROP TABLE IF EXISTS sauna_event;
CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'NON_MEMBER',
                       membership_status VARCHAR(20) NOT NULL DEFAULT 'NONE'
);

CREATE TABLE sauna_event (
                             event_id           INT AUTO_INCREMENT PRIMARY KEY,
                             title              VARCHAR(150)  NOT NULL,
                             description        VARCHAR(500)  NOT NULL,
                             gusmester_name     VARCHAR(100)  NOT NULL,
                             gusmester_image_url VARCHAR(255),

                             start_time         TIMESTAMP     NOT NULL,
                             duration_minutes   INT           NOT NULL,

                             capacity           INT           NOT NULL,
                             price              DOUBLE        NOT NULL,

                             current_bookings   INT           NOT NULL,
                             available_spots    INT           NOT NULL,

                             status             VARCHAR(20)   NOT NULL
);


CREATE TABLE bookings (
                          booking_id      INT AUTO_INCREMENT PRIMARY KEY,
                          created_at      TIMESTAMP      NOT NULL,
                          status          VARCHAR(20)    NOT NULL,
                          user_id         INT            NOT NULL,
                          event_id        INT            NOT NULL,
                          CONSTRAINT fk_booking_user  FOREIGN KEY (user_id)  REFERENCES users(user_id),
                          CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES sauna_event(event_id)
);

CREATE TABLE waitlist_entries (
                                  entry_id    INT AUTO_INCREMENT PRIMARY KEY,
                                  position    INT           NOT NULL,
                                  created_at  TIMESTAMP     NOT NULL,
                                  promoted    BOOLEAN       NOT NULL,
                                  type        VARCHAR(20)   NOT NULL,
                                  user_id     INT           NOT NULL,
                                  event_id    INT           NOT NULL,
                                  CONSTRAINT fk_waitlist_user  FOREIGN KEY (user_id)  REFERENCES users(user_id),
                                  CONSTRAINT fk_waitlist_event FOREIGN KEY (event_id) REFERENCES sauna_event(event_id)
);
