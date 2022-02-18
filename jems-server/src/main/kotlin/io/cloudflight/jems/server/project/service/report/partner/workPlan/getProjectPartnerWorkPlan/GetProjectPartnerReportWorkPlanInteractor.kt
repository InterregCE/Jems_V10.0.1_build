package io.cloudflight.jems.server.project.service.report.partner.workPlan.getProjectPartnerWorkPlan

import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage

interface GetProjectPartnerReportWorkPlanInteractor {

    fun getForPartner(partnerId: Long, reportId: Long): List<ProjectPartnerReportWorkPackage>

}
