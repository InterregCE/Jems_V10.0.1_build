package io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview.ResultOverviewCalculator.Companion.calculateProjectResultOverview
import io.cloudflight.jems.server.project.service.result.model.IndicatorOverviewLine
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectResultIndicatorsOverview(
    private val workPackagePersistence: WorkPackagePersistence,
    private val projectResultPersistence: ProjectResultPersistence,
    private val listOutputIndicatorsPersistence: OutputIndicatorPersistence,
    private val listResultIndicatorsPersistence: ResultIndicatorPersistence
) : GetProjectResultIndicatorsOverviewInteractor {

    @CanRetrieveProjectForm
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectResultIndicatorsOverviewException::class)
    override fun getProjectResultIndicatorOverview(projectId: Long, version: String?): List<IndicatorOverviewLine> {

        return calculateProjectResultOverview(
            projectOutputs = workPackagePersistence.getAllOutputsForProjectIdSortedByNumbers(projectId, version),
            programmeOutputIndicatorsById = listOutputIndicatorsPersistence.getTop50OutputIndicators()
                .associateBy { it.id },
            programmeResultIndicatorsById = listResultIndicatorsPersistence.getTop50ResultIndicators()
                .associateBy { it.id },
            projectResultsByIndicatorId = projectResultPersistence.getResultsForProject(projectId, version)
            .filter { it.programmeResultIndicatorId != null }
            .groupBy { it.programmeResultIndicatorId }
            .toMutableMap()
        )
    }

}
