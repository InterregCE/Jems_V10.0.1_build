package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import java.time.ZonedDateTime

data class ProjectPartnerDetailDTO (
    val id: Long,
    val abbreviation: String,
    val active: Boolean,
    val role: ProjectPartnerRoleDTO,
    val createdAt: ZonedDateTime,
    val sortNumber: Int? = null,
    val nameInOriginalLanguage: String?,
    val nameInEnglish: String?,
    val department: Set<InputTranslation> = emptySet(),
    val partnerType: ProjectTargetGroupDTO?,
    val partnerSubType: PartnerSubTypeDTO?,
    val nace: NaceGroupLevelDTO?,
    val otherIdentifierNumber: String?,
    val otherIdentifierDescription: Set<InputTranslation> = emptySet(),
    val pic: String?,
    val legalStatusId: Long?,
    val vat: String?,
    val vatRecovery: ProjectPartnerVatRecoveryDTO?,
    val addresses: List<ProjectPartnerAddressDTO> = emptyList(),
    val contacts: List<ProjectPartnerContactDTO> = emptyList(),
    val motivation: ProjectPartnerMotivationDTO? = null
)
