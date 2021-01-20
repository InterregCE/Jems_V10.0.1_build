package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.common.dto.AddressDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.investment.InvestmentSummaryDTO
import io.cloudflight.jems.api.project.dto.workpackage.investment.WorkPackageInvestmentDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputUpdateDTO
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

fun WorkPackageOutputUpdateDTO.toWorkPackageOutput() = WorkPackageOutput(
    outputNumber = outputNumber,
    programmeOutputIndicatorId = programmeOutputIndicatorId,
    translatedValues = combineOutputTranslations(title, description),
    targetValue = targetValue,
    periodNumber = periodNumber
)

fun WorkPackageOutput.toWorkPackageOutputDTO() = WorkPackageOutputDTO(
    outputNumber = outputNumber,
    programmeOutputIndicatorId = programmeOutputIndicatorId,
    title = translatedValues.extractField { it.title },
    targetValue = targetValue,
    periodNumber = periodNumber,
    description = translatedValues.extractField { it.description }
)

fun List<WorkPackageOutputUpdateDTO>.toWorkPackageOutputList() = this.map { it.toWorkPackageOutput() }.toList()

fun List<WorkPackageOutput>.toWorkPackageOutputDTOList() = this.map { it.toWorkPackageOutputDTO() }.toList()

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
