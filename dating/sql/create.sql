--
-- PostgreSQL database dump
--

-- Dumped from database version 9.0.1
-- Dumped by pg_dump version 9.0.1
-- Started on 2011-04-23 16:51:37

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 1897 (class 1262 OID 181098)
-- Name: dating; Type: DATABASE; Schema: -; Owner: -
--

CREATE DATABASE dating WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'English, United States' LC_CTYPE = 'English, United States';


\connect dating

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 7 (class 2615 OID 183995)
-- Name: dating; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA dating;


--
-- TOC entry 354 (class 2612 OID 11574)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: -; Owner: -
--

CREATE OR REPLACE PROCEDURAL LANGUAGE plpgsql;


SET search_path = dating, pg_catalog;

--
-- TOC entry 22 (class 1255 OID 217204)
-- Dependencies: 7 354
-- Name: clear_like_matrix(); Type: FUNCTION; Schema: dating; Owner: -
--

CREATE FUNCTION clear_like_matrix() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
begin
    -- Sub
    update dating.like_matrix as lm set val = val-dating.like_distance(r.rank, OLD.rank)
    from dating.rank as r
    where r.target_id=OLD.target_id 
    and ((lm.min_id=r.owner_id and lm.max_id=OLD.owner_id) or (lm.min_id=OLD.owner_id and lm.max_id=r.owner_id));

    if TG_OP='DELETE' then
        return OLD;
    else
        return NEW;
    end if;
end;
$$;


--
-- TOC entry 21 (class 1255 OID 217243)
-- Dependencies: 7 354
-- Name: clear_rank(); Type: FUNCTION; Schema: dating; Owner: -
--

CREATE FUNCTION clear_rank() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
begin
    delete from dating.rank where owner_id=OLD.id or target_id=OLD.id;

    return OLD;
end;
$$;


--
-- TOC entry 23 (class 1255 OID 217201)
-- Dependencies: 354 7
-- Name: fill_like_matrix(); Type: FUNCTION; Schema: dating; Owner: -
--

CREATE FUNCTION fill_like_matrix() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
begin
    -- Fill empty cells
    if TG_OP='INSERT' then
	insert into dating.like_matrix (min_id, max_id)
	select r1.owner_id, r2.owner_id
	from dating.rank as r1 join dating.rank as r2 on r1.target_id=r2.target_id
	where (r1.owner_id=NEW.owner_id or r2.owner_id=NEW.owner_id) and r1.owner_id<r2.owner_id
	and not exists (
	    select * from dating.like_matrix 
	    where min_id=r1.owner_id and max_id=r2.owner_id
        );
    end if;

    -- add
    update dating.like_matrix as lm set val = val+dating.like_distance(r.rank, NEW.rank)
    from dating.rank as r
    where r.target_id=NEW.target_id 
    and ((lm.min_id=r.owner_id and lm.max_id=NEW.owner_id) or (lm.min_id=NEW.owner_id and lm.max_id=r.owner_id));

    return NEW;
end;
$$;


--
-- TOC entry 20 (class 1255 OID 217089)
-- Dependencies: 7 354
-- Name: fill_rank(); Type: FUNCTION; Schema: dating; Owner: -
--

CREATE FUNCTION fill_rank() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
declare
    nrank integer;
begin
    nrank := case when NEW.status='CONFIRMED' then 1 when NEW.status='BAN' then -1 else 0 end;

    if exists(select * from dating.rank where owner_id=NEW.owner_id and target_id=NEW.target_id) then
	update dating.rank set rank=nrank where owner_id=NEW.owner_id and target_id=NEW.target_id;
    else
        insert into dating.rank(owner_id, target_id, rank) values(NEW.owner_id, NEW.target_id, nrank);
    end if;

    return NEW;
end;
$$;


--
-- TOC entry 19 (class 1255 OID 217200)
-- Dependencies: 354 7
-- Name: like_distance(integer, integer); Type: FUNCTION; Schema: dating; Owner: -
--

