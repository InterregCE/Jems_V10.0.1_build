package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroup
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO
import io.cloudflight.jems.server.common.entity.TranslationView

interface PartnerIdentityRow: TranslationView {
    val id: Long
    val projectId: Long
    val abbreviation: String
    val role: ProjectPartnerRoleDTO
    val sortNumber: Int
    val nameInOriginalLanguage: String?
    val nameInEnglish: String?
    val partnerType: ProjectTargetGroup?
    val vat: String?
    val vatRecovery: ProjectPartnerVatRecoveryDTO?
    val legalStatusId: Long

    //partner_transl
    val department: String?
}
