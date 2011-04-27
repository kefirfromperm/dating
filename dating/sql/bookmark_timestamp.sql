begin transaction;

-- bookmarks
ALTER TABLE dating.bookmark ADD COLUMN date_created timestamp without time zone not null default now();
ALTER TABLE dating.bookmark ADD COLUMN last_updated timestamp without time zone not null default now();

-- profiles
ALTER TABLE dating.profile ADD COLUMN date_created timestamp without time zone not null default now();
ALTER TABLE dating.profile ADD COLUMN last_updated timestamp without time zone not null default now();
update dating.profile set date_created = create_date;
update dating.profile set last_updated = create_date;
alter table dating.profile drop column create_date;
commit transaction;