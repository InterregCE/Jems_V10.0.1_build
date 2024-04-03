package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportResultIndicatorOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorsAndResults
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportResultIndicatorOverview(
    private val workPlanPersistence: ProjectReportWorkPlanPersistence,
    private val resultPrinciplePersistence: ProjectReportResultPrinciplePersistence
) : GetProjectReportResultIndicatorOverviewInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportResultIndicatorOverviewException::class)
    override fun getResultIndicatorOverview(
        projectId: Long, reportId: Long
    ): Map<ProjectReportResultIndicatorOverview, ProjectReportOutputIndicatorsAndResults> {
        val workPackageOutputs = workPlanPersistence.getReportWorkPackageOutputsById(projectId, reportId)
        val projectResults = resultPrinciplePersistence.getProjectResultPrinciples(projectId, reportId).projectResults

        val overview = workPackageOutputs.plus(projectResults)
            .groupBy { it.retrieveResultIndicator() }
            .mapValues { (_, resultsAndOutputs) ->
                ProjectReportOutputIndicatorsAndResults(
                    outputIndicators = resultsAndOutputs.retrieveOutputs().groupBy { it.retrieveOutputIndicator() },
                    results = resultsAndOutputs.retrieveResults(),
                )
            }

        overview.forEach { (resultIndicator, outIndicatorsAndResults) ->
            outIndicatorsAndResults.outputIndicators.forEach { (outputIndicator, outputs) ->
                outputIndicator.targetValue = outputs.sumOf { it.targetValue }
                outputIndicator.previouslyReported = outputs.sumOf { it.previouslyReported }
                outputIndicator.currentReport = outputs.sumOf { it.currentReport }
            }
            resultIndicator.baselines = outIndicatorsAndResults.results.mapTo(LinkedHashSet()) { it.baseline }.toList()
            resultIndicator.targetValue = outIndicatorsAndResults.results.sumOf { it.targetValue }
            resultIndicator.previouslyReported = outIndicatorsAndResults.results.sumOf { it.previouslyReported }
            resultIndicator.currentReport = outIndicatorsAndResults.results.sumOf { it.currentReport }
        }

        return overview
    }

    private fun List<ProjectReportLineOverview>.retrieveResults(): List<ProjectReportProjectResult> = filterIsInstance<ProjectReportProjectResult>()
    private fun List<ProjectReportLineOverview>.retrieveOutputs(): List<ProjectReportOutputLineOverview> = filterIsInstance<ProjectReportOutputLineOverview>()

}
