CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'NON_MEMBER',
                       membership_status VARCHAR(20) NOT NULL DEFAULT 'NONE'
);

CREATE TABLE sauna_events (
                              event_id INT AUTO_INCREMENT PRIMARY KEY,
                              title VARCHAR(255) NOT NULL,
                              description TEXT,
                              gusmester_name VARCHAR(255),
                              gusmester_image_url VARCHAR(500),
                              start_time TIMESTAMP NOT NULL,
                              duration_minutes INT NOT NULL,
                              capacity INT NOT NULL,
                              price DECIMAL(10,2),
                              current_bookings INT DEFAULT 0,
                              status VARCHAR(50)
);

CREATE TABLE bookings (
                          booking_id INT AUTO_INCREMENT PRIMARY KEY,
                          user_id INT NOT NULL,
                          event_id INT NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          status VARCHAR(50) NOT NULL,
                          CONSTRAINT fk_bookings_user
                              FOREIGN KEY (user_id) REFERENCES users(user_id),
                          CONSTRAINT fk_bookings_event
                              FOREIGN KEY (event_id) REFERENCES sauna_events(event_id)
);