CREATE FUNCTION like_distance(integer, integer) RETURNS integer
    LANGUAGE plpgsql
    AS $_$
begin
    return case 
        when $1=$2 then 1 
        when ($1<0 and $2>0) or ($1>0 and $2<0) then -1
        else 0
    end;
end;
$_$;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1546 (class 1259 OID 184608)
-- Dependencies: 7
-- Name: account; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE account (
    id bigint NOT NULL,
    version bigint NOT NULL,
    confirm_code character varying(80),
    date timestamp without time zone NOT NULL,
    enabled boolean NOT NULL,
    locked boolean NOT NULL,
    mail character varying(320) NOT NULL,
    password_digest character varying(80) NOT NULL,
    salt character varying(40)
);


--
-- TOC entry 1547 (class 1259 OID 184617)
-- Dependencies: 7
-- Name: account_role; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE account_role (
    account_roles_id bigint,
    role_id bigint
);


--
-- TOC entry 1548 (class 1259 OID 184620)
-- Dependencies: 7
-- Name: asynchronous_mail_attachment; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE asynchronous_mail_attachment (
    id bigint NOT NULL,
    version bigint NOT NULL,
    attachment_name character varying(255) NOT NULL,
    content bytea NOT NULL,
    message_id bigint NOT NULL,
    mime_type character varying(255) NOT NULL,
    attachments_idx integer
);


--
-- TOC entry 1549 (class 1259 OID 184628)
-- Dependencies: 7
-- Name: asynchronous_mail_message; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE asynchronous_mail_message (
    id bigint NOT NULL,
    version bigint NOT NULL,
    attempt_interval bigint NOT NULL,
    attempts_count integer NOT NULL,
    begin_date timestamp without time zone NOT NULL,
    create_date timestamp without time zone NOT NULL,
    end_date timestamp without time zone NOT NULL,
    from_column character varying(255),
    html boolean NOT NULL,
    last_attempt_date timestamp without time zone,
    mark_delete boolean NOT NULL,
    max_attempts_count integer NOT NULL,
    reply_to character varying(255),
    sent_date timestamp without time zone,
    status character varying(255) NOT NULL,
    subject character varying(255) NOT NULL,
    text text NOT NULL
);


--
-- TOC entry 1550 (class 1259 OID 184636)
-- Dependencies: 7
-- Name: asynchronous_mail_message_bcc; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE asynchronous_mail_message_bcc (
    asynchronous_mail_message_id bigint,
    bcc_string character varying(255),
    bcc_idx integer
);


--
-- TOC entry 1551 (class 1259 OID 184639)
-- Dependencies: 7
-- Name: asynchronous_mail_message_cc; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE asynchronous_mail_message_cc (
    asynchronous_mail_message_id bigint,
    cc_string character varying(255),
    cc_idx integer
);


--
-- TOC entry 1552 (class 1259 OID 184642)
-- Dependencies: 7
-- Name: asynchronous_mail_message_headers; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE asynchronous_mail_message_headers (
    headers bigint,
    headers_idx character varying(255),
    headers_elt character varying(255) NOT NULL
);


--
-- TOC entry 1553 (class 1259 OID 184648)
-- Dependencies: 7
-- Name: asynchronous_mail_message_to; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE asynchronous_mail_message_to (
    asynchronous_mail_message_id bigint NOT NULL,
    to_string character varying(255),
    to_idx integer
);


--
-- TOC entry 1554 (class 1259 OID 184651)
-- Dependencies: 7
-- Name: bi_images; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE bi_images (
    id bigint NOT NULL,
    version bigint NOT NULL,
    data bytea NOT NULL,
    type character varying(255) NOT NULL
);


--
-- TOC entry 1555 (class 1259 OID 184659)
-- Dependencies: 7
-- Name: bookmark; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE bookmark (
    id bigint NOT NULL,
    version bigint NOT NULL,
    incoming integer NOT NULL,
    owner_id bigint NOT NULL,
    status character varying(255) NOT NULL,
    target_id bigint NOT NULL
);


