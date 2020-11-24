package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup

data class OutputProjectPartnerDetail (
    val id: Long?,
    val abbreviation: String,
    val role: ProjectPartnerRole,
    val sortNumber: Int? = null,
    val nameInOriginalLanguage: String?,
    val nameInEnglish: String?,
    val department: String?,
    val partnerType: ProjectTargetGroup?,
    val legalStatusId: Long?,
    val vat: String?,
    val vatRecovery: Boolean?,
    val addresses: List<ProjectPartnerAddressDTO> = emptyList(),
    val contacts: List<OutputProjectPartnerContact> = emptyList(),
    val motivation: ProjectPartnerMotivationDTO? = null,
)
