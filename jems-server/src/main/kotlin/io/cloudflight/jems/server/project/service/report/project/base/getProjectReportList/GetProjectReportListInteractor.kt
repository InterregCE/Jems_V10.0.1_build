package io.cloudflight.jems.server.project.service.report.project.base.getProjectReportList

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetProjectReportListInteractor {

    fun findAll(projectId: Long, pageable: Pageable): Page<ProjectReportSummary>

}
