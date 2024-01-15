package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview

import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.ExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverviewLine
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.calculateCertified
import io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview.extractFlatRatesSum
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCostCategoriesFor
import io.cloudflight.jems.server.project.service.report.percentageOf
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.calculateVerified
import io.cloudflight.jems.server.project.service.report.project.verification.expenditure.ProjectReportVerificationExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.project.verification.onlyParkedOnes
import io.cloudflight.jems.server.project.service.report.project.verification.toIdentifiers
import io.cloudflight.jems.server.project.service.report.project.verification.toVerification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectReportVerificationWorkOverviewCalculator(
    private val verificationExpenditurePersistence: ProjectReportVerificationExpenditurePersistence,
    private val partnerReportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
    private val reportPersistence: ProjectReportPersistence,
    private val callPersistence: CallPersistence,
    private val reportSpfClaimPersistence: ProjectReportSpfContributionClaimPersistence,
) {

    companion object {
        fun spfRow(value: BigDecimal) = emptySumUp.copy(
            spfLine = true,
            requestedByPartner = value,
            requestedByPartnerWithoutFlatRates = value,
            afterVerification = value,
            afterVerificationPercentage = BigDecimal.valueOf(100L),
        )
    }

    @Transactional(readOnly = true)
    fun getWorkOverviewPerPartner(reportId: Long): VerificationWorkOverview {
        val expendituresByCertificate = verificationExpenditurePersistence.getProjectReportExpenditureVerification(reportId)
                .groupBy({ it.toIdentifiers() }, { it.toVerification() })

        val costCategoriesByCertificate = partnerReportExpenditureCostCategoryPersistence.getCostCategoriesFor(
            expendituresByCertificate.keys.mapTo(HashSet()) { it.partnerReportId })

        val certificates = expendituresByCertificate.map { (identifiers, verifications) ->
            val costCategories = costCategoriesByCertificate[identifiers.partnerReportId]!!

            val verificationSample = verifications.sumOfSamplingOnes()
            val parked = verifications.onlyParkedOnes().calculateCertified(costCategories.options).sum

            val afterVerification = verifications.calculateVerified(costCategories.options).sum

            val currentReport = costCategories.totalEligibleAfterControl.sum
            val currentReportFlatRates = costCategories.totalEligibleAfterControl.extractFlatRatesSum(costCategories.options)
            val currentReportWithoutFlatRates = currentReport.minus(currentReportFlatRates)

            return@map VerificationWorkOverviewLine(
                partnerId = identifiers.partnerId,
                partnerRole = identifiers.partnerRole,
                partnerNumber = identifiers.partnerNumber,
                partnerReportId = identifiers.partnerReportId,
                partnerReportNumber = identifiers.partnerReportNumber,
                spfLine = false,
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
        }.toMutableList()

        val projectId = reportPersistence.getReportByIdUnSecured(reportId).projectId
        if (callPersistence.getCallByProjectId(projectId).isSpf()) {
            val spfAmount = reportSpfClaimPersistence.getCurrentSpfContribution(reportId).sum
            certificates.add(spfRow(value = spfAmount))
        }

        return VerificationWorkOverview(
            certificates = certificates,
            total = certificates.sumUp(),
        )
    }

    private fun Collection<ExpenditureVerification>.sumOfSamplingOnes() =
        filter { it.partOfSample }.sumOf { it.certifiedAmount }

    private fun Collection<ExpenditureVerification>.calculateDeductedJs(options: ProjectPartnerBudgetOptions) =
        calculateCostCategoriesFor(options) { it.deductedByJs }

    private fun Collection<ExpenditureVerification>.calculateDeductedMa(options: ProjectPartnerBudgetOptions) =
        calculateCostCategoriesFor(options) { it.deductedByMa }

}
