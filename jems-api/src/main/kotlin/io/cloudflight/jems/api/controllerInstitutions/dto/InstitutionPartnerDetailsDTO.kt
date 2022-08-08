package io.cloudflight.jems.api.controllerInstitutions.dto

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO

data class InstitutionPartnerDetailsDTO(
    val institutionId: Long?,
    val partnerId: Long,
    val partnerName: String,
    val partnerStatus: Boolean,
    val partnerRole: ProjectPartnerRoleDTO,
    val partnerSortNumber: Int,
    val partnerNuts3: String?,
    val partnerAddress: String?,
    val callId: Long,
    val projectId: Long,
    val projectAcronym: String
)
