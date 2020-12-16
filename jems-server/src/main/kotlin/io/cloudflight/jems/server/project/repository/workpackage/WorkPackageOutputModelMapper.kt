package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.programme.service.indicator.toIndicatorOutputDto
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriod
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import org.springframework.data.domain.Page


fun WorkPackageOutputUpdate.toEntity(
    indicatorOutput: IndicatorOutput?,
    workPackage: WorkPackageEntity,
    projectPeriod: ProjectPeriod?
) = WorkPackageOutputEntity(
    workPackage = workPackage,
    outputNumber = outputNumber,
    programmeOutputIndicator = indicatorOutput,
    title = title,
    targetValue = targetValue,
    period = projectPeriod,
    description = description
)

fun WorkPackageOutputEntity.toWorkPackageOutput() = WorkPackageOutput(
    outputNumber = outputNumber,
    programmeOutputIndicator = programmeOutputIndicator?.toIndicatorOutputDto(),
    title = title,
    targetValue = targetValue,
    periodNumber = period?.id?.number,
    description = description
)

fun Set<WorkPackageOutputEntity>.toWorkPackageOutputSet() =
    this.map { it.toWorkPackageOutput() }.sortedBy { it.outputNumber }.toSet()

fun Page<WorkPackageInvestmentEntity>.toWorkPackageInvestmentPage() = this.map { it.toWorkPackageInvestment() }

fun WorkPackageInvestmentEntity.toWorkPackageInvestment() = WorkPackageInvestment(
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

fun WorkPackageInvestment.toWorkPackageInvestmentEntity(workPackageEntity: WorkPackageEntity) = WorkPackageInvestmentEntity(
    workPackage = workPackageEntity,
    investmentNumber = investmentNumber,
    title = title,
    justificationExplanation = justificationExplanation,
    justificationTransactionalRelevance = justificationTransactionalRelevance,
    justificationBenefits = justificationBenefits,
    justificationPilot = justificationPilot,
    address = address?.toAddressEntity(),
    risk = risk,
    documentation = documentation,
    ownershipSiteLocation = ownershipSiteLocation,
    ownershipRetain = ownershipRetain,
    ownershipMaintenance = ownershipMaintenance
)

fun Address.toAddressEntity() = AddressEntity(this.country, this.nutsRegion2, this.nutsRegion3, this.street, this.houseNumber, this.postalCode, this.city)
fun AddressEntity.toAddress() = Address(this.country, this.nutsRegion2, this.nutsRegion3, this.street, this.houseNumber, this.postalCode, this.city)
