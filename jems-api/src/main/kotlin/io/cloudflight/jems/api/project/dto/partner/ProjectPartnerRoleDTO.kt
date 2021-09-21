package io.cloudflight.jems.api.project.dto.partner

enum class ProjectPartnerRoleDTO(val isLead: Boolean) {
    PARTNER(false),
    LEAD_PARTNER(true);

}
