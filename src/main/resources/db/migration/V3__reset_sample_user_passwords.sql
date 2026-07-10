-- Password for both sample accounts: password
-- Kept as a new migration so existing environments receive the documented demo credentials.
UPDATE users
SET password = '$2a$10$MOsKR7vUpSfPSZUNlHn99.FJGWvp.tEKGPppyTQLd8yxx03DxfSdy'
WHERE username IN ('admin', 'staff');
