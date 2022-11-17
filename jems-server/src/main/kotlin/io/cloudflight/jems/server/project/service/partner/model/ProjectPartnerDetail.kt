package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import java.time.ZonedDateTime

data class ProjectPartnerDetail(
    val projectId: Long,
    val id: Long,
    val active: Boolean,
    val abbreviation: String,
    val role: ProjectPartnerRole,
    val createdAt: ZonedDateTime,
    val sortNumber: Int? = null,
    val nameInOriginalLanguage: String?,
    val nameInEnglish: String?,
    val department: Set<InputTranslation> = emptySet(),
    val partnerType: ProjectTargetGroup?,
    val partnerSubType: PartnerSubType?,
    val nace: NaceGroupLevel?,
    val otherIdentifierNumber: String?,
    val otherIdentifierDescription: Set<InputTranslation> = emptySet(),
    val pic: String?,
    val legalStatusId: Long?,
    val vat: String?,
    val vatRecovery: ProjectPartnerVatRecovery?,
    val addresses: List<ProjectPartnerAddress> = emptyList(),
    val contacts: List<ProjectPartnerContact> = emptyList(),
    val motivation: ProjectPartnerMotivation? = null,
) {
    fun toSummary() = ProjectPartnerSummary(
        id = id,
        abbreviation = abbreviation,
        active = active,
        role = role,
        sortNumber = sortNumber,
        country = addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.country,
        region = addresses.firstOrNull { it.type == ProjectPartnerAddressType.Organization }?.nutsRegion3,
    )
}
