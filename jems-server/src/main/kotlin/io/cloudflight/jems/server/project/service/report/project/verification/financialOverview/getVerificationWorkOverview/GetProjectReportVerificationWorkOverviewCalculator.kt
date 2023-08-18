package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure.ProjectReportVerificationExpenditureLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureIdentifiers
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverviewLine
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.extractFlatRatesSum
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCostCategoriesFor
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCurrent
import io.cloudflight.jems.server.project.service.report.percentageOf
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectReportVerificationWorkOverviewCalculator(
    private val verificationExpenditurePersistence: ProjectReportVerificationExpenditurePersistence,
    private val partnerReportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
) {

    @Transactional(readOnly = true)
    fun getWorkOverviewPerPartner(reportId: Long): VerificationWorkOverview {
        val expendituresByCertificate = verificationExpenditurePersistence.getProjectReportExpenditureVerification(reportId)
            .groupBy({ it.toIdentifiers()}, { it.toVerification() })

        val certificates = expendituresByCertificate.map { (identifiers, verifications) ->
            val costCategories = partnerReportExpenditureCostCategoryPersistence
                .getCostCategories(identifiers.partnerId, reportId = identifiers.partnerReportId)

            val verificationSample = verifications.sumOfSamplingOnes()
            val parked = verifications.onlyParkedOnes().calculateCurrent(costCategories.options).sum

            val afterVerification = verifications.calculateVerified(costCategories.options).sum

            val currentReport = costCategories.currentlyReported.sum
            val currentReportFlatRates = costCategories.currentlyReported.extractFlatRatesSum(costCategories.options)
            val currentReportWithoutFlatRates = currentReport.minus(currentReportFlatRates)

            return@map VerificationWorkOverviewLine(
                partnerId = identifiers.partnerId,
                partnerRole = identifiers.partnerRole,
                partnerNumber = identifiers.partnerNumber,
                partnerReportId = identifiers.partnerReportId,
                partnerReportNumber = identifiers.partnerReportNumber,
                requestedByPartner = currentReport,
                requestedByPartnerWithoutFlatRates = currentReportWithoutFlatRates,
                inVerificationSample = verificationSample,
                inVerificationSamplePercentage = verificationSample.percentageOf(currentReportWithoutFlatRates) ?: BigDecimal.ZERO,
                parked = parked,
                deductedByJs = verifications.calculateDeductedJs(costCategories.options).sum,
                deductedByMa = verifications.calculateDeductedMa(costCategories.options).sum,
                deducted = currentReport.minus(afterVerification).minus(parked),
                afterVerification = afterVerification,
                afterVerificationPercentage = afterVerification.percentageOf(currentReport) ?: BigDecimal.ZERO,
            )
        }

        return VerificationWorkOverview(
            certificates = certificates,
            total = certificates.sumUp(),
        )
    }

    private fun ProjectReportVerificationExpenditureLine.toIdentifiers() = ExpenditureIdentifiers(
        partnerId = expenditure.partnerId,
        partnerRole = expenditure.partnerRole,
        partnerNumber = expenditure.partnerNumber,
        partnerReportId = expenditure.partnerReportId,
        partnerReportNumber = expenditure.partnerReportNumber,
    )

    private fun ProjectReportVerificationExpenditureLine.toVerification() = ExpenditureVerification(
        id = expenditure.id,
        lumpSumId = expenditure.lumpSum?.lumpSumProgrammeId,
        costCategory = expenditure.costCategory,
        declaredAmountAfterSubmission = expenditure.declaredAmountAfterSubmission,
        parkingMetadata = expenditure.parkingMetadata,
        partOfSample = partOfVerificationSample,
        amountAfterVerification = amountAfterVerification,
        certifiedAmount = expenditure.certifiedAmount,
        parked = parked,
        deductedByJs = deductedByJs,
        deductedByMa = deductedByMa,
    )

    private fun Collection<ExpenditureVerification>.sumOfSamplingOnes() =
        filter { it.partOfSample }.sumOf { it.certifiedAmount }

    private fun Collection<ExpenditureVerification>.onlyParkedOnes() =
        filter { it.parked }

    private fun Collection<ExpenditureVerification>.calculateVerified(options: ProjectPartnerBudgetOptions) =
        calculateCostCategoriesFor(options) { it.amountAfterVerification }

    private fun Collection<ExpenditureVerification>.calculateDeductedJs(options: ProjectPartnerBudgetOptions) =
        calculateCostCategoriesFor(options) { it.deductedByJs }

    private fun Collection<ExpenditureVerification>.calculateDeductedMa(options: ProjectPartnerBudgetOptions) =
        calculateCostCategoriesFor(options) { it.deductedByMa }

}
