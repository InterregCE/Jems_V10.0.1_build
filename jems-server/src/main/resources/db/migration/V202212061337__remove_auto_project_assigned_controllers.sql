-- remove previously auto assigned controller users without monitor AF permission
DELETE FROM account_project
WHERE account_id IN (select user_id FROM controller_institution_user)
  AND account_id NOT IN (SELECT a.id FROM account a, account_role_permission p
                         WHERE a.account_role_id = p.account_role_id AND p.permission = 'ProjectRetrieve');
