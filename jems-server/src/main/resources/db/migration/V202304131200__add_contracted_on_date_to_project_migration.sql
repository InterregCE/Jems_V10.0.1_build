UPDATE project
    JOIN (SELECT *
          FROM (SELECT *, row_number() over (partition by project_id order by number desc) as row_num
                FROM project_contracting_monitoring_add_date pcmad) partition_result
          WHERE partition_result.row_num = 1) select_result
    ON project.id = select_result.project_id
SET project.contracted_on_date = select_result.entry_into_force_date;
