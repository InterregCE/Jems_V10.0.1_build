package io.cloudflight.jems.server.project.service.result.update_project_results

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectForm
import io.cloudflight.jems.server.project.service.lumpsum.model.CLOSURE_PERIOD_NUMBER
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectResults(
    private val projectResultPersistence: ProjectResultPersistence,
    private val generalValidatorService: GeneralValidatorService
) : UpdateProjectResultsInteractor {

    @CanUpdateProjectForm
    @Transactional
    @ExceptionWrapper(CreateProjectResultExceptions::class)
    override fun updateResultsForProject(projectId: Long, projectResults: List<ProjectResult>): List<ProjectResult> =
        ifInputIsValid(projectResults).run {
            validatePeriods(projectId, projectResults)
            projectResultPersistence.updateResultsForProject(projectId, projectResults)
        }

    private fun validatePeriods(projectId: Long, projectResults: List<ProjectResult>) {
        if (projectResults.isEmpty())
            return

        val availablePeriods = projectResultPersistence.getAvailablePeriodNumbers(projectId).toMutableSet()
        if (availablePeriods.isNotEmpty())
            availablePeriods.add(CLOSURE_PERIOD_NUMBER)

        val usedPeriodNumbers = projectResults.mapNotNullTo(HashSet()) { it.periodNumber }

        if ((usedPeriodNumbers union availablePeriods) != availablePeriods)
            throw PeriodNotFoundException()
    }

    private fun ifInputIsValid(projectResults: List<ProjectResult>) {
        if (projectResults.size > 20)
            throw MaxNumberOrResultPerProjectException(20)

        generalValidatorService.throwIfAnyIsInvalid(
            *projectResults.map { generalValidatorService.maxLength(it.description, 1000, "description") }
                .toTypedArray(),
            *projectResults.map {
                generalValidatorService.numberBetween(
                    it.targetValue, BigDecimal.ZERO, BigDecimal.valueOf(999_999_999_99, 2), "targetValue"
                )
            }.toTypedArray(),
            *projectResults.map { generalValidatorService.scale(it.targetValue, 2, "targetValue") }.toTypedArray(),
            *projectResults.map {
                generalValidatorService.numberBetween(
                    it.baseline, BigDecimal.ZERO, BigDecimal.valueOf(999_999_999_99, 2), "baseline"
                )
            }.toTypedArray(),
            *projectResults.map { generalValidatorService.scale(it.baseline, 2, "baseline") }.toTypedArray(),
        )
    }
}
