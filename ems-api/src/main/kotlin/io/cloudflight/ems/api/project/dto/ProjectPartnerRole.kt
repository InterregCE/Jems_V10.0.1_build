package io.cloudflight.ems.api.project.dto

enum class ProjectPartnerRole {
    PARTNER,
    LEAD_PARTNER;

    companion object {

        fun isLeadPartner(role: ProjectPartnerRole): Boolean {
            return role == LEAD_PARTNER
        }
    }
}
