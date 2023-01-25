package io.cloudflight.jems.server.project.service.report.project.identification.updateProjectReportIdentification

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.cloudflight.jems.server.project.service.report.project.identification.ProjectReportIdentificationPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectReportIdentification(
    private val projectReportIdentification: ProjectReportIdentificationPersistence,
    private val generalValidator: GeneralValidatorService
): UpdateProjectReportIdentificationInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateProjectReportIdentificationException::class)
    override fun updateIdentification(
        projectId: Long,
        reportId: Long,
        identification: ProjectReportIdentificationUpdate
    ): ProjectReportIdentification {
        validateInputs(identification)
        return projectReportIdentification.updateReportIdentification(projectId, reportId, identification)
    }

    private fun validateInputs(data: ProjectReportIdentificationUpdate) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(data.highlights, 5000, "highlights"),
            generalValidator.maxLength(data.partnerProblems, 5000, "partnerProblems"),
            generalValidator.maxLength(data.deviations, 5000, "deviations"),
            *data.targetGroups.mapIndexed { index, it ->
                generalValidator.maxLength(it, 2000, "descriptionOfTheTargetGroup[$index]")
            }.toTypedArray(),
        )
    }
}
