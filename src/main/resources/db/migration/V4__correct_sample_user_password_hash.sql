-- Correct the hash for databases that executed V2/V3 before the BCrypt value was fixed.
UPDATE users
SET password = '$2a$10$MOsKR7vUpSfPSZUNlHn99.FJGWvp.tEKGPppyTQLd8yxx03DxfSdy'
WHERE username IN ('admin', 'staff');
