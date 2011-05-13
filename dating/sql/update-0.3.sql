begin transaction;
CREATE TABLE dating.message_notification
(
  id bigint NOT NULL,
  "version" bigint NOT NULL,
  date_created timestamp without time zone NOT NULL,
  message_id bigint NOT NULL,
  recipient_id bigint NOT NULL,
  sent_date timestamp without time zone,
  status character varying(15) NOT NULL,
  CONSTRAINT message_notification_pkey PRIMARY KEY (id),
  CONSTRAINT fkb01f7623303886cc FOREIGN KEY (message_id)
      REFERENCES dating.message (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fkb01f762395008d1c FOREIGN KEY (recipient_id)
      REFERENCES dating.profile (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);
grant select,insert,update,delete on dating.message_notification to dating;
commit transaction;