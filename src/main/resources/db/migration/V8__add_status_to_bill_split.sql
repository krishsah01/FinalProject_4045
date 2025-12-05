ALTER TABLE bill_split ADD COLUMN status VARCHAR(20) DEFAULT 'UNPAID' NOT NULL;

UPDATE bill_split SET status = 'PAID' WHERE isPaid = true;
UPDATE bill_split SET status = 'UNPAID' WHERE isPaid = false;

ALTER TABLE bill_split DROP COLUMN isPaid;
