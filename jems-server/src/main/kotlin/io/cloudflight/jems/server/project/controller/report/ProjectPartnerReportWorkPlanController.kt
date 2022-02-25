package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportWorkPlanApi
import io.cloudflight.jems.server.project.service.report.partner.workPlan.getProjectPartnerWorkPlan.GetProjectPartnerReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.partner.workPlan.updateProjectPartnerWorkPlan.UpdateProjectPartnerReportWorkPlanInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportWorkPlanController(
    private val getPartnerReportWorkPlan: GetProjectPartnerReportWorkPlanInteractor,
    private val updatePartnerReportWorkPlan: UpdateProjectPartnerReportWorkPlanInteractor,
) : ProjectPartnerReportWorkPlanApi {

    override fun getWorkPlan(partnerId: Long, reportId: Long): List<ProjectPartnerReportWorkPackageDTO> =
        getPartnerReportWorkPlan.getForPartner(partnerId = partnerId, reportId = reportId).toDto()

    override fun updateWorkPlan(
        partnerId: Long,
        reportId: Long,
        workPackages: List<UpdateProjectPartnerReportWorkPackageDTO>
    ): List<ProjectPartnerReportWorkPackageDTO> =
        updatePartnerReportWorkPlan.update(
            partnerId = partnerId,
            reportId = reportId,
            workPlan = workPackages.toModel(),
        ).toDto()

}
