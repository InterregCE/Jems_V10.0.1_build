ALTER TABLE project_data
  ADD COLUMN priority_policy_id VARCHAR (127) AFTER duration,
  ADD CONSTRAINT fk_project_priority_policy_call_priority_policy
      FOREIGN KEY (priority_policy_id)
          REFERENCES project_call_priority_policy (programme_priority_policy)
          ON DELETE RESTRICT
          ON UPDATE RESTRICT;
