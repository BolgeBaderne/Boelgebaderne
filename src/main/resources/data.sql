INSERT INTO users (name, email, password_hash, role, membership_status)
VALUES
    ('Member One', 'member1@example.com',
     '$2y$10$DZe5DchD78BCN5wTXVXU/ukIIwxrCR4zrG3RERfO3J/E2OgjWeniK',
     'MEMBER', 'ACTIVE'),

    ('Guest One', 'guest1@example.com',
     '$2y$10$DZe5DchD78BCN5wTXVXU/ukIIwxrCR4zrG3RERfO3J/E2OgjWeniK',
     'NON_MEMBER', 'NONE'),

    ('Admin One', 'admin1@example.com',
     '$2y$10$8GT7GwBM8p2diK/erlgPPO4mB1B7hfYjJF8WhoOSt4WuibOsaS4pa',
     'ADMIN', 'ACTIVE');
