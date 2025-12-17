-- =============================
-- USERS
-- =============================
INSERT INTO users (name, email, password_hash, role, membership_status)
VALUES
    ('Member One', 'member1@example.com', 'password', 'MEMBER', 'ACTIVE'),
    ('Guest One',  'guest1@example.com', 'password', 'NON_MEMBER', 'NONE'),
    ('Admin One',  'admin1@example.com', 'admin',    'ADMIN', 'ACTIVE');


-- =============================
-- SAUNA EVENTS
-- =============================

-- EVENT 1 - FULLY BOOKED (til venteliste test)
INSERT INTO sauna_event
(title, description, gusmester_name, gusmester_image_url,
 start_time, duration_minutes, capacity, price,
 current_bookings, available_spots, status)
VALUES
    ('Morgen Gus', 'Dejlig start på dagen', 'Jens Damp', NULL,
     '2025-12-15T08:00:00', 60,
     1, 50,
     1, 0,
     'FULLY_BOOKED');


-- EVENT 2 - Ikke fuldt booket (skal give fejl hvis man prøver venteliste)
INSERT INTO sauna_event
(title, description, gusmester_name, gusmester_image_url,
 start_time, duration_minutes, capacity, price,
 current_bookings, available_spots, status)
VALUES
    ('Aften Gus', 'Hyggelig aftenstemning', 'Sarah Sved', NULL,
     '2025-12-15T20:00:00', 60,
     5, 80,
     2, 3,
     'UPCOMING');


-- =============================
-- NO BOOKINGS OR WAITLIST ENTRIES YET
-- =============================
-- Booking til user 1
INSERT INTO bookings (booking_id, created_at, status, user_id, event_id)
VALUES (1, CURRENT_TIMESTAMP, 'ACTIVE', 1, 1);

-- Waitlist-entry til user 2
INSERT INTO waitlist_entries (entry_id, position, created_at, promoted, type, user_id, event_id)
VALUES (1, 1, CURRENT_TIMESTAMP, FALSE, 'MEMBER', 2, 1);
-- Eksempel-data til sauna events (uge med gæst/medlem/vagt tider)
INSERT INTO sauna_events (title, description, gusmester_name, start_time, duration_minutes, capacity, price, status)
VALUES
    ('GÆSTER • Onsdag Sauna • 09:00-11:00',
     'Åben sauna for gæster',
     'Peter A.',
     '2025-12-10 09:00:00',
     120,
     12,
     40.00,
     'UPCOMING'),

    ('MEDLEMSGUS • Tirsdag Gus • 20:00-21:00',
     'Saunagus kun for medlemmer',
     'Lars N.',
     '2025-12-09 20:00:00',
     60,
     12,
     0.00,
     'UPCOMING'),

    ('ÅBEN GUS • Fredag Gus • 19:00-20:00',
     'Åben gus for alle',
     'Maria S.',
     '2025-12-12 19:00:00',
     60,
     15,
     60.00,
     'UPCOMING'),

    ('VAGT • Onsdag Vagt • 15:00-21:00',
     'Medlem kan tage vagt og tjekke billetter',
     'VAGT',
     '2025-12-10 15:00:00',
     360,
     1,
     0.00,
     'UPCOMING');
