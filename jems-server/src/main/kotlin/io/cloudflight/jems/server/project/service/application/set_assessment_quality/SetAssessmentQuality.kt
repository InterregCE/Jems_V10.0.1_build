package io.cloudflight.jems.server.project.service.application.set_assessment_quality

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanSetQualityAssessment
import io.cloudflight.jems.server.project.service.ProjectAssessmentPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.getProjectWithoutFormData
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.project.service.qualityAssessmentStep1Concluded
import io.cloudflight.jems.server.project.service.qualityAssessmentStep2Concluded
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetAssessmentQuality(
    private val projectPersistence: ProjectPersistence,
    private val projectAssessmentPersistence: ProjectAssessmentPersistence,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService,
) : SetAssessmentQualityInteractor {

    @CanSetQualityAssessment
    @Transactional
    @ExceptionWrapper(SetAssessmentQualityException::class)
    override fun setQualityAssessment(projectId: Long, result: ProjectAssessmentQualityResult, note: String?): ProjectDetail {
        validateNote(note)
        val project = projectPersistence.getProjectSummary(projectId)

        if (project.isInStep2())
            processStep2(project, result, note)
        else
            processStep1(project, result, note)

        return projectPersistence.getProject(projectId).getProjectWithoutFormData()
    }

    private fun validateNote(note: String?) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(note, 1000, "note"),
        )

    private fun processStep2(project: ProjectSummary, result: ProjectAssessmentQualityResult, note: String? = null) {
        val status = project.status
        if (status != ApplicationStatus.SUBMITTED && status != ApplicationStatus.ELIGIBLE)
            throw AssessmentStep2CannotBeConcludedInThisStatus(status)

        if (projectAssessmentPersistence.qualityForStepExists(project.id, 2))
            throw AssessmentStep2AlreadyConcluded()

        projectAssessmentPersistence.setQuality(
            userId = securityService.getUserIdOrThrow(),
            data = ProjectAssessmentQuality(project.id, 2, result, note = note),
        )
        auditPublisher.publishEvent(qualityAssessmentStep2Concluded(this, project, result))
    }

    private fun processStep1(project: ProjectSummary, result: ProjectAssessmentQualityResult, note: String? = null) {
        val status = project.status
        if (status != ApplicationStatus.STEP1_SUBMITTED && status != ApplicationStatus.STEP1_ELIGIBLE)
            throw AssessmentStep1CannotBeConcludedInThisStatus(status)

        if (projectAssessmentPersistence.qualityForStepExists(project.id, 1))
            throw AssessmentStep1AlreadyConcluded()

        projectAssessmentPersistence.setQuality(
            userId = securityService.getUserIdOrThrow(),
            data = ProjectAssessmentQuality(project.id, 1, result, note = note),
        )
        auditPublisher.publishEvent(qualityAssessmentStep1Concluded(this, project, result))
    }


}
