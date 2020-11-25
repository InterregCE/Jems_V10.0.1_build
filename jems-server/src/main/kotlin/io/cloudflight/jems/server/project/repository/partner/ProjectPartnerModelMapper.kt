package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressType
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.project.entity.Address
import io.cloudflight.jems.server.project.entity.Contact
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContactId
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContact
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerMotivationEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddress
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerAddressId
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner

fun InputProjectPartnerCreate.toEntity(project: Project, legalStatus: ProgrammeLegalStatus) = ProjectPartnerEntity(
    project = project,
    abbreviation = abbreviation!!,
    role = role!!,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    department = department,
    partnerType = partnerType,
    legalStatus = legalStatus,
    vat = vat,
    vatRecovery = vatRecovery
)

fun ProjectPartnerEntity.toProjectPartner() = ProjectPartner(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber,
    country = addresses?.firstOrNull { it.addressId.type == ProjectPartnerAddressType.Organization }?.address?.country
)
fun Iterable<ProjectPartnerEntity>.toProjectPartner() = map { it.toProjectPartner() }

// todo remove when everything switched to Models
fun ProjectPartnerEntity.toOutputProjectPartner() = OutputProjectPartner(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber,
    country = addresses?.firstOrNull { it.addressId.type == ProjectPartnerAddressType.Organization }?.address?.country
)
fun Iterable<ProjectPartnerEntity>.toOutputProjectPartner() = map { it.toOutputProjectPartner() }

fun ProjectPartnerEntity.toOutputProjectPartnerDetail() = OutputProjectPartnerDetail(
    id = id,
    abbreviation = abbreviation,
    role = role,
    sortNumber = sortNumber,
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    department = department,
    partnerType = partnerType,
    legalStatusId = legalStatus.id,
    vat = vat,
    vatRecovery = vatRecovery,
    addresses = addresses?.map { it.toDto() } ?: emptyList(),
    contacts = contacts?.map { it.toOutputProjectPartnerContact() } ?: emptyList(),
    motivation = motivation.map { it.toDto() }.firstOrNull()
)

fun ProjectPartnerAddressDTO.toEntity(partner: ProjectPartnerEntity) = ProjectPartnerAddress(
    addressId = ProjectPartnerAddressId(partner.id, type),
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

fun InputProjectContact.toEntity(partner: ProjectPartnerEntity) = ProjectPartnerContact(
    contactId = ProjectPartnerContactId(partner.id, type),
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

fun ProjectPartnerMotivationDTO.toEntity(partnerId: Long): Set<ProjectPartnerMotivationEntity> {
    val motivation = ProjectPartnerMotivationEntity(
        partnerId = partnerId,
        organizationRelevance = organizationRelevance,
        organizationRole = organizationRole,
        organizationExperience = organizationExperience
    ).nullIfBlank() ?: return emptySet()

    return setOf(motivation)
}

fun ProjectPartnerMotivationEntity.toDto() = ProjectPartnerMotivationDTO(
    organizationRelevance = organizationRelevance,
    organizationRole = organizationRole,
    organizationExperience = organizationExperience
)

fun ProjectPartnerAddress.toDto() = ProjectPartnerAddressDTO(
    type = addressId.type,
    country = address.country,
    nutsRegion2 = address.nutsRegion2,
    nutsRegion3 = address.nutsRegion3,
    street = address.street,
    houseNumber = address.houseNumber,
    postalCode = address.postalCode,
    city = address.city,
    homepage = address.homepage
)
