package io.cloudflight.jems.server.project.service.application.set_assessment_eligibility

import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentEligibilityResult
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanSetEligibilityAssessment
import io.cloudflight.jems.server.project.service.ProjectAssessmentPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.eligibilityAssessmentStep1Concluded
import io.cloudflight.jems.server.project.service.eligibilityAssessmentStep2Concluded
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentEligibility
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SetAssessmentEligibility(
    private val projectPersistence: ProjectPersistence,
    private val projectAssessmentPersistence: ProjectAssessmentPersistence,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher,
    private val securityService: SecurityService,
) : SetAssessmentEligibilityInteractor {

    @CanSetEligibilityAssessment
    @Transactional
    @ExceptionWrapper(SetAssessmentEligibilityException::class)
    override fun setEligibilityAssessment(projectId: Long, result: ProjectAssessmentEligibilityResult, note: String?): Project {
        validateNote(note)
        val project = projectPersistence.getProjectSummary(projectId)

        if (project.isInStep2())
            processStep2(project, result, note)
        else
            processStep1(project, result, note)

        return projectPersistence.getProject(projectId)
    }

    private fun validateNote(note: String?) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(note, 1000, "note"),
        )

    private fun processStep2(project: ProjectSummary, result: ProjectAssessmentEligibilityResult, note: String? = null) {
        if (project.status != ApplicationStatus.SUBMITTED)
            throw AssessmentStep2CannotBeConcludedInThisStatus(project.status)
        if (projectAssessmentPersistence.eligibilityForStepExists(project.id, 2))
            throw AssessmentStep2AlreadyConcluded()

        projectAssessmentPersistence.setEligibility(
            userId = securityService.getUserIdOrThrow(),
            data = ProjectAssessmentEligibility(project.id, 2, result, note = note),
        )
        auditPublisher.publishEvent(eligibilityAssessmentStep2Concluded(this, project, result))
    }

    private fun processStep1(project: ProjectSummary, result: ProjectAssessmentEligibilityResult, note: String? = null) {
        if (project.status != ApplicationStatus.STEP1_SUBMITTED)
            throw AssessmentStep1CannotBeConcludedInThisStatus(project.status)
        if (projectAssessmentPersistence.eligibilityForStepExists(project.id, 1))
            throw AssessmentStep1AlreadyConcluded()

        projectAssessmentPersistence.setEligibility(
            userId = securityService.getUserIdOrThrow(),
            data = ProjectAssessmentEligibility(project.id, 1, result, note = note),
        )
        auditPublisher.publishEvent(eligibilityAssessmentStep1Concluded(this, project, result))
    }

}
