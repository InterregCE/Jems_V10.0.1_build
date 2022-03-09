package io.cloudflight.jems.server.project.controller.report.contribution

import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionWrapperDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.UpdateProjectPartnerReportContributionDataDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportContributionApi
import io.cloudflight.jems.server.project.service.report.partner.contribution.getProjectPartnerReportContribution.GetProjectPartnerReportContributionInteractor
import io.cloudflight.jems.server.project.service.report.partner.contribution.updateProjectPartnerReportContribution.UpdateProjectPartnerReportContributionInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectPartnerReportContributionController(
    private val getContribution: GetProjectPartnerReportContributionInteractor,
    private val updateContribution: UpdateProjectPartnerReportContributionInteractor,
) : ProjectPartnerReportContributionApi {

    override fun getContribution(partnerId: Long, reportId: Long) =
        getContribution.getContribution(partnerId = partnerId, reportId = reportId).let { data ->
            ProjectPartnerReportContributionWrapperDTO(
                contributions = data.contributions.map { it.toDto() },
                overview = data.overview.toDto(),
            )
        }

    override fun updateContribution(
        partnerId: Long,
        reportId: Long,
        contributionData: UpdateProjectPartnerReportContributionDataDTO
    ) =
        updateContribution.update(partnerId = partnerId, reportId = reportId, data = contributionData.toModel()).let { data ->
            ProjectPartnerReportContributionWrapperDTO(
                contributions = data.contributions.map { it.toDto() },
                overview = data.overview.toDto(),
            )
        }

}
