package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectReportLumpSumPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportExpenditureLumpSumBreakdownCalculator(
    private val reportPersistence: ProjectReportPersistence,
    private val reportLumpSumPersistence: ProjectReportLumpSumPersistence,
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
) {

    @Transactional(readOnly = true)
    fun get(partnerId: Long, reportId: Long): ExpenditureLumpSumBreakdown {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId)

        val data = reportLumpSumPersistence.getLumpSum(partnerId = partnerId, reportId = reportId)

        if (!report.status.isClosed()) {
            val currentExpenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)
            data.fillInCurrent(current = currentExpenditures.getCurrentForLumpSums())
        }
        val lumpSumLines = data.fillInOverviewFields()

        return ExpenditureLumpSumBreakdown(
            lumpSums = lumpSumLines,
            total = lumpSumLines.sumUp(),
        )
    }

}
