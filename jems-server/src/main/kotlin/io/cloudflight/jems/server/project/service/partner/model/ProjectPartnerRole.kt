package io.cloudflight.jems.server.project.service.partner.model

enum class ProjectPartnerRole(val isLead: Boolean) {
    PARTNER(false),
    LEAD_PARTNER(true);

}
