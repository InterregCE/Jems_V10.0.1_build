package io.cloudflight.jems.server.project.repository.partner.associated_organization

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectContactDTO
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationAddress
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerContactDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.controller.partner.toDto
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.TranslationOrganizationId
import io.cloudflight.jems.server.project.entity.associatedorganization.AssociatedOrganizationAddressRow
import io.cloudflight.jems.server.project.entity.associatedorganization.AssociatedOrganizationContactRow
import io.cloudflight.jems.server.project.entity.associatedorganization.AssociatedOrganizationDetailRow
import io.cloudflight.jems.server.project.entity.associatedorganization.AssociatedOrganizationSimpleRow
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganization
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationAddress
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationContact
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationContactId
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationRow
import io.cloudflight.jems.server.project.entity.associatedorganization.ProjectAssociatedOrganizationTransl
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.partner.toModel
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary

fun InputProjectAssociatedOrganization.toEntity(
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

fun InputProjectAssociatedOrganization.combineTranslatedValues(
    organizationId: Long
): MutableSet<ProjectAssociatedOrganizationTransl> {
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
    partner = partner.toModel().toDto(),
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    sortNumber = sortNumber,
    address = addresses.map { it.toOutputProjectAssociatedOrganizationDetails() }.firstOrNull(),
    contacts = contacts.map { it.toOutputProjectAssociatedOrganizationContact() },
    roleDescription = translatedValues.mapTo(HashSet()) { InputTranslation(it.translationId.language, it.roleDescription) }
)

fun Set<ProjectContactDTO>.toEntity(organizationId: Long): MutableSet<ProjectAssociatedOrganizationContact> = mapTo(HashSet()) {
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

fun ProjectAssociatedOrganizationContact.toOutputProjectAssociatedOrganizationContact() = ProjectPartnerContactDTO(
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

fun List<ProjectAssociatedOrganizationRow>.toAssociatedOrganizationDetailHistoricalData(
    partner: ProjectPartnerSummary,
    address: OutputProjectAssociatedOrganizationAddress?,
    contacts: List<ProjectPartnerContactDTO>) =
    this.groupBy { it.id }.map { groupedRows -> OutputProjectAssociatedOrganizationDetail(
        id = groupedRows.value.first().id,
        partner = partner.toDto(),
        nameInOriginalLanguage = groupedRows.value.first().nameInOriginalLanguage,
        nameInEnglish = groupedRows.value.first().nameInEnglish,
        sortNumber = groupedRows.value.first().sortNumber,
        address = address,
        contacts = contacts,
        roleDescription = groupedRows.value.extractField { it.roleDescription },
    ) }

fun List<AssociatedOrganizationAddressRow>.toProjectAssociatedOrganizationAddressHistoricalData() =
    this.groupBy { it.id }.map { groupedRows ->
        OutputProjectAssociatedOrganizationAddress(
            country = groupedRows.value.first().country,
            nutsRegion2 = groupedRows.value.first().nutsRegion2,
            nutsRegion3 = groupedRows.value.first().nutsRegion3,
            street = groupedRows.value.first().street,
            houseNumber = groupedRows.value.first().houseNumber,
            postalCode = groupedRows.value.first().postalCode,
            city = groupedRows.value.first().city,
            homepage = groupedRows.value.first().homepage
        )
    }

fun AssociatedOrganizationSimpleRow.toOutputAssociatedOrganizationHistoricalData() = OutputProjectAssociatedOrganization(
    id = id,
    partnerAbbreviation = partnerAbbreviation,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    sortNumber = sortNumber
)

fun Collection<AssociatedOrganizationContactRow>.toAssociatedOrganizationContactHistoricalData() = map { it.toModel() }.toList()

fun AssociatedOrganizationContactRow.toModel() = ProjectPartnerContactDTO(
    type = type,
    title = title,
    firstName = firstName,
    lastName = lastName,
    email = email,
    telephone = telephone
)

fun List<AssociatedOrganizationDetailRow>.toModel() =
    groupBy { it.id }.map { groupedRows ->
        OutputProjectAssociatedOrganizationDetail(
            id = groupedRows.key,
            partner = ProjectPartnerSummaryDTO(
                id = groupedRows.value.first().partnerId,
                abbreviation = groupedRows.value.first().abbreviation,
                role = groupedRows.value.first().role,
                sortNumber = groupedRows.value.first().partnerSortNumber,
                country = groupedRows.value.first().partnerCountry,
                region = groupedRows.value.first().partnerNutsRegion3
            ),
            nameInOriginalLanguage = groupedRows.value.first().nameInOriginalLanguage,
            nameInEnglish = groupedRows.value.first().nameInEnglish,
            sortNumber = groupedRows.value.first().sortNumber,
            address = OutputProjectAssociatedOrganizationAddress(
                country = groupedRows.value.first().country,
                nutsRegion2 = groupedRows.value.first().nutsRegion2,
                nutsRegion3 = groupedRows.value.first().nutsRegion3,
                street = groupedRows.value.first().street,
                houseNumber = groupedRows.value.first().houseNumber,
                postalCode = groupedRows.value.first().postalCode,
                city = groupedRows.value.first().city,
                homepage = groupedRows.value.first().homepage
            ),
            contacts = groupedRows.value.filter { it.contactType != null }.groupBy { it.contactType }.map { groupedContactRows ->
                ProjectPartnerContactDTO(
                    type = groupedContactRows.key!!,
                    title = groupedContactRows.value.first().title,
                    firstName = groupedContactRows.value.first().firstName,
                    lastName = groupedContactRows.value.first().lastName,
                    email = groupedContactRows.value.first().email,
                    telephone = groupedContactRows.value.first().telephone
                )
            },
            roleDescription = groupedRows.value.extractField { it.roleDescription }
        )
    }
