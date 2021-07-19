package io.cloudflight.jems.server.project.service.result.update_project_result

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class UpdateProjectResult(
    private val projectResultPersistence: ProjectResultPersistence
) : UpdateProjectResultInteractor {

    companion object {
        private const val MAX_RESULTS_PER_PROJECT = 20
        private const val MAX_DESCRIPTION_LENGTH = 500
        private val MAX_TARGET_VALUE = BigDecimal.valueOf(999_999_999_99, 2)
    }

    @CanUpdateProjectForm
    override fun updateResultsForProject(projectId: Long, projectResults: List<ProjectResult>): List<ProjectResult> {
        validateMaxAllowedSize(projectResults)
        validateInputs(projectResults)
        validatePeriods(projectId, projectResults)

        return projectResultPersistence.updateResultsForProject(projectId, projectResults)
    }

    private fun validateMaxAllowedSize(projectResults: List<ProjectResult>) {
        if (projectResults.size > MAX_RESULTS_PER_PROJECT)
            throw I18nValidationException(i18nKey = "project.results.max.allowed.reached")
    }

    private fun validateInputs(projectResults: List<ProjectResult>) {
        if (projectResults.any { result ->
                result.description.any {
                    !it.translation.isNullOrBlank() && it.translation!!.length > MAX_DESCRIPTION_LENGTH
                }
            })
            throw I18nValidationException(i18nKey = "project.results.description.size.too.long")

        if (projectResults.mapNotNull { it.targetValue }.any {
                it < BigDecimal.ZERO || it > MAX_TARGET_VALUE || it.scale() > 2
            })
            throw I18nValidationException(i18nKey = "project.results.targetValue.not.valid")
    }

    private fun validatePeriods(projectId: Long, projectResults: List<ProjectResult>) {
        val periodNumbers = projectResults.mapNotNullTo(HashSet()) { it.periodNumber }
        if (periodNumbers.isNotEmpty()
            && !projectResultPersistence.getAvailablePeriodNumbers(projectId).containsAll(periodNumbers)) {
            throw I18nValidationException(i18nKey = "project.results.period.does.not.exist")
        }
    }

}
