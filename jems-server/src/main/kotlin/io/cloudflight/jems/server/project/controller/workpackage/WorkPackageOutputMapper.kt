package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.common.dto.AddressDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.InvestmentSummaryDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput

fun List<WorkPackageInvestment>.toWorkPackageInvestmentDTOList() = this.map { it.toWorkPackageInvestmentDTO() }
fun WorkPackageInvestment.toWorkPackageInvestmentDTO() = WorkPackageInvestmentDTO(
    id = id,
    investmentNumber = investmentNumber,
    title = title,
    expectedDeliveryPeriod = expectedDeliveryPeriod,
    justificationExplanation = justificationExplanation,
    justificationTransactionalRelevance = justificationTransactionalRelevance,
    justificationBenefits = justificationBenefits,
    justificationPilot = justificationPilot,
    address = address?.toAddressDTO(),
    risk = risk,
    documentation = documentation,
    documentationExpectedImpacts = documentationExpectedImpacts,
    ownershipSiteLocation = ownershipSiteLocation,
    ownershipRetain = ownershipRetain,
    ownershipMaintenance = ownershipMaintenance,
    deactivated = deactivated,
)

fun List<InvestmentSummary>.toInvestmentSummaryDTOs() =
    this.map { it.toInvestmentSummaryDTO() }

fun InvestmentSummary.toInvestmentSummaryDTO() = InvestmentSummaryDTO(
    id = id,
    investmentNumber = investmentNumber,
    workPackageNumber = workPackageNumber,
    deactivated = deactivated,
)

fun WorkPackageInvestmentDTO.toWorkPackageInvestment() = WorkPackageInvestment(
    id = id,
    investmentNumber = investmentNumber,
    expectedDeliveryPeriod = expectedDeliveryPeriod,
    title = title,
    justificationExplanation = justificationExplanation,
    justificationTransactionalRelevance = justificationTransactionalRelevance,
    justificationBenefits = justificationBenefits,
    justificationPilot = justificationPilot,
    address = address?.toAddress(),
    risk = risk,
    documentation = documentation,
    documentationExpectedImpacts = documentationExpectedImpacts,
    ownershipSiteLocation = ownershipSiteLocation,
    ownershipRetain = ownershipRetain,
    ownershipMaintenance = ownershipMaintenance,
    deactivated = deactivated ?: false,
)

fun WorkPackageOutputDTO.toModel(workPackageId: Long) = WorkPackageOutput(
    workPackageId = workPackageId,
    outputNumber = outputNumber ?: 0,
    title = title,
    description = description,
    periodNumber = periodNumber,
    programmeOutputIndicatorId = programmeOutputIndicatorId,
    targetValue = targetValue,
    deactivated = deactivated,
)

fun List<WorkPackageOutputDTO>.toModel(workPackageId: Long) = map { it.toModel(workPackageId) }.toList()

fun List<WorkPackageOutput>.toDto() = map {
    WorkPackageOutputDTO(
        workPackageId = it.workPackageId,
        outputNumber = it.outputNumber,
        programmeOutputIndicatorId = it.programmeOutputIndicatorId,
        programmeOutputIndicatorIdentifier = it.programmeOutputIndicatorIdentifier,
        title = it.title ,
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        description = it.description,
        deactivated = it.deactivated,
    )
}

fun Address.toAddressDTO() = AddressDTO(
    country = this.country,
    countryCode = this.countryCode,
    region2 = this.nutsRegion2,
    region2Code = this.nutsRegion2Code,
    region3 = this.nutsRegion3,
    region3Code = this.nutsRegion3Code,
    street = this.street,
    houseNumber = this.houseNumber,
    postalCode = this.postalCode,
    city = this.city
)

fun AddressDTO.toAddress() = Address(
    country = this.country,
    countryCode = this.countryCode,
    nutsRegion2 = this.region2,
    nutsRegion2Code = this.region2Code,
    nutsRegion3 = this.region3,
    nutsRegion3Code = this.region3Code,
    street = this.street,
    houseNumber = this.houseNumber,
    postalCode = this.postalCode,
    city = this.city
)
