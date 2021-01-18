package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.TranslationWorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentTransl
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentTranslation
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputTransl
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import org.springframework.data.domain.Page
import kotlin.collections.HashSet


fun WorkPackageOutput.toEntity(
    indicatorOutput: IndicatorOutput?,
    workPackage: WorkPackageEntity,
    projectPeriod: ProjectPeriodEntity?,
    outputNumber: Int
) : WorkPackageOutputEntity {
    val outputId = WorkPackageOutputId(workPackage.id, outputNumber)
    return WorkPackageOutputEntity(
        outputId = outputId,
        programmeOutputIndicator = indicatorOutput,
        translatedValues = translatedValues.toEntity(outputId),
        targetValue = targetValue,
        period = projectPeriod
    )
}

fun WorkPackageOutputEntity.toWorkPackageOutput() = WorkPackageOutput(
    outputNumber = outputId.outputNumber,
    programmeOutputIndicatorId = programmeOutputIndicator?.id,
    translatedValues = translatedValues.toModel(),
    targetValue = targetValue,
    periodNumber = period?.id?.number
)

fun List<WorkPackageOutputEntity>.toWorkPackageOutputList() =
    this.map { it.toWorkPackageOutput() }.sortedBy { it.outputNumber }.toList()

fun Page<WorkPackageInvestmentEntity>.toWorkPackageInvestmentPage() = this.map { it.toWorkPackageInvestment() }

fun List<WorkPackageInvestmentEntity>.toInvestmentSummaryList() =
    this.map { it.toInvestmentSummary() }

fun WorkPackageInvestmentEntity.toInvestmentSummary() = InvestmentSummary(
    id = id,
    investmentNumber = investmentNumber,
    workPackageId = workPackage.id
)

fun WorkPackageInvestmentEntity.toWorkPackageInvestment() = WorkPackageInvestment(
    id = id,
    investmentNumber = investmentNumber,
    title = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.title)
    },
    justificationExplanation = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.justificationExplanation)
    },
    justificationTransactionalRelevance = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.justificationTransactionalRelevance)
    },
    justificationBenefits = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.justificationBenefits)
    },
    justificationPilot = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.justificationPilot)
    },
    address = address?.toAddress(),
    risk = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.risk)
    },
    documentation = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.documentation)
    },
    ownershipSiteLocation = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.ownershipSiteLocation)
    },
    ownershipMaintenance = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.ownershipMaintenance)
    },
    ownershipRetain = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.ownershipRetain)
    },
)

fun WorkPackageInvestment.toWorkPackageInvestmentEntity(workPackageEntity: WorkPackageEntity) =
    WorkPackageInvestmentEntity(
        workPackage = workPackageEntity,
        investmentNumber = investmentNumber,
        address = address?.toAddressEntity(),
        id = id ?: 0L
    ).apply {
        translatedValues.addAll(
            title.plus(justificationExplanation)
                .plus(justificationTransactionalRelevance)
                .plus(justificationBenefits)
                .plus(justificationPilot)
                .plus(risk)
                .plus(documentation)
                .plus(ownershipSiteLocation)
                .plus(ownershipMaintenance)
                .plus(ownershipRetain)
                .map { it.language }
                .distinct()
                .map { language ->
                    WorkPackageInvestmentTransl(
                        investmentTranslation = WorkPackageInvestmentTranslation(this, language),
                        title = title.firstOrNull { it.language == language }?.translation ?: "",
                        justificationExplanation = justificationExplanation.firstOrNull { it.language == language }?.translation
                            ?: "",
                        justificationTransactionalRelevance = justificationTransactionalRelevance.firstOrNull { it.language == language }?.translation
                            ?: "",
                        justificationBenefits = justificationBenefits.firstOrNull { it.language === language }?.translation
                            ?: "",
                        justificationPilot = justificationPilot.firstOrNull { it.language === language }?.translation
                            ?: "",
                        risk = risk.firstOrNull { it.language === language }?.translation ?: "",
                        documentation = documentation.firstOrNull { it.language === language }?.translation ?: "",
                        ownershipSiteLocation = ownershipSiteLocation.firstOrNull { it.language === language }?.translation
                            ?: "",
                        ownershipMaintenance = ownershipMaintenance.firstOrNull { it.language === language }?.translation
                            ?: "",
                        ownershipRetain = ownershipRetain.firstOrNull { it.language === language }?.translation ?: "",
                    )
                }.toMutableSet()
        )
    }

fun Address.toAddressEntity() = AddressEntity(
    this.country,
    this.nutsRegion2,
    this.nutsRegion3,
    this.street,
    this.houseNumber,
    this.postalCode,
    this.city
)

fun AddressEntity.toAddress() =
    Address(this.country, this.nutsRegion2, this.nutsRegion3, this.street, this.houseNumber, this.postalCode, this.city)

fun Set<WorkPackageOutputTranslatedValue>.toEntity(outputId: WorkPackageOutputId) = mapTo(HashSet()) { it.toEntity(outputId) }

fun WorkPackageOutputTranslatedValue.toEntity(workPackageOutputId: WorkPackageOutputId) = WorkPackageOutputTransl(
    translationId = TranslationWorkPackageOutputId(workPackageOutputId = workPackageOutputId, language = language),
    title = title,
    description = description,
)

fun Set<WorkPackageOutputTransl>.toModel() = mapTo(HashSet()) {
    WorkPackageOutputTranslatedValue(
        language = it.translationId.language,
        description = it.description,
        title = it.title
    )
}
