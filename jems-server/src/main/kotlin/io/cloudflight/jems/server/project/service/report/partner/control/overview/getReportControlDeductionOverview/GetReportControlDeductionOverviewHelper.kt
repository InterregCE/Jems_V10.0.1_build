package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlDeductionOverview

import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverviewRow
import java.math.BigDecimal

private val emptySumUp = ControlDeductionOverviewRow(
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

fun List<ControlDeductionOverviewRow>?.sumUp(): ControlDeductionOverviewRow {
    if (this == null)
        return emptySumUp
    return this.fold(emptySumUp) { first, second ->
        ControlDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = first.staffCost!!.plus(second.staffCost ?: BigDecimal.ZERO),
            officeAndAdministration = first.officeAndAdministration!!.plus(second.officeAndAdministration ?: BigDecimal.ZERO),
            travelAndAccommodation = first.travelAndAccommodation!!.plus(second.travelAndAccommodation ?: BigDecimal.ZERO),
            externalExpertise = first.externalExpertise!!.plus(second.externalExpertise ?: BigDecimal.ZERO),
            equipment = first.equipment!!.plus(second.equipment ?: BigDecimal.ZERO),
            infrastructureAndWorks = first.infrastructureAndWorks!!.plus(second.infrastructureAndWorks ?: BigDecimal.ZERO),
            lumpSums = first.lumpSums!!.plus(second.lumpSums ?: BigDecimal.ZERO),
            unitCosts = first.unitCosts!!.plus(second.unitCosts ?: BigDecimal.ZERO),
            otherCosts = first.otherCosts!!.plus(second.otherCosts ?: BigDecimal.ZERO),
            total = first.total.plus(second.total),
        )
    }
}
