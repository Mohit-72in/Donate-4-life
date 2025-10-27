ALTER TABLE users
    ADD COLUMN latitude DOUBLE,
ADD COLUMN longitude DOUBLE;

ALTER TABLE requests
    ADD COLUMN latitude DOUBLE,
ADD COLUMN longitude DOUBLE;

ALTER TABLE donations
    ADD COLUMN latitude DOUBLE,
ADD COLUMN longitude DOUBLE;