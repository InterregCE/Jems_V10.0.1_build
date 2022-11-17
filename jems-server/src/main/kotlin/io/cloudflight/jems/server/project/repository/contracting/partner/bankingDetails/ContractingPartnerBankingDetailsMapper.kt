package io.cloudflight.jems.server.project.repository.contracting.partner.bankingDetails

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBankingDetailsEntity
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails

fun ProjectContractingPartnerBankingDetailsEntity.toModel() =
    ContractingPartnerBankingDetails(
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

fun ContractingPartnerBankingDetails.toEntity() =
    ProjectContractingPartnerBankingDetailsEntity(
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