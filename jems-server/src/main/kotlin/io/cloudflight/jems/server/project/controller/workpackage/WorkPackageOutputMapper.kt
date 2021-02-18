package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.common.dto.AddressDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.investment.InvestmentSummaryDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
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

fun List<InvestmentSummary>.toInvestmentSummaryDTOs() =
    this.map { it.toInvestmentSummaryDTO() }

fun InvestmentSummary.toInvestmentSummaryDTO() = InvestmentSummaryDTO(
    id = id,
    investmentNumber = investmentNumber,
    workPackageNumber = workPackageNumber
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

fun WorkPackageOutputDTO.toModel() = WorkPackageOutput(
    translatedValues = combineOutputTranslations(title, description),
    periodNumber = periodNumber,
    programmeOutputIndicatorId = programmeOutputIndicatorId,
    targetValue = targetValue
)

fun List<WorkPackageOutputDTO>.toModel() = map { it.toModel() }.toList()

fun List<WorkPackageOutput>.toDto() = map {
    WorkPackageOutputDTO(
        outputNumber = it.outputNumber,
        programmeOutputIndicatorId = it.programmeOutputIndicatorId,
        programmeOutputIndicatorIdentifier = it.programmeOutputIndicatorIdentifier,
        title = it.translatedValues.extractField { it.title },
        targetValue = it.targetValue,
        periodNumber = it.periodNumber,
        description = it.translatedValues.extractField { it.description }
    )
}

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

fun combineOutputTranslations(
    title: Set<InputTranslation>,
    description: Set<InputTranslation>
): Set<WorkPackageOutputTranslatedValue> {
    val titleMap = title.groupByLanguage()
    val descriptionMap = description.groupByLanguage()

    return extractLanguages(titleMap, descriptionMap)
        .map {
            WorkPackageOutputTranslatedValue(
                language = it,
                title = titleMap[it],
                description = descriptionMap[it],
            )
        }
        .filter { !it.isEmpty() }
        .toSet()
}
