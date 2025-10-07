-- V1__create_tables.sql

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    age INT,
    gender VARCHAR(10),
    blood_group VARCHAR(10),
    address VARCHAR(255),
    phone VARCHAR(15),
    user_type VARCHAR(10) NOT NULL,
    registration_date DATETIME NOT NULL
);

CREATE TABLE donations (
    donation_id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT NOT NULL,
    donation_date DATE NOT NULL,
    hospital_name VARCHAR(100),
    FOREIGN KEY (donor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    acceptor_id INT NOT NULL,
    requested_blood_group VARCHAR(10),
    request_date DATETIME NOT NULL,
    hospital_name VARCHAR(100),
    status VARCHAR(15) NOT NULL,
    FOREIGN KEY (acceptor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE credits (
    credit_id INT AUTO_INCREMENT PRIMARY KEY,
    donor_id INT NOT NULL,
    credit_earned_date DATE NOT NULL,
    credit_expiry_date DATE NOT NULL,
    FOREIGN KEY (donor_id) REFERENCES users(user_id) ON DELETE CASCADE
);