package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.programme.controller.indicator.toIndicatorOutputDto
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentTransl
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentTranslation
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import org.springframework.data.domain.Page

fun WorkPackageOutputUpdate.toEntity(
    indicatorOutput: IndicatorOutput?,
    workPackage: WorkPackageEntity,
    projectPeriod: ProjectPeriodEntity?
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
                        title = title.firstOrNull { it.language === language }?.translation ?: "",
                        justificationExplanation = justificationExplanation.firstOrNull { it.language === language }?.translation
                            ?: "",
                        justificationTransactionalRelevance = justificationTransactionalRelevance.firstOrNull { it.language === language }?.translation
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
