begin transaction;
ALTER TABLE dating.bookmark ADD COLUMN date_created timestamp without time zone not null default now();
ALTER TABLE dating.bookmark ADD COLUMN last_updated timestamp without time zone not null default now();
commit transaction;