--
-- TOC entry 1561 (class 1259 OID 184757)
-- Dependencies: 7
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: dating; Owner: -
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 1563 (class 1259 OID 217184)
-- Dependencies: 1842 7
-- Name: like_matrix; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE like_matrix (
    min_id bigint NOT NULL,
    max_id bigint NOT NULL,
    val integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 1556 (class 1259 OID 184666)
-- Dependencies: 7
-- Name: message; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE message (
    id bigint NOT NULL,
    date timestamp without time zone NOT NULL,
    from_id bigint NOT NULL,
    text character varying(511) NOT NULL,
    to_id bigint NOT NULL
);


--
-- TOC entry 1557 (class 1259 OID 184674)
-- Dependencies: 7
-- Name: profile; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE profile (
    id bigint NOT NULL,
    version bigint NOT NULL,
    about text,
    account_id bigint NOT NULL,
    alias character varying(63) NOT NULL,
    create_date timestamp without time zone NOT NULL,
    name character varying(255) NOT NULL,
    photo_id bigint,
    use_gravatar boolean NOT NULL
);


--
-- TOC entry 1562 (class 1259 OID 217075)
-- Dependencies: 1841 7
-- Name: rank; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE rank (
    owner_id bigint NOT NULL,
    target_id bigint NOT NULL,
    rank integer DEFAULT 0 NOT NULL
);


--
-- TOC entry 1558 (class 1259 OID 184684)
-- Dependencies: 7
-- Name: role; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE role (
    id bigint NOT NULL,
    authority character varying(255) NOT NULL
);


--
-- TOC entry 1559 (class 1259 OID 184691)
-- Dependencies: 7
-- Name: saga_file; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE saga_file (
    id bigint NOT NULL,
    date timestamp without time zone NOT NULL,
    file_content_id bigint NOT NULL,
    link_alias character varying(255),
    mimetype character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    size bigint NOT NULL
);


--
-- TOC entry 1560 (class 1259 OID 184699)
-- Dependencies: 7
-- Name: saga_file_content; Type: TABLE; Schema: dating; Owner: -; Tablespace: 
--

CREATE TABLE saga_file_content (
    id bigint NOT NULL,
    content bytea NOT NULL
);


--
-- TOC entry 1844 (class 2606 OID 184614)
-- Dependencies: 1546 1546
-- Name: account_confirm_code_key; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_confirm_code_key UNIQUE (confirm_code);


--
-- TOC entry 1846 (class 2606 OID 184616)
-- Dependencies: 1546 1546
-- Name: account_mail_key; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_mail_key UNIQUE (mail);


--
-- TOC entry 1848 (class 2606 OID 184612)
-- Dependencies: 1546 1546
-- Name: account_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);


--
-- TOC entry 1850 (class 2606 OID 184627)
-- Dependencies: 1548 1548
-- Name: asynchronous_mail_attachment_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY asynchronous_mail_attachment
    ADD CONSTRAINT asynchronous_mail_attachment_pkey PRIMARY KEY (id);


--
-- TOC entry 1852 (class 2606 OID 184635)
-- Dependencies: 1549 1549
-- Name: asynchronous_mail_message_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY asynchronous_mail_message
    ADD CONSTRAINT asynchronous_mail_message_pkey PRIMARY KEY (id);


--
-- TOC entry 1854 (class 2606 OID 184658)
-- Dependencies: 1554 1554
-- Name: bi_images_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY bi_images
    ADD CONSTRAINT bi_images_pkey PRIMARY KEY (id);


--
-- TOC entry 1856 (class 2606 OID 184665)
-- Dependencies: 1555 1555 1555
-- Name: bookmark_owner_id_target_id_key; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY bookmark
    ADD CONSTRAINT bookmark_owner_id_target_id_key UNIQUE (owner_id, target_id);


