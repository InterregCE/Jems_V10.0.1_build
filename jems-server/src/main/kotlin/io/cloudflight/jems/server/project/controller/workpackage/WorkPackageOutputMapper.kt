package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.common.dto.AddressDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputUpdateDTO
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import org.springframework.data.domain.Page

fun Page<WorkPackageInvestment>.toWorkPackageInvestmentDTOPage() = this.map { it.toWorkPackageInvestmentDTO() }
fun WorkPackageInvestment.toWorkPackageInvestmentDTO() = WorkPackageInvestmentDTO(
    id = id,
    investmentNumber = investmentNumber,
    title = title,
    justificationExplanation = justificationExplanation,
    justificationTransactionalRelevance = justificationTransactionalRelevance,
    justificationBenefits = justificationBenefits,
    justificationPilot = justificationPilot,
    address = address?.toAddressDTO(),
    risk = risk,
    documentation = documentation,
    ownershipSiteLocation = ownershipSiteLocation,
    ownershipRetain = ownershipRetain,
    ownershipMaintenance = ownershipMaintenance
)

fun WorkPackageInvestmentDTO.toWorkPackageInvestment() = WorkPackageInvestment(
    id = id,
    investmentNumber = investmentNumber,
    title = title,
    justificationExplanation = justificationExplanation,
    justificationTransactionalRelevance = justificationTransactionalRelevance,
    justificationBenefits = justificationBenefits,
    justificationPilot = justificationPilot,
    address = address?.toAddress(),
    risk = risk,
    documentation = documentation,
    ownershipSiteLocation = ownershipSiteLocation,
    ownershipRetain = ownershipRetain,
    ownershipMaintenance = ownershipMaintenance
)

fun WorkPackageOutputUpdateDTO.toWorkPackageOutputUpdate() = WorkPackageOutputUpdate(
    outputNumber = outputNumber,
    programmeOutputIndicatorId = programmeOutputIndicatorId,
    title = title,
    targetValue = targetValue,
    periodNumber = periodNumber,
    description = description
)

fun WorkPackageOutput.toWorkPackageOutputDTO() = WorkPackageOutputDTO(
    outputNumber = outputNumber,
    programmeOutputIndicator = programmeOutputIndicator,
    title = title,
    targetValue = targetValue,
    periodNumber = periodNumber,
    description = description
)

fun Set<WorkPackageOutputUpdateDTO>.toWorkPackageOutputUpdateSet() = this.map { it.toWorkPackageOutputUpdate() }.toSet()

fun Set<WorkPackageOutput>.toWorkPackageOutputDTOSet() = this.map { it.toWorkPackageOutputDTO() }.toSet()

fun Address.toAddressDTO() = AddressDTO(
    this.country,
    this.nutsRegion2,
    this.nutsRegion3,
    this.street,
    this.houseNumber,
    this.postalCode,
    this.city
)

fun AddressDTO.toAddress() =
    Address(this.country, this.region2, this.region3, this.street, this.houseNumber, this.postalCode, this.city)
