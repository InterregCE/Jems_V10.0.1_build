package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationAddressDetails
import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganizationAddressDetails
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganizationDetail
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerContact
import io.cloudflight.jems.server.project.entity.AssociatedOrganizationContact
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerContactId
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.ProjectAssociatedOrganization
import io.cloudflight.jems.server.project.entity.ProjectAssociatedOrganizationDetail
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.toOutputProjectPartner

fun InputProjectAssociatedOrganizationCreate.toEntity(project: Project,
                                                      partner: ProjectPartner) = ProjectAssociatedOrganization(
    nameInOriginalLanguage = nameInOriginalLanguage,
    nameInEnglish = nameInEnglish,
    project = project,
    partner = partner
)

fun ProjectAssociatedOrganization.toOutputProjectAssociatedOrganization() = OutputProjectAssociatedOrganization(
    id = id!!,
    nameInEnglish = nameInEnglish,
    nameInOriginalLanguage = nameInOriginalLanguage,
    organizationAddress = organizationAddress?.toOutputProjectAssociatedOrganizationDetails(),
    partner = partner.toOutputProjectPartner(),
    sortNumber = sortNumber
)

fun ProjectAssociatedOrganization.toOutputProjectAssociatedOrganizationDetail() = OutputProjectAssociatedOrganizationDetail(
    id = id!!,
    nameInEnglish = nameInEnglish,
    nameInOriginalLanguage = nameInOriginalLanguage,
    organizationAddress = organizationAddress?.toOutputProjectAssociatedOrganizationDetails(),
    partner = partner.toOutputProjectPartner(),
    sortNumber = sortNumber,
    associatedOrganizationContacts = associatedOrganizationContacts?.map { it.toOutputProjectPartnerContact() }?.toHashSet()
)


fun InputProjectPartnerContact.toAssociatedOrganizationContact(associatedOrganization: ProjectAssociatedOrganization) = AssociatedOrganizationContact(
    associatedOrganizationContactId = ProjectPartnerContactId(associatedOrganization.id!!, type),
    title = title,
    firstName =  firstName,
    lastName = lastName,
    email = email,
    telephone = telephone
)

fun AssociatedOrganizationContact.toOutputProjectPartnerContact() = OutputProjectPartnerContact(
    type = associatedOrganizationContactId.type,
    title = title,
    firstName =  firstName,
    lastName = lastName,
    email = email,
    telephone = telephone
)

fun ProjectAssociatedOrganizationDetail.toOutputProjectAssociatedOrganizationDetails() = OutputProjectAssociatedOrganizationAddressDetails(
    country = country,
    nutsRegion2 = nutsRegion2,
    nutsRegion3 = nutsRegion3,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city,
    homepage = homepage
)

fun InputProjectAssociatedOrganizationAddressDetails.toEntity(projectAssociatedOrganization: ProjectAssociatedOrganization) = ProjectAssociatedOrganizationDetail(
    organizationId = projectAssociatedOrganization.id!!,
    organization = projectAssociatedOrganization,
    country = country,
    nutsRegion2 = nutsRegion2,
    nutsRegion3 = nutsRegion3,
    street = street,
    houseNumber = houseNumber,
    postalCode = postalCode,
    city = city,
    homepage = homepage
)