--
-- TOC entry 1858 (class 2606 OID 184663)
-- Dependencies: 1555 1555
-- Name: bookmark_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY bookmark
    ADD CONSTRAINT bookmark_pkey PRIMARY KEY (id);


--
-- TOC entry 1876 (class 2606 OID 217189)
-- Dependencies: 1563 1563 1563
-- Name: like_matrix_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY like_matrix
    ADD CONSTRAINT like_matrix_pkey PRIMARY KEY (min_id, max_id);


--
-- TOC entry 1860 (class 2606 OID 184673)
-- Dependencies: 1556 1556
-- Name: message_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY message
    ADD CONSTRAINT message_pkey PRIMARY KEY (id);


--
-- TOC entry 1862 (class 2606 OID 184683)
-- Dependencies: 1557 1557
-- Name: profile_account_id_key; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT profile_account_id_key UNIQUE (account_id);


--
-- TOC entry 1864 (class 2606 OID 184681)
-- Dependencies: 1557 1557
-- Name: profile_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT profile_pkey PRIMARY KEY (id);


--
-- TOC entry 1874 (class 2606 OID 217079)
-- Dependencies: 1562 1562 1562
-- Name: rank_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY rank
    ADD CONSTRAINT rank_pkey PRIMARY KEY (owner_id, target_id);


--
-- TOC entry 1866 (class 2606 OID 184690)
-- Dependencies: 1558 1558
-- Name: role_authority_key; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_authority_key UNIQUE (authority);


--
-- TOC entry 1868 (class 2606 OID 184688)
-- Dependencies: 1558 1558
-- Name: role_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 1872 (class 2606 OID 184706)
-- Dependencies: 1560 1560
-- Name: saga_file_content_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY saga_file_content
    ADD CONSTRAINT saga_file_content_pkey PRIMARY KEY (id);


--
-- TOC entry 1870 (class 2606 OID 184698)
-- Dependencies: 1559 1559
-- Name: saga_file_pkey; Type: CONSTRAINT; Schema: dating; Owner: -; Tablespace: 
--

ALTER TABLE ONLY saga_file
    ADD CONSTRAINT saga_file_pkey PRIMARY KEY (id);


--
-- TOC entry 1893 (class 2620 OID 217207)
-- Dependencies: 22 1562 1562
-- Name: clear_like_matrix_trigger; Type: TRIGGER; Schema: dating; Owner: -
--

CREATE TRIGGER clear_like_matrix_trigger BEFORE DELETE OR UPDATE OF rank ON rank FOR EACH ROW EXECUTE PROCEDURE clear_like_matrix();


--
-- TOC entry 1892 (class 2620 OID 217244)
-- Dependencies: 1557 21
-- Name: clear_rank_trigger; Type: TRIGGER; Schema: dating; Owner: -
--

CREATE TRIGGER clear_rank_trigger BEFORE DELETE ON profile FOR EACH ROW EXECUTE PROCEDURE clear_rank();


--
-- TOC entry 1894 (class 2620 OID 217208)
-- Dependencies: 23 1562 1562
-- Name: fill_like_matrix_trigger; Type: TRIGGER; Schema: dating; Owner: -
--

CREATE TRIGGER fill_like_matrix_trigger AFTER INSERT OR UPDATE OF rank ON rank FOR EACH ROW EXECUTE PROCEDURE fill_like_matrix();


--
-- TOC entry 1891 (class 2620 OID 217101)
-- Dependencies: 1555 20 1555
-- Name: fill_rank_trigger; Type: TRIGGER; Schema: dating; Owner: -
--

CREATE TRIGGER fill_rank_trigger AFTER INSERT OR UPDATE OF status ON bookmark FOR EACH ROW EXECUTE PROCEDURE fill_rank();


