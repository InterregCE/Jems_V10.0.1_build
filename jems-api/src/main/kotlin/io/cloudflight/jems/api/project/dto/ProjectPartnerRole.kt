package io.cloudflight.jems.api.project.dto

enum class ProjectPartnerRole(val isLead: Boolean) {
    PARTNER(false),
    LEAD_PARTNER(true);

}
