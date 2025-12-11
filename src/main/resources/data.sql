INSERT INTO users (name, email, password_hash, role, membership_status) VALUES
('Member One', 'member1@example.com', 'password', 'MEMBER', 'ACTIVE'),
('Guest One',  'guest1@example.com', 'password', 'NON_MEMBER', 'NONE'),
('Admin One',  'admin1@example.com', 'admin',    'ADMIN', 'ACTIVE');

-- Eksempel events
INSERT INTO sauna_event (
    title,
    description,
    gusmester_name,
    gusmester_image_url,
    start_time,
    duration_minutes,
    capacity,
    price,
    current_bookings,
    available_spots,
    status
) VALUES
      (
          'Morgen Saunagus',
          'Blid opvarmning og afslappende gus til at starte dagen.',
          'Gusmester Anna',
          'https://example.com/images/anna.jpg',
          TIMESTAMP '2025-12-15 08:00:00',
          60,
          10,
          75.0,
          0,     -- current_bookings
          10,    -- available_spots (samme som capacity når ingen bookings)
          'UPCOMING'
      ),
      (
          'Aften Saunagus',
          'Intens gus med fokus på afstressning efter arbejde.',
          'Gusmester Mads',
          'https://example.com/images/mads.jpg',
          TIMESTAMP '2025-12-15 19:00:00',
          90,
          8,
          95.0,
          0,
          8,
          'UPCOMING'
      );

-- Et event der er helt fuldt booket
INSERT INTO sauna_event (
    title, description, gusmester_name, gusmester_image_url,
    start_time, duration_minutes, capacity, price,
    current_bookings, available_spots, status
) VALUES (
             'FULDT BOOKET Saunagus',
             'Testevent som er fuldt booket.',
             'Gusmester Test',
             NULL,
             TIMESTAMP '2025-12-16 18:00:00',
             60,
             5,      -- capacity
             80.0,
             5,      -- current_bookings = 5
             0,      -- available_spots = 0
             'UPCOMING'
         );
