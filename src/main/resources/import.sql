-- Insert locations
INSERT INTO locations (city, country) VALUES
('Novi Sad', 'Serbia'),
('Subotica', 'Serbia');

INSERT INTO accommodation_features (feature) VALUES
('Free Wifi'),
('Kitchen'),
('AC'),
('Free Parking'),
('Pet friendly'),
('Pool');

INSERT INTO accommodations (name, location_id, photograph_url, minimum_no_guests, maximum_no_guests, host_email, price, is_price_per_guest, terminated)
VALUES ('Test Accommodation', 1, 'apartment.webp', 1, 4, 'pera@gmail.com', 100.00, FALSE, FALSE);
