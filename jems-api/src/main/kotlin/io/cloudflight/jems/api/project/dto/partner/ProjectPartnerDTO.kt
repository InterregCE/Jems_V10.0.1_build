package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO

data class ProjectPartnerDTO(

    val id: Long?,
    val abbreviation: String?,
    val role: ProjectPartnerRoleDTO?,
    val nameInOriginalLanguage: String? = null,
    val nameInEnglish: String? = null,
    val department: Set<InputTranslation> = emptySet(),
    val partnerType: ProjectTargetGroupDTO? = null,
    val partnerSubType: PartnerSubTypeDTO? = null,
    val nace: NaceGroupLevelDTO? = null,
    val otherIdentifierNumber: String? = null,
    val otherIdentifierDescription: Set<InputTranslation> = emptySet(),
    val pic: String? = null,
    val legalStatusId: Long?,
    val vat: String? = null,
    val vatRecovery: ProjectPartnerVatRecoveryDTO? = null

)
