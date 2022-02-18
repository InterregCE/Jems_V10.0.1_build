package io.cloudflight.jems.server.project.service.report.partner.workPlan.getProjectPartnerWorkPlan

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportWorkPlan(
    private val reportPersistence: ProjectReportPersistence,
) : GetProjectPartnerReportWorkPlanInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportWorkPlanException::class)
    override fun getForPartner(partnerId: Long, reportId: Long): List<ProjectPartnerReportWorkPackage> =
        reportPersistence.getPartnerReportWorkPlanById(partnerId = partnerId, reportId = reportId)

}
