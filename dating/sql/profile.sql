-- Clear rank before profile delete
create or replace function dating.clear_rank() returns trigger as 
$$
begin
    delete from dating.rank where owner_id=OLD.id or target_id=OLD.id;

    return OLD;
end;
$$ LANGUAGE plpgsql;

-- Create triggers on rank which fill like matrix
drop trigger if exists clear_rank_trigger on dating.profile;
create trigger clear_rank_trigger before delete on dating.profile
for each row execute procedure dating.clear_rank();
