package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.common.entity.TranslationView
import io.cloudflight.jems.server.project.service.model.ProjectContactType
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddressType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import java.sql.Timestamp

interface PartnerDetailRow : TranslationView {
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
    val pic: String?
    val legalStatusId: Long
    val vat: String?
    val vatRecovery: ProjectPartnerVatRecovery?
    //translation
    val department: String?
    val otherIdentifierDescription: String?
    //address
    val addressType: ProjectPartnerAddressType?
    val country: String?
    val countryCode: String?
    val nutsRegion2: String?
    val nutsRegion2Code: String?
    val nutsRegion3: String?
    val nutsRegion3Code: String?
    val street: String?
    val houseNumber: String?
    val postalCode: String?
    val city: String?
    val homepage: String?
    //contact
    val contactType: ProjectContactType?
    val title: String?
    val firstName: String?
    val lastName: String?
    val email: String?
    val telephone: String?
    //motivation
    val motivationRowLanguage: SystemLanguage?
    val organizationRelevance: String?
    val organizationRole: String?
    val organizationExperience: String?
}
