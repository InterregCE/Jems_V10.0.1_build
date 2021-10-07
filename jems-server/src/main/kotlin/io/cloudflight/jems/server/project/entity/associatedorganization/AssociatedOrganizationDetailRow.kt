package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.api.project.dto.ProjectContactTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.common.entity.TranslationView

interface AssociatedOrganizationDetailRow : TranslationView {
    //associated organization
    val id: Long
    val nameInOriginalLanguage: String?
    val nameInEnglish: String?
    val sortNumber: Int?
    //associated organization translation
    val roleDescription: String?
    // partner
    val partnerId: Long
    val abbreviation: String
    val role: ProjectPartnerRoleDTO
    val partnerSortNumber: Int?
    val partnerCountry: String?
    val partnerNutsRegion3: String?
    //address
    val country: String?
    val nutsRegion2: String?
    val nutsRegion3: String?
    val street: String?
    val houseNumber: String?
    val postalCode: String?
    val city: String?
    val homepage: String?
    //contact
    val contactType: ProjectContactTypeDTO?
    val title: String?
    val firstName: String?
    val lastName: String?
    val email: String?
    val telephone: String?
}
