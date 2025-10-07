ALTER TABLE donations
ADD COLUMN verification_status VARCHAR(20) NOT NULL DEFAULT 'UNVERIFIED';

-- We add a link from credit back to the donation to prevent creating duplicate credits
ALTER TABLE credits
ADD COLUMN donation_id INT,
ADD CONSTRAINT fk_donation_id FOREIGN KEY (donation_id) REFERENCES donations(donation_id);