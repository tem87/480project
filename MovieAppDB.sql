-- ENSF 480 Project Group 7

DROP DATABASE IF EXISTS MovieAppDB;
CREATE DATABASE MovieAppDB;
USE MovieAppDB;

CREATE TABLE Movie (
    movie_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(100) NOT NULL,
    synopsis TEXT,
    length VARCHAR(100) NOT NULL,
    rating VARCHAR(10),
    price DECIMAL(8, 2) NOT NULL,
    early_access BOOLEAN DEFAULT FALSE
);

CREATE TABLE Theater (
    theater_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);

CREATE TABLE Showtime (
    showtime_id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    theater_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    max_seats INT NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES Movie(movie_id),
    FOREIGN KEY (theater_id) REFERENCES Theater(theater_id)
);

CREATE TABLE Seats (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    showtime_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    status ENUM('Available', 'Booked') DEFAULT 'Available',
    FOREIGN KEY (showtime_id) REFERENCES Showtime(showtime_id)
);

CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    phone_number VARCHAR(15),
    address TEXT,
    is_registered BOOLEAN DEFAULT FALSE,
    annual_fee_paid BOOLEAN DEFAULT FALSE
);

CREATE TABLE Tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    showtime_id INT NOT NULL,
    seat_id INT NOT NULL,
    price DECIMAL(8, 2) NOT NULL,
    status ENUM('Booked', 'Cancelled') DEFAULT 'Booked',
    purchase_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (showtime_id) REFERENCES Showtime(showtime_id),
    FOREIGN KEY (seat_id) REFERENCES Seats(seat_id)
);

CREATE TABLE Bank (
    bank_id INT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(16) UNIQUE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    expiration_date VARCHAR(5) NOT NULL, -- Format MM/YY
    balance DECIMAL(10, 2)
);

