begin transaction;
CREATE TABLE dating.notification
(
  id bigint NOT NULL,
  "version" bigint NOT NULL,
  date_created timestamp without time zone NOT NULL,
  recipient_id bigint NOT NULL,
  sent_date timestamp without time zone,
  status character varying(15) NOT NULL,
  "class" character varying(63) NOT NULL,
  message_id bigint,
  CONSTRAINT notification_pkey PRIMARY KEY (id),
  CONSTRAINT fk237a88eb303886cc FOREIGN KEY (message_id)
      REFERENCES dating.message (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk237a88eb95008d1c FOREIGN KEY (recipient_id)
      REFERENCES dating.profile (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);
grant select,insert,update,delete on dating.notification to dating;
commit transaction;