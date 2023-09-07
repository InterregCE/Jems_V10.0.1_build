package io.cloudflight.jems.server.project.service.report.project.base.getMyProjectReports

import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetMyProjectReportsInteractor {

    fun findAllOfMine(pageable: Pageable): Page<ProjectReportSummary>
}
