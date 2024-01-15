package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview

import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverviewLine
import io.cloudflight.jems.server.project.service.report.percentageOf
import java.math.BigDecimal

val emptySumUp = VerificationWorkOverviewLine(
    partnerId = 0L,
    partnerRole = null,
    partnerNumber = 0,
    partnerReportId = 0,
    partnerReportNumber = 0,
    spfLine = false,
    requestedByPartner = BigDecimal.ZERO,
    requestedByPartnerWithoutFlatRates = BigDecimal.ZERO,
    inVerificationSample = BigDecimal.ZERO,
    inVerificationSamplePercentage = null,
    parked = BigDecimal.ZERO,
    deductedByJs = BigDecimal.ZERO,
    deductedByMa = BigDecimal.ZERO,
    deducted = BigDecimal.ZERO,
    afterVerification = BigDecimal.ZERO,
    afterVerificationPercentage = null,
)

fun Collection<VerificationWorkOverviewLine>.sumUp() =
    fold(emptySumUp) { first, second -> first.plus(second) }

private fun VerificationWorkOverviewLine.plus(other: VerificationWorkOverviewLine): VerificationWorkOverviewLine {
    val requestedSum = requestedByPartner.plus(other.requestedByPartner)
    val requestedWithoutFlatRatesSum = requestedByPartnerWithoutFlatRates.plus(other.requestedByPartnerWithoutFlatRates)
    val inVerificationSampleSum = inVerificationSample.plus(other.inVerificationSample)
    val afterVerificationSum = afterVerification.plus(other.afterVerification)

    return VerificationWorkOverviewLine(
        partnerId = 0L,
        partnerRole = null,
        partnerNumber = 0,

        partnerReportId = 0L,
        partnerReportNumber = 0,
        spfLine = false,

        requestedByPartner = requestedSum,
        requestedByPartnerWithoutFlatRates = requestedWithoutFlatRatesSum,
        inVerificationSample = inVerificationSampleSum,
        inVerificationSamplePercentage = inVerificationSampleSum.percentageOf(requestedWithoutFlatRatesSum),
        parked = parked.plus(other.parked),
        deductedByJs = deductedByJs.plus(other.deductedByJs),
        deductedByMa = deductedByMa.plus(other.deductedByMa),
        deducted = deducted.plus(other.deducted),
        afterVerification = afterVerificationSum,
        afterVerificationPercentage = afterVerificationSum.percentageOf(requestedSum),
    )
}
