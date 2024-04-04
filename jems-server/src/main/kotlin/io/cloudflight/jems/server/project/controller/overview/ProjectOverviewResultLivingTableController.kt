package io.cloudflight.jems.server.project.controller.overview

import io.cloudflight.jems.api.project.dto.report.overview.resultIndicator.ProjectReportResultIndicatorLivingTableDTO
import io.cloudflight.jems.api.project.overview.ProjectOverviewResultLivingTableApi
import io.cloudflight.jems.server.project.service.overview.getIndicatorLivingTableOverview.GetIndicatorLivingTableInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectOverviewResultLivingTableController(
    private val getResultOverview: GetIndicatorLivingTableInteractor,
): ProjectOverviewResultLivingTableApi {

    override fun getResultOverview(projectId: Long): List<ProjectReportResultIndicatorLivingTableDTO> =
        this.getResultOverview.getResultIndicatorLivingTable(projectId).toResultDto()

}
