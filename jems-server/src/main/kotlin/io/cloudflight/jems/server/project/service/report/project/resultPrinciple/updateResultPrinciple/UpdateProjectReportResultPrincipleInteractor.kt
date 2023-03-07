package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.updateResultPrinciple

import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrincipleUpdate

interface UpdateProjectReportResultPrincipleInteractor {

    fun update(projectId: Long, reportId: Long, resultPrinciple: ProjectReportResultPrincipleUpdate): ProjectReportResultPrinciple
}
