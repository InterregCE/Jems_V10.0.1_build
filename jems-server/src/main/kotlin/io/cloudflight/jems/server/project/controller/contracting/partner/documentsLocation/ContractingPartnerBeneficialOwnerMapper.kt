package io.cloudflight.jems.server.project.controller.contracting.partner.documentsLocation

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerDocumentsLocationDTO
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val documentsLocationMapper = Mappers.getMapper(ContractingPartnerDocumentsLocationMapper::class.java)

fun List<ContractingPartnerDocumentsLocation>.toDto() = map { it.toDto() }

fun ContractingPartnerDocumentsLocation.toDto() = documentsLocationMapper.map(this)

fun List<ContractingPartnerDocumentsLocationDTO>.toModel() = map { it.toModel() }

fun ContractingPartnerDocumentsLocationDTO.toModel() = ContractingPartnerDocumentsLocation(
    id = id,
    partnerId = partnerId,
    title = title,
    firstName = firstName,
    lastName = lastName,
    emailAddress = emailAddress,
    telephoneNo = telephoneNo,
    institutionName = institutionName,
    street = street,
    locationNumber = locationNumber,
    postalCode = postalCode,
    city = city,
    homepage = homepage,
    country = country,
    nutsTwoRegion = nutsTwoRegion,
    nutsThreeRegion = nutsThreeRegion,
    countryCode = countryCode,
    nutsTwoRegionCode = nutsTwoRegionCode,
    nutsThreeRegionCode = nutsThreeRegionCode
)

@Mapper
interface ContractingPartnerDocumentsLocationMapper {
    fun map(model: ContractingPartnerDocumentsLocation): ContractingPartnerDocumentsLocationDTO
}
