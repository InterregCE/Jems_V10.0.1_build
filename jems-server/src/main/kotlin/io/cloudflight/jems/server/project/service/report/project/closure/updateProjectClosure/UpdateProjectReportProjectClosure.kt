package io.cloudflight.jems.server.project.service.report.project.closure.updateProjectClosure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.closure.ProjectReportProjectClosure
import io.cloudflight.jems.server.project.service.report.project.closure.ProjectReportProjectClosurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportProjectClosure(
    private val projectReportProjectClosurePersistence: ProjectReportProjectClosurePersistence,
    private val generalValidatorService: GeneralValidatorService
): UpdateProjectReportProjectClosureInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateProjectReportProjectClosureException::class)
    override fun update(
        reportId: Long,
        projectClosure: ProjectReportProjectClosure
    ): ProjectReportProjectClosure {
        validateInputs(projectClosure)
        return projectReportProjectClosurePersistence.updateProjectReportProjectClosure(reportId, projectClosure)
    }

    private fun validateInputs(projectClosure: ProjectReportProjectClosure) {
        if (projectClosure.prizes.size > 100)
            throw ProjectClosurePrizeLimitNumberExceededException()

        generalValidatorService.throwIfAnyIsInvalid(
            generalValidatorService.maxLength(projectClosure.story, 5000, "story"),
            *projectClosure.prizes.map { generalValidatorService.maxLength(it.prize, 500, "prize") }
                .toTypedArray(),
        )
    }
}
