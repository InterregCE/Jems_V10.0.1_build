package io.cloudflight.jems.server.project.service.report.partner.control.overview.getReportControlWorkOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.ProjectPartnerReportExpenditureVerificationPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCoFinancingPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.calculateCurrent
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.percentageOf
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetReportControlWorkOverview(
    private val reportCoFinancingPersistence: ProjectPartnerReportExpenditureCoFinancingPersistence,
    private val reportControlExpenditurePersistence: ProjectPartnerReportExpenditureVerificationPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
) : GetReportControlWorkOverviewInteractor {

    @CanViewPartnerControlReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportControlWorkOverviewException::class)
    override fun get(partnerId: Long, reportId: Long): ControlWorkOverview {
        val currentExpenditures = reportControlExpenditurePersistence
            .getPartnerControlReportExpenditureVerification(partnerId, reportId = reportId)

        val costCategories = reportExpenditureCostCategoryPersistence.getCostCategories(partnerId, reportId = reportId)

        val controlSample = currentExpenditures.onlySamplingOnes().sum()
        val parkedSum = currentExpenditures.onlyParkedOnes().calculateCurrent(costCategories.options).sum
        val eligibleAfterControl = currentExpenditures.calculateCertified(costCategories.options).sum

        val currentReportSum = reportCoFinancingPersistence.getReportCurrentSum(partnerId, reportId = reportId)

        return ControlWorkOverview(
            declaredByPartner = currentReportSum,
            inControlSample = controlSample,
            inControlSamplePercentage = controlSample.percentageOf(currentReportSum),
            parked = parkedSum,
            deductedByControl = currentReportSum.minus(eligibleAfterControl).minus(parkedSum),
            eligibleAfterControl = eligibleAfterControl,
            eligibleAfterControlPercentage = eligibleAfterControl.percentageOf(currentReportSum),
        )
    }

    private fun Collection<BigDecimal?>.sum() = sumOf { it ?: BigDecimal.ZERO }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.onlySamplingOnes() =
        filter { it.partOfSample }.map { it.declaredAmountAfterSubmission }

    private fun Collection<ProjectPartnerReportExpenditureVerification>.onlyParkedOnes() =
        filter { it.parked }

}
