CREATE TABLE project_call_selected_checklist
(
  project_call_id        int UNSIGNED NOT NULL,
  programme_checklist_id int UNSIGNED NOT NULL,
  PRIMARY KEY (project_call_id, programme_checklist_id),
  CONSTRAINT fk_project_call_selected_checklist_to_project_call FOREIGN KEY (project_call_id) REFERENCES project_call (id) ON DELETE CASCADE,
  CONSTRAINT fk_project_call_selected_checklist_to_programme_checklist FOREIGN KEY (programme_checklist_id) REFERENCES programme_checklist (id) ON DELETE CASCADE
);

INSERT INTO project_call_selected_checklist(project_call_id, programme_checklist_id)
SELECT project_call.id, programme_checklist.id
FROM project_call,
     programme_checklist;
