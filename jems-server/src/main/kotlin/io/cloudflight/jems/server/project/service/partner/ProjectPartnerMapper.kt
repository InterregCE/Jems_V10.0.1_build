package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerAddress
import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.OutputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerAddress
import io.cloudflight.jems.server.project.entity.Address
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContactId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContact
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContribution
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddress
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.service.partner.cofinancing.toOutputProjectCoFinancing

fun InputProjectPartnerCreate.toEntity(project: Project) = ProjectPartner(
    project = project,
    abbreviation = abbreviation!!,
    role = role!!,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    department = department
)

fun ProjectPartner.toOutputProjectPartner() = OutputProjectPartner(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber
)

fun ProjectPartner.toOutputProjectPartnerDetail() = OutputProjectPartnerDetail(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    department = department,
    addresses = addresses?.map { it.toOutputProjectPartnerAddress() } ?: emptyList(),
    contacts = contacts?.map { it.toOutputProjectPartnerContact() } ?: emptyList(),
    partnerContribution = partnerContribution.map { it.toOutputProjectPartnerContribution() }.firstOrNull(),
    financing = financing.map { it.toOutputProjectCoFinancing() }
)

fun InputProjectPartnerAddress.toEntity(partner: ProjectPartner) = ProjectPartnerAddress(
    addressId = ProjectPartnerAddressId(partner.id!!, type),
    address = Address(
        country = country,
        nutsRegion2 = nutsRegion2,
        nutsRegion3 = nutsRegion3,
        street = street,
        houseNumber = houseNumber,
        postalCode = postalCode,
        city = city,
        homepage = homepage
    )
)

fun InputProjectContact.toEntity(partner: ProjectPartner) = ProjectPartnerContact(
    contactId = ProjectPartnerContactId(partner.id!!, type),
    contact = Contact(
        title = title,
        firstName = firstName,
        lastName = lastName,
        email = email,
        telephone = telephone
    )
)

fun ProjectPartnerContact.toOutputProjectPartnerContact() = OutputProjectPartnerContact(
    type = contactId.type,
    title = contact?.title,
    firstName = contact?.firstName,
    lastName = contact?.lastName,
    email = contact?.email,
    telephone = contact?.telephone
)

fun InputProjectPartnerContribution.toEntity(partnerId: Long): Set<ProjectPartnerContribution> {
    val contribution = ProjectPartnerContribution(
        partnerId = partnerId,
        organizationRelevance = organizationRelevance,
        organizationRole = organizationRole,
        organizationExperience = organizationExperience
    ).nullIfBlank() ?: return emptySet()

    return setOf(contribution)
}

fun ProjectPartnerContribution.toOutputProjectPartnerContribution() = OutputProjectPartnerContribution(
    organizationRelevance = organizationRelevance,
    organizationRole = organizationRole,
    organizationExperience = organizationExperience
)

fun ProjectPartnerAddress.toOutputProjectPartnerAddress() = OutputProjectPartnerAddress(
    type = addressId.type,
    country = address?.country,
    nutsRegion2 = address?.nutsRegion2,
    nutsRegion3 = address?.nutsRegion3,
    street = address?.street,
    houseNumber = address?.houseNumber,
    postalCode = address?.postalCode,
    city = address?.city,
    homepage = address?.homepage
)
