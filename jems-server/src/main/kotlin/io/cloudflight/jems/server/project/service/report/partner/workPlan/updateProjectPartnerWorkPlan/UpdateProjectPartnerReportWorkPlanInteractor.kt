package io.cloudflight.jems.server.project.service.report.partner.workPlan.updateProjectPartnerWorkPlan

import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.update.UpdateProjectPartnerReportWorkPackage

interface UpdateProjectPartnerReportWorkPlanInteractor {

    fun update(
        partnerId: Long,
        reportId: Long,
        workPlan: List<UpdateProjectPartnerReportWorkPackage>
    ): List<ProjectPartnerReportWorkPackage>

}
