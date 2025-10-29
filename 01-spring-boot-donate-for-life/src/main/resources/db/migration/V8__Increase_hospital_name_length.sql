-- V8__Increase_hospital_name_length.sql

-- Increase the length of hospital_name in the requests table
ALTER TABLE requests MODIFY COLUMN hospital_name VARCHAR(255);

-- Increase the length of hospital_name in the donations table
ALTER TABLE donations MODIFY COLUMN hospital_name VARCHAR(255);