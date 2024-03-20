INSERT IGNORE INTO programme_checklist ( type,name,min_score,max_score,allows_decimal_score)
VALUES ('CLOSURE','HIT - Project final report #TEMP_Final_Report#','0.00','10.00','0');

SELECT id INTO @temp_checklist_id FROM programme_checklist WHERE programme_checklist.name LIKE 'HIT - Project final report #TEMP_Final_Report#';

INSERT IGNORE INTO programme_checklist_component (type, position_on_table, programme_checklist_id, metadata)
VALUES
  ('HEADLINE', 6, @temp_checklist_id, '{"value":"Cooperation should not only concern partners and associated partners but also cooperation with stakeholders."}'),
  ('HEADLINE', 1, @temp_checklist_id, '{"value":"D1 Outputs after project end"}'),
  ('OPTIONS_TOGGLE', 14, @temp_checklist_id, '{"question":"Targeted problems have been solved within project time frame","firstOption":"Yes","secondOption":"No"}'),
  ('TEXT_INPUT', 20, @temp_checklist_id, '{"question":"E.1 Cooperation with the Programme bodies\\nPlease give feedback on your experience with cooperating with the programme bodies (MA, JS, Contact Points, Controllers, …)","explanationLabel":"Enter text here","explanationMaxLength":5000}'),
  ('HEADLINE', 0, @temp_checklist_id, '{"value":"Part D - Future outlook"}'),
  ('OPTIONS_TOGGLE', 10, @temp_checklist_id, '{"question":"Follow-up projects\\n(if yes, please describe them briefly and give number of the follow-up projects and possible funding sources e.g. European Territorial Cooperation, national public funds, national private funds, other EU funds)\\n","firstOption":"Yes","secondOption":"No"}'),
  ('TEXT_INPUT', 2, @temp_checklist_id, '{"question":"D.1.1 What will happen with project outputs after the project end and how will the outputs be made available to the general public? \\nPlease describe how the outputs will be maintained and developed further after project end and where they will be made available.","explanationLabel":"Enter text here","explanationMaxLength":5000}'),
  ('HEADLINE', 13, @temp_checklist_id, '{"value":"If you do not plan to continue the cross-border partnership established by the project please indicate the main reasons."}'),
  ('HEADLINE', 4, @temp_checklist_id, '{"value":"D2.2 Cooperation beyond this project"}'),
  ('OPTIONS_TOGGLE', 11, @temp_checklist_id, '{"question":"Other (please specify)","firstOption":"Yes","secondOption":"No"}'),
  ('HEADLINE', 22, @temp_checklist_id, '{"value":"Part F – Project Documentation \\u0026 public contribution follow-up"}'),
  ('TEXT_INPUT', 23, @temp_checklist_id, '{"question":"F.1 funding of external public sources\\nPlease list any external public funding that the project partners received. Please clearly indicate the partner number, name, funding source and the amount.","explanationLabel":"Enter text here","explanationMaxLength":5000}'),
  ('HEADLINE', 5, @temp_checklist_id, '{"value":"Please indicate by Yes or No how you will continue the cooperation beyond this project (to ensure durability of project outputs). "}'),
  ('HEADLINE', 12, @temp_checklist_id, '{"value":"D2.3 No cooperation beyond this project "}'),
  ('OPTIONS_TOGGLE', 7, @temp_checklist_id, '{"question":"Institutional structures for maintaining of the project results\\n(if yes, please indicate what structures for maintenance of the project results were established, which partners will support them financially, what will be the tasks for such structures)","firstOption":"Yes","secondOption":"No"}'),
  ('TEXT_INPUT', 21, @temp_checklist_id, '{"question":"E.2 Obstacles faced during the implementation of the project.\\nPlease give feedback on your experience with the legislative framework, programme rules and guidance. In case you faced any obstacles that held you back in project implementation please indicate it here.","explanationLabel":"Enter text here","explanationMaxLength":5000}'),
  ('OPTIONS_TOGGLE', 16, @temp_checklist_id, '{"question":"Partners have no interest to continue. ","firstOption":"Yes","secondOption":"No"}'),
  ('OPTIONS_TOGGLE', 24, @temp_checklist_id, '{"question":"F.2 Document storage\\nPlease acknowledge that the conditions of the subsidy contract have been complied with and that the information on the location of project documents after project end has been updated for each project partner (Section in Supplementary information template (Contracting section in Jems))","firstOption":"I acknowledge","secondOption":"I do not acknowledge"}'),
  ('OPTIONS_TOGGLE', 17, @temp_checklist_id, '{"question":"Other (please specify)","firstOption":"Yes","secondOption":"No"}'),
  ('OPTIONS_TOGGLE', 8, @temp_checklist_id, '{"question":"Long run action plan for maintaining the project results\\n(if yes, please indicate by whom an action plan has been agreed and what is its time horizon)\\n","firstOption":"Yes","secondOption":"No"}'),
  ('OPTIONS_TOGGLE', 3, @temp_checklist_id, '{"question":"D.2.1 Do you plan to continue cooperation beyond this project?\\nIf the answer is yes, please fill in section D2.2 If the answer is no please complete D2.3\\n","firstOption":"Yes","secondOption":"No"}'),
  ('HEADLINE', 19, @temp_checklist_id, '{"value":"Part E – Feedback on the Programme"}'),
  ('TEXT_INPUT', 18, @temp_checklist_id, '{"question":"D.3. Contribution to wider policies and strategies\\nCompared to what was mentioned in the Application Form how do you now contribute to wider policies and strategies?","explanationLabel":"Enter text here","explanationMaxLength":5000}'),
  ('OPTIONS_TOGGLE', 9, @temp_checklist_id, '{"question":"Adjustment of the regional/national strategic documents or/and planning procedures for maintaining the project results\\n(if yes, please indicate the nature of such adjustments and names of documents and procedures affected)\\n","firstOption":"Yes","secondOption":"No"}'),
  ('OPTIONS_TOGGLE', 15, @temp_checklist_id, '{"question":"Partnership has not turned out to be very successful","firstOption":"Yes","secondOption":"No"}');
