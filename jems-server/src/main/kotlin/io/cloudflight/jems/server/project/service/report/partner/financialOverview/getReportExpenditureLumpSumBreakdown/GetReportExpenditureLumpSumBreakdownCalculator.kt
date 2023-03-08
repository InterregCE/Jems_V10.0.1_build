package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportExpenditureLumpSumBreakdownCalculator(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportLumpSumPersistence: ProjectPartnerReportLumpSumPersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
) {

    @Transactional(readOnly = true)
    fun get(partnerId: Long, reportId: Long): ExpenditureLumpSumBreakdown {
        val report = reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId).status

        val data = reportLumpSumPersistence.getLumpSum(partnerId = partnerId, reportId = reportId)

        if (report.isOpen()) {
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
