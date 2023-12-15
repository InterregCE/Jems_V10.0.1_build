UPDATE report_project rp
    LEFT JOIN project p ON rp.project_id = p.id
    LEFT JOIN project_call c ON p.project_call_id = c.id
    LEFT JOIN project_partner pp ON p.id = pp.project_id AND pp.active
SET rp.spf_partner_id = pp.id
WHERE c.type = 'SPF' AND rp.spf_partner_id IS NULL;
