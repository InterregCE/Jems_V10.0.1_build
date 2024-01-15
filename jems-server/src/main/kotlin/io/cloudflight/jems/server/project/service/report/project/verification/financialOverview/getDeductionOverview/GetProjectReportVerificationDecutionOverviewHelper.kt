package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getDeductionOverview

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.VerificationDeductionOverviewRow
import java.math.BigDecimal

private val emptySumUp = VerificationDeductionOverviewRow(
    typologyOfErrorId = null,
    typologyOfErrorName = null,
    staffCost = BigDecimal.ZERO,
    officeAndAdministration = BigDecimal.ZERO,
    travelAndAccommodation = BigDecimal.ZERO,
    externalExpertise = BigDecimal.ZERO,
    equipment = BigDecimal.ZERO,
    infrastructureAndWorks = BigDecimal.ZERO,
    lumpSums = BigDecimal.ZERO,
    unitCosts = BigDecimal.ZERO,
    otherCosts = BigDecimal.ZERO,
    total = BigDecimal.ZERO,
)

fun List<VerificationDeductionOverviewRow>?.sumUp(): VerificationDeductionOverviewRow {
    if (this == null)
        return emptySumUp
    return this.fold(emptySumUp) { first, second ->
        VerificationDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = first.staffCost.plus(second.staffCost),
            officeAndAdministration = first.officeAndAdministration.plus(second.officeAndAdministration),
            travelAndAccommodation = first.travelAndAccommodation.plus(second.travelAndAccommodation ),
            externalExpertise = first.externalExpertise.plus(second.externalExpertise),
            equipment = first.equipment.plus(second.equipment),
            infrastructureAndWorks = first.infrastructureAndWorks .plus(second.infrastructureAndWorks ),
            lumpSums = first.lumpSums.plus(second.lumpSums),
            unitCosts = first.unitCosts.plus(second.unitCosts),
            otherCosts = first.otherCosts.plus(second.otherCosts),
            total = first.total.plus(second.total),
        )
    }
}
