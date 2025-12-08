-- =========================================================
--  DATABASE-SCRIPT TIL EVENTDETALJER (BØLGEBADERNE)
--  Dækker kun det der er relevant for User Story "Eventdetaljer"
-- =========================================================

-- SLET TABELLER HVIS DE ALLEREDE FINDES (til udvikling/test)
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS sauna_event;

-- =========================================================
--  TABEL: SaunaEvent
-- =========================================================
CREATE TABLE sauna_event (
                             event_id             INT PRIMARY KEY AUTO_INCREMENT,
                             title                VARCHAR(100) NOT NULL,
                             description          TEXT NOT NULL,
                             gusmester_name       VARCHAR(100) NOT NULL,
                             gusmester_image_url  VARCHAR(255),
                             start_time           TIMESTAMP NOT NULL,
                             duration_minutes     INT NOT NULL,
                             capacity             INT NOT NULL,
                             price                DOUBLE NOT NULL,
                             status               VARCHAR(20) NOT NULL,

    -- NY KOLONNE, MATCHER DIN ENTITY
                             current_bookings     INT NOT NULL DEFAULT 0
);

-- =========================================================
--  TABEL: Booking
--  Bruges til at kunne udregne tilgængelige pladser
-- =========================================================
CREATE TABLE booking (
                         booking_id      INT PRIMARY KEY AUTO_INCREMENT,
                         created_at      TIMESTAMP NOT NULL,
                         status          VARCHAR(20) NOT NULL,        -- 'CONFIRMED'
                         user_id         INT NOT NULL,
                         event_id        INT NOT NULL,

                         CONSTRAINT fk_booking_event
                             FOREIGN KEY (event_id) REFERENCES sauna_event(event_id)
);

-- =========================================================
--  EKSEMPEL-DATA — EVENTS
-- =========================================================

INSERT INTO sauna_event
(title, description, gusmester_name, gusmester_image_url,
 start_time, duration_minutes, capacity, price, status, current_bookings)
VALUES
    (
        'Morgen-saunagus',
        'En blid start på dagen med æteriske olier og roligt tempo.',
        'Anne Larsen',
        'https://example.com/images/gusmester_anne.jpg',
        '2025-12-06 08:00:00',
        45,
        12,
        120.00,
        'UPCOMING',
        5   -- 5 bookinger
    ),
    (
        'Aften-saunagus',
        'Intens gus med fokus på varme, åndedræt og afslapning.',
        'Peter Andersen',
        'https://example.com/images/gusmester_peter.jpg',
        '2025-12-06 19:00:00',
        60,
        14,
        150.00,
        'UPCOMING',
        10  -- 10 bookinger
    );

-- =========================================================
--  EKSEMPEL-DATA — BOOKINGER
-- =========================================================

-- Event 1: 5 tilmeldte
INSERT INTO booking (created_at, status, user_id, event_id)
VALUES
    ('2025-12-01 10:00:00', 'CONFIRMED', 1, 1),
    ('2025-12-01 11:00:00', 'CONFIRMED', 2, 1),
    ('2025-12-02 09:30:00', 'CONFIRMED', 3, 1),
    ('2025-12-02 12:15:00', 'CONFIRMED', 4, 1),
    ('2025-12-03 08:45:00', 'CONFIRMED', 5, 1);

-- Event 2: 10 tilmeldte
INSERT INTO booking (created_at, status, user_id, event_id)
VALUES
    ('2025-12-01 18:00:00', 'CONFIRMED', 6, 2),
    ('2025-12-01 18:05:00', 'CONFIRMED', 7, 2),
    ('2025-12-01 18:10:00', 'CONFIRMED', 8, 2),
    ('2025-12-01 18:20:00', 'CONFIRMED', 9, 2),
    ('2025-12-01 18:25:00', 'CONFIRMED', 10, 2),
    ('2025-12-01 18:30:00', 'CONFIRMED', 11, 2),
    ('2025-12-01 18:35:00', 'CONFIRMED', 12, 2),
    ('2025-12-01 18:40:00', 'CONFIRMED', 13, 2),
    ('2025-12-01 18:45:00', 'CONFIRMED', 14, 2),
    ('2025-12-01 18:50:00', 'CONFIRMED', 15, 2);
