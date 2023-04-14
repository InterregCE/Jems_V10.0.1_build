package io.cloudflight.jems.server.project.service.report.project.resultPrinciple

import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrincipleUpdate
import java.math.BigDecimal

interface ProjectReportResultPrinciplePersistence {

    fun getProjectResultPrinciples(projectId: Long, reportId: Long): ProjectReportResultPrinciple

    fun updateProjectReportResultPrinciple(
        projectId: Long,
        reportId: Long,
        newResultsAndPrinciples: ProjectReportResultPrincipleUpdate,
    ): ProjectReportResultPrinciple

    fun getResultCumulative(reportIds: Set<Long>): Map<Int, BigDecimal>

    fun deleteProjectResultPrinciples(reportId: Long)
}
