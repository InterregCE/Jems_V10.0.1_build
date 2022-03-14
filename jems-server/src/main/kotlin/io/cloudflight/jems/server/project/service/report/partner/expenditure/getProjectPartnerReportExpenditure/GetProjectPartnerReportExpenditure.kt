package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import org.springframework.stereotype.Service

@Service
class GetProjectPartnerReportExpenditure(
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
) : GetProjectPartnerReportExpenditureInteractor {

    @CanViewPartnerReport
    @ExceptionWrapper(GetProjectPartnerReportExpenditureException::class)
    override fun getExpenditureCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportExpenditureCost> =
        reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId = partnerId, reportId = reportId)

}
