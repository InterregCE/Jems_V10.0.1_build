package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.common.entity.TranslationView
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import java.sql.Timestamp

interface PartnerIdentityRow : TranslationView {
    val id: Long
    val active: Boolean
    val projectId: Long
    val abbreviation: String
    val role: ProjectPartnerRole
    val sortNumber: Int
    val createdAt: Timestamp
    val nameInOriginalLanguage: String?
    val nameInEnglish: String?
    val partnerType: ProjectTargetGroup?
    val partnerSubType: PartnerSubType?
    val nace: NaceGroupLevel?
    val otherIdentifierNumber: String?
    val otherIdentifierDescription: String?
    val pic: String?
    val vat: String?
    val vatRecovery: ProjectPartnerVatRecovery?
    val legalStatusId: Long
    val department: String?
}
