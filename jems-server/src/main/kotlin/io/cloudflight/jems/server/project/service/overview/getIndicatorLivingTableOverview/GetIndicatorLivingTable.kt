package io.cloudflight.jems.server.project.service.overview.getIndicatorLivingTableOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.report.model.overview.ProjectReportLivingTableLine
import io.cloudflight.jems.server.project.service.report.model.overview.ProjectReportOutputIndicatorsAndResultsLivingTable
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportLineOverview.Companion.emptyOutputIndicator
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportLineOverview.Companion.emptyResultIndicator
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.OutputRowWithCurrent
import io.cloudflight.jems.server.project.service.result.model.ProjectResultWithCurrent
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetIndicatorLivingTable(
    private val workPlanPersistence: ProjectReportWorkPlanPersistence,
    private val resultPrinciplePersistence: ProjectReportResultPrinciplePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
    private val workPackagePersistence: WorkPackagePersistence,
    private val projectResultPersistence: ProjectResultPersistence,
    private val listOutputIndicatorsPersistence: OutputIndicatorPersistence,
    private val listResultIndicatorsPersistence: ResultIndicatorPersistence,
    private val projectVersionPersistence: ProjectVersionPersistence,
) : GetIndicatorLivingTableInteractor {

    companion object {
        private fun Collection<ResultIndicatorSummary>.byNullableId() = map {
            ProjectReportResultIndicatorOverview(
                id = it.id,
                identifier = it.identifier,
                name = it.name,
                measurementUnit = it.measurementUnit,
                baselineIndicator = it.baseline ?: BigDecimal.ZERO,
                baselines = emptyList(),
                targetValue = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                currentReport = BigDecimal.ZERO
            )
        }.associateBy { it.id }.plus(null to emptyResultIndicator())

        private fun Set<OutputIndicatorSummary>.byNullableId() = map {
            ProjectReportOutputIndicatorOverview(
                id = it.id,
                identifier = it.identifier,
                measurementUnit = it.measurementUnit,
                resultIndicator = null,
                targetValue = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                currentReport = BigDecimal.ZERO
            )
        }.associateBy { it.id }.plus(null to emptyOutputIndicator())
    }

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetResultIndicatorLivingTableException::class)
    override fun getResultIndicatorLivingTable(
        projectId: Long,
    ): Map<ProjectReportResultIndicatorOverview, ProjectReportOutputIndicatorsAndResultsLivingTable> {
        val lastApprovedVersion = projectVersionPersistence.getLatestApprovedOrCurrent(projectId)

        val programmeOutputIndicatorsById = listOutputIndicatorsPersistence.getTop250OutputIndicators().byNullableId()
        val programmeResultIndicatorsById = listResultIndicatorsPersistence.getTop50ResultIndicators().byNullableId()

        val projectOutputs = workPackagePersistence.getAllOutputsForProjectIdSortedByNumbers(projectId, lastApprovedVersion)
            .toOutputModelWithCurrent()
        val projectResults = projectResultPersistence.getResultsForProject(projectId, lastApprovedVersion)
            .toResultModelWithCurrent()

        // prepare overview table
        val overview = projectOutputs.plus(projectResults).groupBy { it.retrieveResultIndicatorId() }
            .mapValues { (_, resultsAndOutputs) ->
                ProjectReportOutputIndicatorsAndResultsLivingTable(
                    outputIndicators = resultsAndOutputs.retrieveOutputs().groupBy { it.retrieveOutputIndicatorId() }
                        .mapKeys { programmeOutputIndicatorsById[it.key]!! },
                    results = resultsAndOutputs.retrieveResults(),
                )
            }.mapKeys { programmeResultIndicatorsById[it.key]!! }

        val submittedReportIds = projectReportPersistence.getSubmittedProjectReports(projectId).map { it.id }.toSet()

        val reportOutputsCurrent = workPlanPersistence
            .getReportWorkPackageOutputsByIdAndReportIdIn(projectId, submittedReportIds).groupByOutputNumbers()

        val reportResultsCurrent = resultPrinciplePersistence
            .getProjectResultPrinciplesForLivingTable(projectId, submittedReportIds).groupByResultNumbers()

        // fill in current values
        overview.forEach { (resultIndicator, resultsAndOutputs) ->
            resultsAndOutputs.outputIndicators.forEach { (outputIndicator, outputs) ->
                outputs.fillInCurrentOutputValues(valuesByWpAndOutput = reportOutputsCurrent)
                outputIndicator.currentReport = outputs.sumOf { it.current }
                outputIndicator.targetValue = outputs.sumOf { it.outputTargetValue }
            }
            resultsAndOutputs.results.fillInCurrentResultValues(valuesByResultNr = reportResultsCurrent)
            resultIndicator.baselines = resultsAndOutputs.results.mapTo(LinkedHashSet()) { it.baseline }.toList()
            resultIndicator.currentReport = resultsAndOutputs.results.sumOf { it.current }
            resultIndicator.targetValue = resultsAndOutputs.results.sumOf { it.targetValue ?: BigDecimal.ZERO } //TODO: might be wrong
        }

        return overview
    }

    private fun List<ProjectReportLivingTableLine>.retrieveResults(): List<ProjectResultWithCurrent> =
        filterIsInstance<ProjectResultWithCurrent>()

    private fun List<ProjectReportLivingTableLine>.retrieveOutputs(): List<OutputRowWithCurrent> =
        filterIsInstance<OutputRowWithCurrent>()

}
