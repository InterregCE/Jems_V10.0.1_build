package io.cloudflight.jems.server.controllerInstitution.service.model

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

data class InstitutionPartnerDetails(
    val institutionId: Long?,
    val partnerId: Long,
    val partnerName: String,
    val partnerStatus: Boolean,
    val partnerRole: ProjectPartnerRole,
    val partnerSortNumber: Int,
    val partnerNuts3: String?,
    val partnerAddress: String?,
    val callId: Long,
    val projectId: Long,
    val projectAcronym: String
)
