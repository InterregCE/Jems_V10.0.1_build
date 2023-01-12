
-- remove entries from optimization table where projects are not approved anymore
delete from optimization_project_version where project_id NOT IN (
    select project_id from project_status where status = 'APPROVED'
);
