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
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

INSERT INTO Bank (card_number, cvv, expiration_date, balance)
VALUES
('1234567890123456', '123', '12/25', 500.00),
('6543210987654321', '456', '11/24', 300.00),
('1111222233334444', '789', '10/23', 1000.00),
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
(1, 1, '2024-11-25 17:00', 10), -- less than 72 hours
(1, 2, '2024-11-30 18:00', 5), -- more
(2, 1, '2024-11-21 19:00', 10),
(2, 2, '2024-11-21 20:00', 10),
(3, 1, '2024-11-22 22:00', 10),
(3, 2, '2024-11-21 20:00', 10),
(4, 1, '2024-11-22 22:00', 10),
(4, 2, '2024-11-21 20:00', 10);


-- Generate seats for showtime_id = 1 (10 seats)
INSERT INTO Seats (showtime_id, seat_number) VALUES
(1, 'A1'), (1, 'A2'), (1, 'A3'), (1, 'A4'), (1, 'A5'),
(1, 'A6'), (1, 'A7'), (1, 'A8'), (1, 'A9'), (1, 'A10'),
(1, 'A11'), (1, 'A12');
-- Generate seats for showtime_id = 2 (5 seats)
INSERT INTO Seats (showtime_id, seat_number) VALUES
(2, 'B1'), (2, 'B2'), (2, 'B3'), (2, 'B4'), (2, 'B5');

-- Generate seats for showtime_id = 3 (5 seats)
INSERT INTO Seats (showtime_id, seat_number) VALUES
(3, 'B1'), (3, 'B2'), (3, 'B3'), (3, 'B4'), (3, 'B5');
-- c and d are for me before you
-- Generate seats for showtime_id = 7 (10 seats as per max_seats)
INSERT INTO Seats (showtime_id, seat_number) VALUES
(7, 'C1'), (7, 'C2'), (7, 'C3'), (7, 'C4'), (7, 'C5'),
(7, 'C6'), (7, 'C7'), (7, 'C8'), (7, 'C9'), (7, 'C10');

-- Generate seats for showtime_id = 8 (10 seats as per max_seats)
INSERT INTO Seats (showtime_id, seat_number) VALUES
(8, 'D1'), (8, 'D2'), (8, 'D3'), (8, 'D4'), (8, 'D5'),
(8, 'D6'), (8, 'D7'), (8, 'D8'), (8, 'D9'), (8, 'D10');

-- E and F are for spirited away
-- Generate seats for showtime_id = 5 (10 seats)
INSERT INTO Seats (showtime_id, seat_number) VALUES
(5, 'E1'), (5, 'E2'), (5, 'E3'), (5, 'E4'), (5, 'E5'),
(5, 'E6'), (5, 'E7'), (5, 'E8'), (5, 'E9'), (5, 'E10');

-- Generate seats for showtime_id = 6 (10 seats)
INSERT INTO Seats (showtime_id, seat_number) VALUES
(6, 'F1'), (6, 'F2'), (6, 'F3'), (6, 'F4'), (6, 'F5'),
(6, 'F6'), (6, 'F7'), (6, 'F8'), (6, 'F9'), (6, 'F10');
-- h is for shrek 2 for theater id 2
-- Generate seats for showtime_id = 4 (10 seats)
INSERT INTO Seats (showtime_id, seat_number) VALUES
(4, 'H1'), (4, 'H2'), (4, 'H3'), (4, 'H4'), (4, 'H5'),
(4, 'H6'), (4, 'H7'), (4, 'H8'), (4, 'H9'), (4, 'H10');



INSERT INTO Users (name, email, password, phone_number, address, is_registered, annual_fee_paid) VALUES
('Jamie Smith', 'jamie1@gmaile.com', NULL, NULL, NULL, FALSE, FALSE),
('a', 'a@gmail.com', 'a', '0987654321', '596 Oak Avenue', TRUE, FALSE);




/*
-- Users Table
INSERT INTO Users (name, email, password, phone_number, address, is_registered, annual_fee_paid) VALUES
('Jamie Smith', 'jamie1@gmaile.com', 'password123#', '1234567890', '890 Brentwood', TRUE, TRUE),
('Tony Sanchez', 'tony2@example.com', 'password456&', '0987654321', '596 Oak Avenue', FALSE, FALSE);

-- Tickets Table
-- checked that seats also updates when buying a ticket
INSERT INTO Tickets (user_id, showtime_id, seat_id, price, status, purchase_date) VALUES
(1, 1, 1, 12.50, 'Booked', '2024-11-10 10:00:00'),
(1, 1, 2, 12.50, 'Booked', '2024-11-10 10:15:00'),
(2, 2, 6, 15.00, 'Booked', '2024-11-11 14:30:00');
*/
