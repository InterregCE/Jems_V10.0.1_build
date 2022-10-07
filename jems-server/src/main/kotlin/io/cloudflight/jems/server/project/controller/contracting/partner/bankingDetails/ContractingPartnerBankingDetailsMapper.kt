package io.cloudflight.jems.server.project.controller.contracting.partner.bankingDetails

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerBankingDetailsDTO
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val bankingDetailsMapper = Mappers.getMapper(ContractingPartnerBankingDetailsMapper::class.java)

fun ContractingPartnerBankingDetails.toDto() = bankingDetailsMapper.map(this)

fun ContractingPartnerBankingDetailsDTO.toModel() = ContractingPartnerBankingDetails(
    partnerId = partnerId,
    accountHolder = accountHolder,
    accountNumber = accountNumber,
    accountIBAN = accountIBAN,
    accountSwiftBICCode = accountSwiftBICCode,
    bankName = bankName,
    streetName = streetName,
    streetNumber = streetNumber,
    postalCode = postalCode,
    country = country,
    nutsTwoRegion = nutsTwoRegion,
    nutsThreeRegion = nutsThreeRegion
)

@Mapper
interface ContractingPartnerBankingDetailsMapper {

    fun map(model: ContractingPartnerBankingDetails): ContractingPartnerBankingDetailsDTO
}