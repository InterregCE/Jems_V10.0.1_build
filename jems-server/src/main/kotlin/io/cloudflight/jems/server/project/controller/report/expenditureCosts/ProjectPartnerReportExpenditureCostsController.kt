package io.cloudflight.jems.server.project.controller.report.expenditureCosts

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportExpenditureCostDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportExpenditureCostsApi
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure.GetProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure.UpdateProjectPartnerReportExpenditureInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportExpenditureCostsController(
    private val getProjectPartnerReportExpenditureInteractor: GetProjectPartnerReportExpenditureInteractor,
    private val updateProjectPartnerReportExpenditureInteractor: UpdateProjectPartnerReportExpenditureInteractor,
) : ProjectPartnerReportExpenditureCostsApi {

    override fun getProjectPartnerReports(
        partnerId: Long, reportId: Long
    ): List<ProjectPartnerReportExpenditureCostDTO> =
        getProjectPartnerReportExpenditureInteractor.getExpenditureCosts(
            partnerId = partnerId,
            reportId = reportId,
        ).toDto()

    override fun updatePartnerReportExpenditures(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCostDTO>
    ): List<ProjectPartnerReportExpenditureCostDTO> =
        updateProjectPartnerReportExpenditureInteractor.updatePartnerReportExpenditureCosts(
            partnerId = partnerId,
            reportId = reportId,
            expenditureCosts = expenditureCosts.toModel()
        ).toDto()
}
