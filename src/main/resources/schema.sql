CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL DEFAULT 'NON_MEMBER',
                       membership_status VARCHAR(20) NOT NULL DEFAULT 'NONE'
);

CREATE TABLE IF NOT EXISTS sauna_events (
                                            event_id INT AUTO_INCREMENT PRIMARY KEY,
                                            title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL,
    capacity INT NOT NULL,
    price DECIMAL(10,2),
    status VARCHAR(50)
    );

-- sørg for at kolonnen findes, også hvis tabellen allerede eksisterede
ALTER TABLE sauna_events
    ADD COLUMN IF NOT EXISTS gussmester_name VARCHAR(255);


