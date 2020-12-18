package io.cloudflight.jems.server.project.service.associatedorganization

import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationUpdate
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.TranslationOrganizationId
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganization
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationAddress
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationContact
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationContactId
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationTransl
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.partner.toOutputProjectPartner

fun InputProjectAssociatedOrganizationCreate.toEntity(
    partner: ProjectPartnerEntity
) = ProjectAssociatedOrganization(
    project = partner.project,
    partner = partner,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish
    // translatedValues - need organization Id
    // addresses - need organization Id
    // contacts - need organization Id
)

fun InputProjectAssociatedOrganizationCreate.combineTranslatedValues(
    organizationId: Long
): Set<ProjectAssociatedOrganizationTransl> {
    val roleDescriptionMap = roleDescription.associateBy( { it.language }, { it.translation } )
    val languages = roleDescriptionMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectAssociatedOrganizationTransl(
            TranslationOrganizationId(organizationId, it),
            roleDescriptionMap[it]
        )
    }
}

fun InputProjectAssociatedOrganizationUpdate.combineTranslatedValues(
    organizationId: Long
): Set<ProjectAssociatedOrganizationTransl> {
    val roleDescriptionMap = roleDescription.associateBy( { it.language }, { it.translation } )
    val languages = roleDescriptionMap.keys.toMutableSet()

    return languages.mapTo(HashSet()) {
        ProjectAssociatedOrganizationTransl(
            TranslationOrganizationId(organizationId, it),
            roleDescriptionMap[it]
        )
    }
}

fun InputProjectAssociatedOrganizationAddress?.toEntity(organizationId: Long): MutableSet<ProjectAssociatedOrganizationAddress> {
    var address: ProjectAssociatedOrganizationAddress? = null
    if (this != null)
        address = ProjectAssociatedOrganizationAddress(
            organizationId = organizationId,
            address = AddressEntity(
                country = country,
                nutsRegion2 = nutsRegion2,
                nutsRegion3 = nutsRegion3,
                street = street,
                houseNumber = houseNumber,
                postalCode = postalCode,
                city = city,
                homepage = homepage
            )
        ).nullIfBlank()

    if (address == null)
        return mutableSetOf()
    return mutableSetOf(address)
}

fun ProjectAssociatedOrganization.toOutputProjectAssociatedOrganization() = OutputProjectAssociatedOrganization(
    id = id,
    partnerAbbreviation = partner.abbreviation,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    sortNumber = sortNumber
)

fun ProjectAssociatedOrganization.toOutputProjectAssociatedOrganizationDetail() = OutputProjectAssociatedOrganizationDetail(
    id = id,
    partner = partner.toOutputProjectPartner(),
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    sortNumber = sortNumber,
    address = addresses.map { it.toOutputProjectAssociatedOrganizationDetails() }.firstOrNull(),
    contacts = contacts.map { it.toOutputProjectAssociatedOrganizationContact() },
    roleDescription = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.roleDescription) }
)

fun Set<InputProjectContact>.toEntity(organizationId: Long): MutableSet<ProjectAssociatedOrganizationContact> = mapTo(HashSet()) {
    ProjectAssociatedOrganizationContact(
        contactId = ProjectAssociatedOrganizationContactId(organizationId, it.type),
        contact = Contact(
            title = it.title,
            firstName = it.firstName,
            lastName = it.lastName,
            email = it.email,
            telephone = it.telephone
        )
    )
}

fun ProjectAssociatedOrganizationContact.toOutputProjectAssociatedOrganizationContact() = OutputProjectPartnerContact(
    type = contactId.type,
    title = contact?.title,
    firstName = contact?.firstName,
    lastName = contact?.lastName,
    email = contact?.email,
    telephone = contact?.telephone
)

fun ProjectAssociatedOrganizationAddress.toOutputProjectAssociatedOrganizationDetails() = OutputProjectAssociatedOrganizationAddress(
    country = address?.country,
    nutsRegion2 = address?.nutsRegion2,
    nutsRegion3 = address?.nutsRegion3,
    street = address?.street,
    houseNumber = address?.houseNumber,
    postalCode = address?.postalCode,
    city = address?.city,
    homepage = address?.homepage
)
