package io.cloudflight.jems.api.project.dto.partner

data class ProjectPartnerContractingSummaryDTO(
    val id: Long?,
    val abbreviation: String,
    val institutionName: String?,
    val active: Boolean,
    val role: ProjectPartnerRoleDTO,
    val sortNumber: Int? = null,
    val country: String? = null,
    val region: String? = null,
    val currencyCode: String? = null,
    val locked: Boolean
)
