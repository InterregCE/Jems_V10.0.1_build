package io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview

import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import io.cloudflight.jems.server.project.service.result.model.IndicatorOutput
import io.cloudflight.jems.server.project.service.result.model.IndicatorOverviewLine
import io.cloudflight.jems.server.project.service.result.model.IndicatorResult
import io.cloudflight.jems.server.project.service.result.model.OutputRow
import io.cloudflight.jems.server.project.service.result.model.ProjectOutput
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import java.math.BigDecimal

class ResultOverviewCalculator {

    companion object {
        fun calculateProjectResultOverview(
            projectOutputs: List<OutputRow>,
            programmeOutputIndicatorsById: Map<Long?, OutputIndicatorSummary>,
            programmeResultIndicatorsById: Map<Long?, ResultIndicatorSummary>,
            projectResultsByIndicatorId: MutableMap<Long?, List<ProjectResult>>
        ): List<IndicatorOverviewLine> {
            val targetSumsPerOutputId = projectOutputs
                .filter { it.programmeOutputId != null }
                .groupBy({ it.programmeOutputId!! }, { it.outputTargetValue })
                .mapValues { it.value.sumUp() }

            val result = projectOutputs.mapIndexed { index, model ->
                val outputIndicator = programmeOutputIndicatorsById[model.programmeOutputId]
                val outputIndicatorTargetValueSumUp = targetSumsPerOutputId[outputIndicator?.id] ?: BigDecimal.ZERO
                val resultIndicator = programmeResultIndicatorsById[model.programmeResultId]
                val projectResults = projectResultsByIndicatorId[resultIndicator?.id] ?: emptyList()

                IndicatorOverviewLine(
                    outputIndicator = outputIndicator?.toIndicatorOutput(targetValueSumUp = outputIndicatorTargetValueSumUp),
                    projectOutput = model.toProjectOutput(),
                    resultIndicator = resultIndicator.toIndicatorResult(projectResults),
                    onlyResultWithoutOutputs = false,
                )
            }.toMutableList()

            projectOutputs.mapNotNullTo(HashSet()) { it.programmeResultId }.forEach {
                projectResultsByIndicatorId.remove(it)
            }

            // Add all result indicators used in Project Results, but not linked to any work-package-outputs
            result.addAll(
                projectResultsByIndicatorId.map {
                    IndicatorOverviewLine(
                        outputIndicator = null,
                        projectOutput = null,
                        resultIndicator = programmeResultIndicatorsById[it.key].toIndicatorResult(it.value),
                        onlyResultWithoutOutputs = true,
                    )
                }
            )

            return result
        }

        private fun Collection<BigDecimal>.sumUp() = fold(BigDecimal.ZERO) { first, second -> first.add(second) }

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
}