--
-- TOC entry 1882 (class 2606 OID 217219)
-- Dependencies: 1557 1863 1556
-- Name: fk38eb00072ef82842; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY message
    ADD CONSTRAINT fk38eb00072ef82842 FOREIGN KEY (from_id) REFERENCES profile(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1883 (class 2606 OID 217224)
-- Dependencies: 1556 1557 1863
-- Name: fk38eb000758fb73d1; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY message
    ADD CONSTRAINT fk38eb000758fb73d1 FOREIGN KEY (to_id) REFERENCES profile(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1877 (class 2606 OID 184707)
-- Dependencies: 1558 1867 1547
-- Name: fk410d03483dfe15d1; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY account_role
    ADD CONSTRAINT fk410d03483dfe15d1 FOREIGN KEY (role_id) REFERENCES role(id);


--
-- TOC entry 1878 (class 2606 OID 184712)
-- Dependencies: 1547 1847 1546
-- Name: fk410d03483fa9bea5; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY account_role
    ADD CONSTRAINT fk410d03483fa9bea5 FOREIGN KEY (account_roles_id) REFERENCES account(id);


--
-- TOC entry 1880 (class 2606 OID 217209)
-- Dependencies: 1863 1555 1557
-- Name: fk7787a53621c830fb; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY bookmark
    ADD CONSTRAINT fk7787a53621c830fb FOREIGN KEY (target_id) REFERENCES profile(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1881 (class 2606 OID 217214)
-- Dependencies: 1557 1555 1863
-- Name: fk7787a536b5868499; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY bookmark
    ADD CONSTRAINT fk7787a536b5868499 FOREIGN KEY (owner_id) REFERENCES profile(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1890 (class 2606 OID 217195)
-- Dependencies: 1563 1863 1557
-- Name: fk_like_matrix_max; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY like_matrix
    ADD CONSTRAINT fk_like_matrix_max FOREIGN KEY (max_id) REFERENCES profile(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1889 (class 2606 OID 217190)
-- Dependencies: 1557 1563 1863
-- Name: fk_like_matrix_min; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY like_matrix
    ADD CONSTRAINT fk_like_matrix_min FOREIGN KEY (min_id) REFERENCES profile(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1887 (class 2606 OID 217102)
-- Dependencies: 1863 1562 1557
-- Name: fk_rank_owner; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY rank
    ADD CONSTRAINT fk_rank_owner FOREIGN KEY (owner_id) REFERENCES profile(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1888 (class 2606 OID 217107)
-- Dependencies: 1557 1562 1863
-- Name: fk_rank_target; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY rank
    ADD CONSTRAINT fk_rank_target FOREIGN KEY (target_id) REFERENCES profile(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1879 (class 2606 OID 184717)
-- Dependencies: 1548 1851 1549
-- Name: fkd4396ab474651218; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY asynchronous_mail_attachment
    ADD CONSTRAINT fkd4396ab474651218 FOREIGN KEY (message_id) REFERENCES asynchronous_mail_message(id);


--
-- TOC entry 1886 (class 2606 OID 184752)
-- Dependencies: 1560 1871 1559
-- Name: fkeaa277b38a1ae786; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY saga_file
    ADD CONSTRAINT fkeaa277b38a1ae786 FOREIGN KEY (file_content_id) REFERENCES saga_file_content(id);


--
-- TOC entry 1885 (class 2606 OID 184747)
-- Dependencies: 1869 1559 1557
-- Name: fked8e89a972f3907f; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT fked8e89a972f3907f FOREIGN KEY (photo_id) REFERENCES saga_file(id);


--
-- TOC entry 1884 (class 2606 OID 184742)
-- Dependencies: 1557 1546 1847
-- Name: fked8e89a9eec80363; Type: FK CONSTRAINT; Schema: dating; Owner: -
--

ALTER TABLE ONLY profile
    ADD CONSTRAINT fked8e89a9eec80363 FOREIGN KEY (account_id) REFERENCES account(id);


-- Completed on 2011-04-23 16:51:38

--
-- PostgreSQL database dump complete
--

-- Privileges
grant connect on database dating to dating;
grant usage on schema dating to dating;
grant all on all sequences in schema dating to dating;
grant select,insert,update,delete on all tables in schema dating to dating;
