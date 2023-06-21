package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportResultIndicatorOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetProjectReportResultIndicatorOverview(
    private val workPlanPersistence: ProjectReportWorkPlanPersistence,
) : GetProjectReportResultIndicatorOverviewInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportResultIndicatorOverviewException::class)
    override fun getResultIndicatorOverview(
        projectId: Long, reportId: Long
    ): Map<ProjectReportResultIndicatorOverview, Map<ProjectReportOutputIndicatorOverview, List<ProjectReportOutputLineOverview>>> {
        val workPackageOutputs = workPlanPersistence.getReportWorkPackageOutputsById(projectId, reportId)

        val resultMap = workPackageOutputs.groupBy { it.outputIndicator ?: emptyOutputIndicator() }
            .entries.groupBy { it.key.resultIndicator ?: emptyResultIndicator() }
            .mapValues { it.value.associateBy({ it.key }, { it.value }) }

        resultMap.forEach { (resultIndicator, outputIndicatorsMap) ->
            outputIndicatorsMap.forEach { (outputIndicator, outputs) ->
                outputIndicator.targetValue = outputs.sumOf { it.targetValue }
                outputIndicator.previouslyReported = outputs.sumOf { it.previouslyReported }
                outputIndicator.currentReport = outputs.sumOf { it.currentReport }
            }
            resultIndicator.targetValue = outputIndicatorsMap.keys.sumOf { it.targetValue }
            resultIndicator.previouslyReported = outputIndicatorsMap.keys.sumOf { it.previouslyReported }
            resultIndicator.currentReport = outputIndicatorsMap.keys.sumOf { it.currentReport }
        }

        return resultMap
    }


    private fun emptyOutputIndicator() = ProjectReportOutputIndicatorOverview(
        id = null,
        identifier = "",
        name = emptySet(),
        measurementUnit = emptySet(),
        resultIndicator = null,
        targetValue = BigDecimal.ZERO,
        currentReport = BigDecimal.ZERO,
        previouslyReported = BigDecimal.ZERO,
    )

    private fun emptyResultIndicator() = ProjectReportResultIndicatorOverview(
        id = null,
        identifier = null,
        name = emptySet(),
        measurementUnit = emptySet(),
        baseline = BigDecimal.ZERO,
        targetValue = BigDecimal.ZERO,
        currentReport = BigDecimal.ZERO,
        previouslyReported = BigDecimal.ZERO,
    )
}

