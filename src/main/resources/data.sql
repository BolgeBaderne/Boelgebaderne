-- =============================
-- USERS
-- =============================
INSERT INTO users (name, email, password_hash, role, membership_status)
VALUES
    ('Member One', 'member1@example.com', 'password', 'MEMBER', 'MEMBER'),
    ('Guest One',  'guest1@example.com', 'password', 'NON_MEMBER', 'NON_MEMBER'),
    ('Admin One',  'admin1@example.com', 'admin',    'ADMIN', 'MEMBER');


-- =============================
-- SAUNA EVENTS (singular table: sauna_event)
-- =============================

-- EVENT 1 - FULLY BOOKED (for waitlist test)
INSERT INTO sauna_event
(title, description, gusmester_name, gusmester_image_url,
 start_time, duration_minutes, capacity, price,
 current_bookings, available_spots, status)
VALUES
    ('Morgen Gus', 'Dejlig start på dagen', 'Jens Damp', '',
     '2025-12-15 08:00:00', 60,
     1, 50.00,
     1, 0,
     'FULLY_BOOKED');


-- EVENT 2 - Ikke fuldt booket
INSERT INTO sauna_event
(title, description, gusmester_name, gusmester_image_url,
 start_time, duration_minutes, capacity, price,
 current_bookings, available_spots, status)
VALUES
    ('Aften Gus', 'Hyggelig aftenstemning', 'Sarah Sved', '',
     '2025-12-15 20:00:00', 60,
     5, 80.00,
     2, 3,
     'UPCOMING');


-- =============================
-- EXAMPLE GENERATED SAUNA EVENTS (same sauna_event table)
-- =============================
INSERT INTO sauna_event
(title, description, gusmester_name, gusmester_image_url,
 start_time, duration_minutes, capacity, price,
 current_bookings, available_spots, status)
VALUES
    ('GÆSTER • Onsdag Sauna • 09:00-11:00', 'Åben sauna for gæster', 'Peter A.', '',
     '2025-12-10 09:00:00', 120, 12, 40.00, 0, 12, 'UPCOMING'),

    ('MEDLEMSGUS • Tirsdag Gus • 20:00-21:00', 'Saunagus kun for medlemmer', 'Lars N.', '',
     '2025-12-09 20:00:00', 60, 12, 0.00, 0, 12, 'FULLY_BOOKED'),

    ('ÅBEN GUS • Fredag Gus • 19:00-20:00', 'Åben gus for alle', 'Maria S.', '',
     '2025-12-12 19:00:00', 60, 12, 60.00, 0, 12, 'UPCOMING'),

    ('MEDLEMSGUS • Morgen Gus • 07:00-08:00', 'Saunagus kun for medlemmer', 'Nana K.', '',
     '2025-12-10 07:00:00', 60, 12, 0.00, 0, 12, 'CANCELLED');



-- =============================
-- BOOKINGS AND WAITLIST
-- =============================
-- Booking for user 1 on event 1
INSERT INTO bookings (created_at, status, user_id, event_id)
VALUES (CURRENT_TIMESTAMP, 'ACTIVE', 1, 1);

-- Waitlist-entry for user 2 on event 1
INSERT INTO waitlist_entries (position, created_at, promoted, type, user_id, event_id)
VALUES (1, CURRENT_TIMESTAMP, FALSE, 'MEMBER', 2, 1);



-- =============================
-- SHIFTS (Dashboard test data)
-- =============================

INSERT INTO shifts (date, start_time, end_time, label, user_id)
VALUES
    -- Næste uge
    ('2025-12-16', '08:00:00', '12:00:00', 'Morgenvagt', 1),
    ('2025-12-16', '12:00:00', '16:00:00', 'Middagsvagt', 1),

    -- Senere på ugen
    ('2025-12-18', '16:00:00', '20:00:00', 'Aftenvagt', 1),

    -- Weekend
    ('2025-12-21', '11:00:00', '15:00:00', 'Weekendvagt', 1);
