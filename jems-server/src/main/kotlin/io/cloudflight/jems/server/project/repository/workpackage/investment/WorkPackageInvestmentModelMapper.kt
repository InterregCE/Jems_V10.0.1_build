package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.entity.extractField
import io.cloudflight.jems.server.project.entity.AddressEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentRow
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentTransl
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentTranslation
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageSummaryRow
import io.cloudflight.jems.server.project.service.model.Address
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment

fun List<WorkPackageInvestmentEntity>.toWorkPackageInvestmentList() = this.map { it.toWorkPackageInvestment() }

fun List<WorkPackageInvestmentEntity>.toInvestmentSummaryList() =
    this.map { it.toInvestmentSummary() }

fun WorkPackageInvestmentEntity.toInvestmentSummary() = InvestmentSummary(
    id = id,
    investmentNumber = investmentNumber,
    workPackageNumber = workPackage.number,
    deactivated = deactivated,
)

fun Iterable<WorkPackageInvestmentEntity>.toModel() =  map { it.toWorkPackageInvestment() }
    .sortedBy { it.investmentNumber }

fun WorkPackageInvestmentEntity.toWorkPackageInvestment() = WorkPackageInvestment(
    id = id,
    investmentNumber = investmentNumber,
    expectedDeliveryPeriod = expectedDeliveryPeriod,
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
    documentationExpectedImpacts = translatedValues.mapTo(HashSet()) {
        InputTranslation(it.investmentTranslation.language, it.documentationExpectedImpacts)
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
    deactivated = deactivated,
)

fun WorkPackageInvestment.toWorkPackageInvestmentEntity(workPackageEntity: WorkPackageEntity) =
    WorkPackageInvestmentEntity(
        workPackage = workPackageEntity,
        investmentNumber = investmentNumber,
        expectedDeliveryPeriod = expectedDeliveryPeriod,
        address = address?.toAddressEntity(),
        deactivated = deactivated,
        id = id ?: 0L
    ).apply {
        translatedValues.addAll(
            title.plus(justificationExplanation)
                .plus(justificationTransactionalRelevance)
                .plus(justificationBenefits)
                .plus(justificationPilot)
                .plus(risk)
                .plus(documentation)
                .plus(documentationExpectedImpacts)
                .plus(ownershipSiteLocation)
                .plus(ownershipMaintenance)
                .plus(ownershipRetain)
                .mapTo(HashSet()){it.language}
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
                        documentationExpectedImpacts = documentationExpectedImpacts.firstOrNull { it.language === language }?.translation ?: "",
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
    country = this.country,
    countryCode = this.countryCode,
    nutsRegion2 = this.nutsRegion2,
    nutsRegion2Code = this.nutsRegion2Code,
    nutsRegion3 = this.nutsRegion3,
    nutsRegion3Code = this.nutsRegion3Code,
    street = this.street,
    houseNumber = this.houseNumber,
    postalCode = this.postalCode,
    city = this.city
)

fun AddressEntity.toAddress() = Address(
    country = this.country,
    countryCode = this.countryCode,
    nutsRegion2 = this.nutsRegion2,
    nutsRegion2Code = this.nutsRegion2Code,
    nutsRegion3 = this.nutsRegion3,
    nutsRegion3Code = this.nutsRegion3Code,
    street = this.street,
    houseNumber = this.houseNumber,
    postalCode = this.postalCode,
    city = this.city
)

fun List<WorkPackageInvestmentRow>.toWorkPackageInvestmentHistoricalData() =
    this.groupBy { it.id }.map { groupedRows -> WorkPackageInvestment(
        id = groupedRows.value.first().id,
        investmentNumber = groupedRows.value.first().investmentNumber,
        title = groupedRows.value.extractField { it.title },
        expectedDeliveryPeriod = groupedRows.value.firstOrNull()?.expectedDeliveryPeriod,
        justificationExplanation = groupedRows.value.extractField { it.justificationExplanation },
        justificationTransactionalRelevance = groupedRows.value.extractField { it.justificationTransactionalRelevance },
        justificationBenefits = groupedRows.value.extractField { it.justificationBenefits },
        justificationPilot = groupedRows.value.extractField { it.justificationPilot },
        address = Address(
            country = groupedRows.value.first().country,
            countryCode = groupedRows.value.first().countryCode,
            nutsRegion2 = groupedRows.value.first().nutsRegion2,
            nutsRegion2Code = groupedRows.value.first().nutsRegion2Code,
            nutsRegion3 = groupedRows.value.first().nutsRegion3,
            nutsRegion3Code = groupedRows.value.first().nutsRegion3Code,
            street = groupedRows.value.first().street,
            houseNumber = groupedRows.value.first().houseNumber,
            postalCode = groupedRows.value.first().postalCode,
            city = groupedRows.value.first().city
        ),
        risk = groupedRows.value.extractField { it.risk },
        documentation = groupedRows.value.extractField { it.documentation },
        documentationExpectedImpacts = groupedRows.value.extractField { it.documentationExpectedImpacts },
        ownershipSiteLocation = groupedRows.value.extractField { it.ownershipSiteLocation },
        ownershipRetain = groupedRows.value.extractField { it.ownershipRetain },
        ownershipMaintenance = groupedRows.value.extractField { it.ownershipMaintenance },
        deactivated = groupedRows.value.first().deactivated ?: false,
    ) }.first()

fun List<WorkPackageInvestmentRow>.toWorkPackageInvestmentHistoricalList() =
    this.groupBy { it.id }.map { groupedRows -> WorkPackageInvestment(
        id = groupedRows.value.first().id,
        investmentNumber = groupedRows.value.first().investmentNumber,
        title = groupedRows.value.extractField { it.title },
        justificationExplanation = groupedRows.value.extractField { it.justificationExplanation },
        justificationTransactionalRelevance = groupedRows.value.extractField { it.justificationTransactionalRelevance },
        justificationBenefits = groupedRows.value.extractField { it.justificationBenefits },
        justificationPilot = groupedRows.value.extractField { it.justificationPilot },
        address = Address(
            country = groupedRows.value.first().country,
            countryCode = groupedRows.value.first().countryCode,
            nutsRegion2 = groupedRows.value.first().nutsRegion2,
            nutsRegion2Code = groupedRows.value.first().nutsRegion2Code,
            nutsRegion3 = groupedRows.value.first().nutsRegion3,
            nutsRegion3Code = groupedRows.value.first().nutsRegion3Code,
            street = groupedRows.value.first().street,
            houseNumber = groupedRows.value.first().houseNumber,
            postalCode = groupedRows.value.first().postalCode,
            city = groupedRows.value.first().city
        ),
        risk = groupedRows.value.extractField { it.risk },
        documentation = groupedRows.value.extractField { it.documentation },
        documentationExpectedImpacts = groupedRows.value.extractField { it.documentationExpectedImpacts },
        ownershipSiteLocation = groupedRows.value.extractField { it.ownershipSiteLocation },
        ownershipRetain = groupedRows.value.extractField { it.ownershipRetain },
        ownershipMaintenance = groupedRows.value.extractField { it.ownershipMaintenance },
        deactivated = groupedRows.value.first().deactivated ?: false,
        expectedDeliveryPeriod = groupedRows.value.first().expectedDeliveryPeriod
    ) }.sortedBy { it.investmentNumber }

fun List<WorkPackageSummaryRow>.toWorkPackageInvestmentSummaryList(workPackageNumber: Int?) =
    this.map {
        InvestmentSummary(
            id = it.id,
            investmentNumber = it.investmentNumber,
            workPackageNumber = workPackageNumber,
            deactivated = it.deactivated,
        )
    }
