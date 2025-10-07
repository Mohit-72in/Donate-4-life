-- V2__insert_initial_data.sql
-- Seed initial users with default password ("password" hashed using BCrypt)

-- Insert Donors
INSERT INTO users (username, password, name, age, gender, blood_group, address, phone, user_type, registration_date)
VALUES
('saurabh@gmail.com', '$2a$10$Fk80MBuGxoinpW2nCdEgv.fizrwuTGtqZsFZ36lAtuBrgKmW5jyxy', 'Saurabh Kumar', 30, 'Male', 'O+', '123 Life St, Delhi', '9876543210', 'DONOR', '2025-01-10 09:00:00'),
('sheema@gmail.com', '$2a$10$Fk80MBuGxoinpW2nCdEgv.fizrwuTGtqZsFZ36lAtuBrgKmW5jyxy', 'Sheema Kumari', 28, 'Female', 'A+', '45 Health Ave, Mumbai', '9876543211', 'DONOR', '2025-02-15 11:30:00'),
('admin@donate4life.com', '$2a$10$pTNFByAnfz4w/htKw581Ne3KvjUZXkVrZPK9du3pRvamizaAN1CM2', 'Admin User', 99, 'Male', 'N/A', 'Main Office', '7903059919', 'ADMIN', '2025-01-01 00:00:00'),
('mohit@gmail.com','$2a$10$Fk80MBuGxoinpW2nCdEgv.fizrwuTGtqZsFZ36lAtuBrgKmW5jyxy','Mohit Kumar',23,'Male','B+','Sarovar Hostel CUSAT, South Kalamassery','7903059919','DONOR','2025-09-25 03:26:36');
-- Insert Acceptors
INSERT INTO users (username, password, name, age, gender, blood_group, address, phone, user_type, registration_date)
VALUES
('tanya@gmail.com', '$2a$10$Fk80MBuGxoinpW2nCdEgv.fizrwuTGtqZsFZ36lAtuBrgKmW5jyxy', 'Tanya Kumari', 21, 'Female', 'AB+', '1 Hope Blvd, Kolkata', '9123456780', 'ACCEPTOR', '2025-01-20 08:00:00'),
('prakash@gmail.com', '$2a$10$Fk80MBuGxoinpW2nCdEgv.fizrwuTGtqZsFZ36lAtuBrgKmW5jyxy', 'Prakash Chandra', 24, 'Male', 'O-', '2 Mercy Ln, Hyderabad', '9123456781', 'ACCEPTOR', '2025-02-25 10:45:00');
