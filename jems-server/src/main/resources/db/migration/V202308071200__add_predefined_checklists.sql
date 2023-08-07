-- PR PROGRESS

INSERT IGNORE INTO programme_checklist (type, name, min_score, max_score, allows_decimal_score)
VALUES ('VERIFICATION','HIT - Project reports Monitoring checklist #TEMP_PR_PROGRESS#', '0.00', '10.00', '0');

SELECT id INTO @temp_checklist_id FROM programme_checklist WHERE programme_checklist.name LIKE 'HIT - Project reports Monitoring checklist #TEMP_PR_PROGRESS#';

INSERT IGNORE INTO programme_checklist_component (type, position_on_table, programme_checklist_id, metadata)
VALUES
    ('HEADLINE', 1, @temp_checklist_id, '{"value":"The main objectives of this document are:"}'),
    ('HEADLINE', 2, @temp_checklist_id, '{"value":"- for the programme to evaluate the progression of the project based on the submitted progress report"}'),
    ('HEADLINE', 3, @temp_checklist_id, '{"value":"- to check if the project is on track (outcomes, work plan, budget, communication),"}'),
    ('HEADLINE', 4, @temp_checklist_id, '{"value":"- preferably to check quality of project results/outputs and to check promised intensity of cooperation (involvement of partners),"}'),
    ('HEADLINE', 5, @temp_checklist_id, '{"value":"- to check stakeholders and target groups engagement,"}'),
    ('HEADLINE', 6, @temp_checklist_id, '{"value":"- to provide information for communication and publicity on programme level (also reporting to COM)."}'),
    ('HEADLINE', 0, @temp_checklist_id, '{"value":"Project Report - Monitoring checklist"}'),
    ('TEXT_INPUT', 7, @temp_checklist_id, '{"question":"Officer responsible for this checklist:","explanationLabel":"Please write the name of the person responsible","explanationMaxLength":1000}'),
    ('HEADLINE', 8, @temp_checklist_id, '{"value":"Highlights of main achievements"}'),
    ('OPTIONS_TOGGLE', 9, @temp_checklist_id, '{"question":"Has the project included a summary highlighting the added value of cooperation as well as its main achievements written in an easily understandable and engaging way? ","firstOption":"Yes","secondOption":"No"}'),
    ('HEADLINE', 10, @temp_checklist_id, '{"value":"Overview of the outputs and results achievement"}'),
    ('OPTIONS_TOGGLE', 11, @temp_checklist_id, '{"question":"How is the project progressing with regards to outputs, programme output indicators and programme result indicators? ","firstOption":"On track","secondOption":"Delayed","thirdOption":"Delivered"}'),
    ('OPTIONS_TOGGLE', 12, @temp_checklist_id, '{"question":"Is there any cause for concern (deviations, delays, low achievement levels, etc.) no addressed in section A.3 of the project progress report? ","firstOption":"Yes","secondOption":"No"}'),
    ('HEADLINE', 13, @temp_checklist_id, '{"value":"Project problems and deviations"}'),
    ('OPTIONS_TOGGLE', 14, @temp_checklist_id, '{"question":"Has the project indicated experiencing problems, issues, delays or deviations in the project progress report? ","firstOption":"Yes","secondOption":"No"}'),
    ('HEADLINE', 15, @temp_checklist_id, '{"value":"Target Groups"}'),
    ('OPTIONS_TOGGLE', 16, @temp_checklist_id, '{"question":"Is the project involving the target groups as indicated in the approved application form? ","firstOption":"Yes","secondOption":"No"}'),
    ('OPTIONS_TOGGLE', 17, @temp_checklist_id, '{"question":"Are there any communication activities linked to this worth noting?","firstOption":"Yes","secondOption":"No"}'),
    ('OPTIONS_TOGGLE', 18, @temp_checklist_id, '{"question":"How well is the project progressing in involving or reaching out to their chosen target groups? ","firstOption":"Yes","secondOption":"No"}'),
    ('HEADLINE', 19, @temp_checklist_id, '{"value":"Work plan progress"}'),
    ('OPTIONS_TOGGLE', 20, @temp_checklist_id, '{"question":"How well is the project progressing in relation to the approved work plan, including communication activities? ","firstOption":"On track","secondOption":"Delayed","thirdOption":"Delivered"}'),
    ('OPTIONS_TOGGLE', 21, @temp_checklist_id, '{"question":"How well is the project progressing in relation to the objectives described in the application form? ","firstOption":"On track","secondOption":"Delayed","thirdOption":"Delivered"}'),
    ('TEXT_INPUT', 22, @temp_checklist_id, '{"question":"How has the project involved the partners in the delivery of its planned activities? ","explanationLabel":"Enter text here","explanationMaxLength":5000}'),
    ('TEXT_INPUT', 23, @temp_checklist_id, '{"question":"How well is the project progressing in relation to delivery of its planned outputs? ","explanationLabel":"Enter text here","explanationMaxLength":5000}'),
    ('OPTIONS_TOGGLE', 24, @temp_checklist_id, '{"question":"In case there are investments in the project, Is the project delivering the planned investments? ","firstOption":"Yes","secondOption":"No"}'),
    ('OPTIONS_TOGGLE', 25, @temp_checklist_id, '{"question":"Is the added value of cooperation clear/ present? ","firstOption":"Yes","secondOption":"No"}'),
    ('OPTIONS_TOGGLE', 26, @temp_checklist_id, '{"question":"Are there any deviations not indicated in section A.4 of the project progress report that impact delivery of the planned investments? ","firstOption":"Yes","secondOption":"No"}'),
    ('HEADLINE', 27, @temp_checklist_id, '{"value":"Project results"}'),
    ('OPTIONS_TOGGLE', 28, @temp_checklist_id, '{"question":"How well is the project progressing towards their results? ","firstOption":"On track","secondOption":"Delayed","thirdOption":"Delivered"}'),
    ('HEADLINE', 29, @temp_checklist_id, '{"value":"Horizontal principles"}'),
    ('OPTIONS_TOGGLE', 30, @temp_checklist_id, '{"question":"Is the project respecting the horizontal principles of the programme (sustainable development, gender equality, non-discrimination)? ","firstOption":"Yes","secondOption":"No"}'),
    ('TEXT_INPUT', 31, @temp_checklist_id, '{"question":"Follow up actions ","explanationLabel":"Desk officer comments, if any","explanationMaxLength":5000}');
