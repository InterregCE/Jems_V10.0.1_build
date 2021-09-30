package io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.IndicatorOutput
import io.cloudflight.jems.server.project.service.result.model.IndicatorOverviewLine
import io.cloudflight.jems.server.project.service.result.model.IndicatorResult
import io.cloudflight.jems.server.project.service.result.model.OutputRow
import io.cloudflight.jems.server.project.service.result.model.ProjectOutput
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

@Service
class GetProjectResultIndicatorsOverview(
    private val workPackagePersistence: WorkPackagePersistence,
    private val projectResultPersistence: ProjectResultPersistence,
    private val listOutputIndicatorsPersistence: OutputIndicatorPersistence,
    private val listResultIndicatorsPersistence: ResultIndicatorPersistence,
) : GetProjectResultIndicatorsOverviewInteractor {

    @CanRetrieveProjectForm
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectResultIndicatorsOverviewException::class)
    override fun getProjectResultIndicatorOverview(projectId: Long, version: String?): List<IndicatorOverviewLine> {
        val outputs = workPackagePersistence.getAllOutputsForProjectIdSortedByNumbers(projectId, version)

        val targetSumsPerOutputId = outputs
            .filter{ it.programmeOutputId != null }
            .groupBy({ it.programmeOutputId!! }, { it.outputTargetValue })
            .mapValues { it.value.sumUp() }

        val programmeOutputIndicatorsById = listOutputIndicatorsPersistence.getTop50OutputIndicators()
            .associateBy { it.id }

        val programmeResultIndicatorsById = listResultIndicatorsPersistence.getTop50ResultIndicators()
            .associateBy { it.id }

        val projectResultsByIndicatorId = projectResultPersistence.getResultsForProject(projectId, version)
            .filter { it.programmeResultIndicatorId != null }
            .groupBy { it.programmeResultIndicatorId }
            .toMutableMap()

        val result = outputs.mapIndexed { index, model ->
            val outputIndicator = programmeOutputIndicatorsById[model.programmeOutputId]
            val outputIndicatorTargetValueSumUp = targetSumsPerOutputId[outputIndicator?.id] ?: ZERO
            val resultIndicator = programmeResultIndicatorsById[model.programmeResultId]
            val projectResults = projectResultsByIndicatorId[resultIndicator?.id] ?: emptyList()

            IndicatorOverviewLine(
                outputIndicator = outputIndicator?.toIndicatorOutput(targetValueSumUp = outputIndicatorTargetValueSumUp),
                projectOutput = model.toProjectOutput(),
                resultIndicator = resultIndicator.toIndicatorResult(projectResults),
                onlyResultWithoutOutputs = false,
            )
        }.toMutableList()

        outputs.mapNotNullTo(HashSet()) { it.programmeResultId }.forEach {
            projectResultsByIndicatorId.remove(it)
        }

        // Add all result indicators used in Project Results, but not linked to any work-package-outputs
        result.addAll(
            projectResultsByIndicatorId.map { IndicatorOverviewLine(
                outputIndicator = null,
                projectOutput = null,
                resultIndicator = programmeResultIndicatorsById[it.key].toIndicatorResult(it.value),
                onlyResultWithoutOutputs = true,
            ) }
        )

        return result
    }

    private fun Collection<BigDecimal>.sumUp() = fold(ZERO) { first, second -> first.add(second) }

    private fun OutputRow.toProjectOutput() = ProjectOutput(
        projectOutputNumber = "$workPackageNumber.$outputNumber",
        projectOutputTitle = outputTitle,
        projectOutputTargetValue = outputTargetValue,
    )

    private fun ResultIndicatorSummary?.toIndicatorResult(projectResults: List<ProjectResult>) = if (this == null) null else IndicatorResult(
        id = id!!,
        identifier = identifier,
        name = name,
        measurementUnit = measurementUnit,
        baseline = projectResults.mapTo(HashSet()) { it.baseline },
        targetValueSumUp = projectResults.mapNotNull { it.targetValue }.sumUp(),
    )

    private fun OutputIndicatorSummary.toIndicatorOutput(targetValueSumUp: BigDecimal) = IndicatorOutput(
        id = id!!,
        identifier = identifier,
        name = name,
        measurementUnit = measurementUnit,
        targetValueSumUp = targetValueSumUp,
    )

}
