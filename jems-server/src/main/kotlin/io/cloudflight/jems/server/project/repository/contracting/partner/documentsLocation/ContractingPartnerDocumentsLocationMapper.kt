package io.cloudflight.jems.server.project.repository.contracting.partner.documentsLocation

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerDocumentsLocationEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation


fun ProjectContractingPartnerDocumentsLocationEntity.toModel() =
    ContractingPartnerDocumentsLocation(
        id = id,
        partnerId = projectPartner.id,
        firstName = firstName,
        lastName = lastName,
        title = title,
        telephoneNo = telephoneNo,
        street = street,
        postalCode = postalCode,
        nutsTwoRegion = nutsTwoRegion,
        nutsThreeRegion = nutsThreeRegion,
        locationNumber = locationNumber,
        institutionName = institutionName,
        homepage = homepage,
        emailAddress = emailAddress,
        country = country,
        city = city,
        countryCode = countryCode,
        nutsTwoRegionCode = nutsTwoRegionCode,
        nutsThreeRegionCode = nutsThreeRegionCode
    )

fun ContractingPartnerDocumentsLocation.toEntity(projectPartner: ProjectPartnerEntity) =
    ProjectContractingPartnerDocumentsLocationEntity(
        id = id,
        projectPartner = projectPartner,
        firstName = firstName,
        lastName = lastName,
        title = title,
        telephoneNo = telephoneNo,
        street = street,
        postalCode = postalCode,
        nutsTwoRegion = nutsTwoRegion,
        nutsThreeRegion = nutsThreeRegion,
        locationNumber = locationNumber,
        institutionName = institutionName,
        homepage = homepage,
        emailAddress = emailAddress,
        country = country,
        city = city,
        countryCode = countryCode,
        nutsTwoRegionCode = nutsTwoRegionCode,
        nutsThreeRegionCode = nutsThreeRegionCode
    )
