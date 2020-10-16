package io.cloudflight.jems.api.project.dto.partner

enum class ProjectPartnerRole(val isLead: Boolean) {
    PARTNER(false),
    LEAD_PARTNER(true);

}
