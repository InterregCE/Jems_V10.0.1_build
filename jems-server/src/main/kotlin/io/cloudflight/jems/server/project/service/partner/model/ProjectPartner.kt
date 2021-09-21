package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup

data class ProjectPartner(

    val id: Long?,
    val abbreviation: String?,
    val role: ProjectPartnerRole?,
    val nameInOriginalLanguage: String? = null,
    val nameInEnglish: String? = null,
    val department: Set<InputTranslation> = emptySet(),
    val partnerType: ProjectTargetGroup? = null,
    val partnerSubType: PartnerSubType? = null,
    val nace: NaceGroupLevel? = null,
    val otherIdentifierNumber: String? = null,
    val otherIdentifierDescription: Set<InputTranslation> = emptySet(),
    val pic: String? = null,
    val legalStatusId: Long?,
    val vat: String? = null,
    val vatRecovery: ProjectPartnerVatRecovery? = null

)