CREATE TABLE Voucher (
    voucher_id INT NOT NULL PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(8, 2) NOT NULL, -- Value of the voucher
    is_used BOOLEAN DEFAULT FALSE, -- Indicates if the voucher has been redeemed
    created_at DATETIME NOT NULL, -- Timestamp when voucher was issued
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Receipt (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    ticket_id INT,
    card_number VARCHAR(16),
    payment_date DATETIME NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    masked_card_number VARCHAR(19),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (ticket_id) REFERENCES Tickets(ticket_id)
); 

CREATE TABLE News (
	news_id INT AUTO_INCREMENT PRIMARY KEY,
    headline VARCHAR(255) NOT NULL,
    content TEXT NOT NULL
);

-- Insert sample data into News table
INSERT INTO News (headline, content) VALUES
('New Blockbuster Movie Released', 'A thrilling new blockbuster has just hit theaters, breaking box office records.'),
('Award Season Kicks Off', 'The award season is officially here. Several movies are tipped to win big this year.'),
('Exclusive Interview with Top Director', 'In an exclusive interview, the director discusses the making of their latest hit movie.'),
('Top 10 Movies to Watch this Winter', 'Here is our pick of the top 10 movies to enjoy during the winter season.'),
('Upcoming Movie Premieres This Month', 'Several highly anticipated movies are premiering this month. Check out the full schedule here.');


INSERT INTO Bank (card_number, cvv, expiration_date, balance)
VALUES
('1234567890123456', '123', '12/25', 500.00),
('6543210987654321', '456', '11/24', 300.00),
('1111222233334444', '789', '10/25', 1000.00),
('9999000011112222', '654', '08/27', 1200.00);


-- Insert movies with prices
INSERT INTO Movie (title, genre, synopsis, length, rating, price, early_access) VALUES
('Avengers: Endgame', 'Action', 'The Avengers attempt to undo the damage caused by Thanos.', '3h 2min', 'PG-13', 20.00, FALSE),
('Shrek 2', 'Animation', 'Shrek and Fiona visit her parents.', '1h 33min', 'PG', 15.00, TRUE),
('Spirited Away', 'Fantasy', 'A young girl enters a magical.', '2h 5min', 'PG', 13.00, FALSE),
('Me Before You', 'Romance', 'A young woman forms a bond with a paralyzed man she cares for.', '1h 50min', 'PG-13', 13.00, FALSE);

-- Insert theaters
INSERT INTO Theater (name, location) VALUES
('Crowfoot Theater', '682 8 Ave'),
('Kensington Theater', '278 Kensington');

-- Insert showtimes
INSERT INTO Showtime (movie_id, theater_id, start_time, max_seats) VALUES
(1, 1, '2024-11-25 17:00', 20), -- less than 72 hours
(1, 2, '2024-12-25 18:00', 20), -- more
(2, 1, '2024-12-21 19:00', 20),
(2, 2, '2024-12-21 20:00', 20),
(3, 1, '2024-12-22 22:00', 20),
(3, 2, '2024-12-21 20:00', 20),
(4, 1, '2024-12-22 22:00', 20),
(4, 2, '2024-12-21 20:00', 20);


-- Generate 20 seats for showtime_id = 1
INSERT INTO Seats (showtime_id, seat_number) VALUES
(1, 'A1'), (1, 'A2'), (1, 'A3'), (1, 'A4'), (1, 'A5'),
(1, 'A6'), (1, 'A7'), (1, 'A8'), (1, 'A9'), (1, 'A10'),
(1, 'A11'), (1, 'A12'), (1, 'A13'), (1, 'A14'), (1, 'A15'),
(1, 'A16'), (1, 'A17'), (1, 'A18'), (1, 'A19'), (1, 'A20');

-- Generate 20 seats for showtime_id = 2
INSERT INTO Seats (showtime_id, seat_number) VALUES
(2, 'B1'), (2, 'B2'), (2, 'B3'), (2, 'B4'), (2, 'B5'),
(2, 'B6'), (2, 'B7'), (2, 'B8'), (2, 'B9'), (2, 'B10'),
(2, 'B11'), (2, 'B12'), (2, 'B13'), (2, 'B14'), (2, 'B15'),
(2, 'B16'), (2, 'B17'), (2, 'B18'), (2, 'B19'), (2, 'B20');

-- Generate 20 seats for showtime_id = 3
INSERT INTO Seats (showtime_id, seat_number) VALUES
(3, 'C1'), (3, 'C2'), (3, 'C3'), (3, 'C4'), (3, 'C5'),
(3, 'C6'), (3, 'C7'), (3, 'C8'), (3, 'C9'), (3, 'C10'),
(3, 'C11'), (3, 'C12'), (3, 'C13'), (3, 'C14'), (3, 'C15'),
(3, 'C16'), (3, 'C17'), (3, 'C18'), (3, 'C19'), (3, 'C20');

-- Generate 20 seats for showtime_id = 7
INSERT INTO Seats (showtime_id, seat_number) VALUES
(7, 'D1'), (7, 'D2'), (7, 'D3'), (7, 'D4'), (7, 'D5'),
(7, 'D6'), (7, 'D7'), (7, 'D8'), (7, 'D9'), (7, 'D10'),
(7, 'D11'), (7, 'D12'), (7, 'D13'), (7, 'D14'), (7, 'D15'),
(7, 'D16'), (7, 'D17'), (7, 'D18'), (7, 'D19'), (7, 'D20');

-- Generate 20 seats for showtime_id = 8
INSERT INTO Seats (showtime_id, seat_number) VALUES
(8, 'E1'), (8, 'E2'), (8, 'E3'), (8, 'E4'), (8, 'E5'),
(8, 'E6'), (8, 'E7'), (8, 'E8'), (8, 'E9'), (8, 'E10'),
(8, 'E11'), (8, 'E12'), (8, 'E13'), (8, 'E14'), (8, 'E15'),
(8, 'E16'), (8, 'E17'), (8, 'E18'), (8, 'E19'), (8, 'E20');

-- Generate 20 seats for showtime_id = 5
INSERT INTO Seats (showtime_id, seat_number) VALUES
(5, 'F1'), (5, 'F2'), (5, 'F3'), (5, 'F4'), (5, 'F5'),
(5, 'F6'), (5, 'F7'), (5, 'F8'), (5, 'F9'), (5, 'F10'),
(5, 'F11'), (5, 'F12'), (5, 'F13'), (5, 'F14'), (5, 'F15'),
(5, 'F16'), (5, 'F17'), (5, 'F18'), (5, 'F19'), (5, 'F20');

-- Generate 20 seats for showtime_id = 6
INSERT INTO Seats (showtime_id, seat_number) VALUES
(6, 'G1'), (6, 'G2'), (6, 'G3'), (6, 'G4'), (6, 'G5'),
(6, 'G6'), (6, 'G7'), (6, 'G8'), (6, 'G9'), (6, 'G10'),
(6, 'G11'), (6, 'G12'), (6, 'G13'), (6, 'G14'), (6, 'G15'),
(6, 'G16'), (6, 'G17'), (6, 'G18'), (6, 'G19'), (6, 'G20');



INSERT INTO Users (name, email, password, phone_number, address, is_registered, annual_fee_paid) VALUES
('Jamie Smith', 'jamie1@gmaile.com', NULL, NULL, NULL, FALSE, FALSE),
('Ana', 'a@gmail.com', 'ana145', '0987654321', '596 Oak Avenue', TRUE, FALSE);


-- Step 2: Insert Voucher for the User
INSERT INTO Users (name, email, password, phone_number, address, is_registered, annual_fee_paid)
VALUES ('John Doe', 'john.doe@example.com', 'securepassword123', '5551234567', '123 Maple Street', TRUE, TRUE);
-- Not expired
INSERT INTO Voucher (voucher_id, user_id, amount, is_used, created_at)
VALUES (4, 3, 50.00, FALSE, '2024-11-25 12:00:00');  -- Valid voucher
-- Expired Voucher (created in the past, e.g., January 2024)
INSERT INTO Voucher (voucher_id, user_id, amount, is_used, created_at)
VALUES (5, 3, 50.00, FALSE, '2023-01-01 00:00:00');  -- Expired voucher
-- Used Voucher (marked as used)
INSERT INTO Voucher (voucher_id, user_id, amount, is_used, created_at)
VALUES (6, 3, 50.00, TRUE, '2024-11-15 00:00:00');  -- Used voucher

-- Insert guest user (non-registered)
INSERT INTO Users (name, email, password, phone_number, address, is_registered, annual_fee_paid)
VALUES ('Guest User', 'guest@example.com', NULL, NULL, NULL, FALSE, FALSE);
-- Valid voucher for guest (not expired)
INSERT INTO Voucher (voucher_id, user_id, amount, is_used, created_at)
VALUES (7, 4, 30.00, FALSE, '2024-11-25 12:00:00');  -- Valid voucher for guest

-- Expired voucher for guest (created in the past, e.g., January 2024)
INSERT INTO Voucher (voucher_id, user_id, amount, is_used, created_at)
VALUES (8, 4, 20.00, FALSE, '2023-01-01 00:00:00');  -- Expired voucher for guest
