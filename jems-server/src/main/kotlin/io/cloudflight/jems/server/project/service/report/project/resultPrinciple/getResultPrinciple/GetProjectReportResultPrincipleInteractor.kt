package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.getResultPrinciple

import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple

interface GetProjectReportResultPrincipleInteractor {

    fun get(projectId: Long, reportId: Long): ProjectReportResultPrinciple
}
