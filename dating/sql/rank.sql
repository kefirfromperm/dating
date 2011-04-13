set search_path = dating;
begin transaction;

-- Create table rank
CREATE TABLE dating.rank(
  owner_id bigint NOT NULL,
  target_id bigint NOT NULL,
  rank integer NOT NULL DEFAULT 0,
  CONSTRAINT rank_pkey PRIMARY KEY (owner_id, target_id),
  CONSTRAINT fk_rank_owner FOREIGN KEY (owner_id)
      REFERENCES dating.profile (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_rank_target FOREIGN KEY (target_id)
      REFERENCES dating.profile (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);

-- Fill rank table
create or replace function dating.fill_rank() returns trigger as $$
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
$$ LANGUAGE plpgsql;

-- Create trigger on bookmark which fill rank table
drop trigger if exists fill_rank_trigger on dating.bookmark;
create trigger fill_rank_trigger after insert or update of status on dating.bookmark
for each row execute procedure dating.fill_rank();

commit transaction;

-- Like matrix between profiles
begin transaction;

-- Create table rank
CREATE TABLE dating.like_matrix (
  min_id bigint NOT NULL,
  max_id bigint NOT NULL,
  val integer NOT NULL DEFAULT 0,
  CONSTRAINT like_matrix_pkey PRIMARY KEY (min_id, max_id),
  CONSTRAINT fk_like_matrix_min FOREIGN KEY (min_id)
      REFERENCES dating.profile (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT fk_like_matrix_max FOREIGN KEY (max_id)
      REFERENCES dating.profile (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);

-- Calculate like distance between rank
create or replace function dating.like_distance(integer, integer) returns integer as $$
begin
    return case 
        when $1=$2 then 1 
        when ($1<0 and $2>0) or ($1>0 and $2<0) then -1
        else 0
    end;
end;
$$ LANGUAGE plpgsql;

-- Clear like matrix
create or replace function dating.clear_like_matrix() returns trigger as $$
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
$$ LANGUAGE plpgsql;


-- Fill like matrix
create or replace function dating.fill_like_matrix() returns trigger as $$
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
$$ LANGUAGE plpgsql;

-- Create triggers on rank which fill like matrix
drop trigger if exists clear_like_matrix_trigger on dating.rank;
create trigger clear_like_matrix_trigger before update of rank or delete on dating.rank
for each row execute procedure dating.clear_like_matrix();

drop trigger if exists fill_like_matrix_trigger on dating.rank;
create trigger fill_like_matrix_trigger after insert or update of rank on dating.rank
for each row execute procedure dating.fill_like_matrix();

commit transaction;