package io.cloudflight.jems.server.project.service.application.set_assessment_quality

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult.RECOMMENDED_FOR_FUNDING
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.service.ProjectAssessmentPersistence
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.projectWithId
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher

class SetAssessmentQualityInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 335L
        private fun projectWithStatus(status: ApplicationStatus): ProjectSummary {
            val project = projectWithId(PROJECT_ID, status)
            return ProjectSummary(
                id = PROJECT_ID,
                callName = project.callSettings.callName,
                acronym = project.acronym,
                status = status,
            )
        }
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var projectAssessmentPersistence: ProjectAssessmentPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var setAssessment: SetAssessmentQuality

    @BeforeEach
    fun clearMocks() {
        clearMocks(auditPublisher)
    }

    @ParameterizedTest(name = "can set quality assessment in STEP2 status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["SUBMITTED", "ELIGIBLE"])
    fun `setQualityAssessment - step2 - everything OK`(status: ApplicationStatus) {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectWithStatus(status = status)
        every { projectAssessmentPersistence.qualityForStepExists(PROJECT_ID, 2) } returns false
        every { securityService.getUserIdOrThrow() } returns applicantUser.user.id
        val slotAssessment = slot<ProjectAssessmentQuality>()
        every { projectAssessmentPersistence.setQuality(applicantUser.user.id, capture(slotAssessment)) } answers {}
        every { projectPersistence.getProject(PROJECT_ID, null) } returns projectWithId(PROJECT_ID, status)

        setAssessment.setQualityAssessment(PROJECT_ID, RECOMMENDED_FOR_FUNDING, "note")

        assertThat(slotAssessment.captured).isEqualTo(
            ProjectAssessmentQuality(PROJECT_ID, 2, RECOMMENDED_FOR_FUNDING, note = "note")
        )

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.QUALITY_ASSESSMENT_CONCLUDED,
                project = AuditProject(id = PROJECT_ID.toString(), name = "project acronym"),
                description = "Project application quality assessment concluded as RECOMMENDED_FOR_FUNDING"
            )
        )
    }

    @ParameterizedTest(name = "can set quality assessment in STEP1 status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["STEP1_SUBMITTED", "STEP1_ELIGIBLE"])
    fun `setQualityAssessment - step1 - everything OK`(status: ApplicationStatus) {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectWithStatus(status = status)
        every { projectAssessmentPersistence.qualityForStepExists(PROJECT_ID, 1) } returns false
        every { securityService.getUserIdOrThrow() } returns applicantUser.user.id
        val slotAssessment = slot<ProjectAssessmentQuality>()
        every { projectAssessmentPersistence.setQuality(applicantUser.user.id, capture(slotAssessment)) } answers {}
        every { projectPersistence.getProject(PROJECT_ID, null) } returns projectWithId(PROJECT_ID, status)

        setAssessment.setQualityAssessment(PROJECT_ID, RECOMMENDED_FOR_FUNDING, "note")

        assertThat(slotAssessment.captured).isEqualTo(
            ProjectAssessmentQuality(PROJECT_ID, 1, RECOMMENDED_FOR_FUNDING, note = "note")
        )

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.QUALITY_ASSESSMENT_CONCLUDED,
                project = AuditProject(id = PROJECT_ID.toString(), name = "project acronym"),
                description = "Project application quality assessment (step 1) concluded as RECOMMENDED_FOR_FUNDING"
            )
        )
    }

    @ParameterizedTest(name = "cannot set quality assessment because already exists (status {0})")
    @EnumSource(value = ApplicationStatus::class, names = ["SUBMITTED", "ELIGIBLE"])
    fun `setQualityAssessment - step2 - already exists`(status: ApplicationStatus) {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectWithStatus(status = status)
        every { projectAssessmentPersistence.qualityForStepExists(PROJECT_ID, 2) } returns true

        assertThrows<AssessmentStep2AlreadyConcluded> {
            setAssessment.setQualityAssessment(PROJECT_ID, RECOMMENDED_FOR_FUNDING)
        }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @ParameterizedTest(name = "cannot set quality assessment because already exists STEP1 (status {0})")
    @EnumSource(value = ApplicationStatus::class, names = ["STEP1_SUBMITTED", "STEP1_ELIGIBLE"])
    fun `setQualityAssessment - step1 - already exists`(status: ApplicationStatus) {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectWithStatus(status = status)
        every { projectAssessmentPersistence.qualityForStepExists(PROJECT_ID, 1) } returns true

        assertThrows<AssessmentStep1AlreadyConcluded> {
            setAssessment.setQualityAssessment(PROJECT_ID, RECOMMENDED_FOR_FUNDING)
        }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @ParameterizedTest(name = "cannot set quality assessment STEP2 because status is {0})")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "RETURNED_TO_APPLICANT", "INELIGIBLE", "APPROVED", "APPROVED_WITH_CONDITIONS", "NOT_APPROVED"])
    fun `setQualityAssessment - step2 - wrong status`(status: ApplicationStatus) {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectWithStatus(status = status)

        assertThrows<AssessmentStep2CannotBeConcludedInThisStatus> {
            setAssessment.setQualityAssessment(PROJECT_ID, RECOMMENDED_FOR_FUNDING)
        }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @ParameterizedTest(name = "cannot set quality assessment STEP1 because status is {0})")
    @EnumSource(value = ApplicationStatus::class, names = ["STEP1_DRAFT", "STEP1_INELIGIBLE", "STEP1_APPROVED", "STEP1_APPROVED_WITH_CONDITIONS", "STEP1_NOT_APPROVED"])
    fun `setQualityAssessment - step1 - wrong status`(status: ApplicationStatus) {
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectWithStatus(status = status)

        assertThrows<AssessmentStep1CannotBeConcludedInThisStatus> {
            setAssessment.setQualityAssessment(PROJECT_ID, RECOMMENDED_FOR_FUNDING)
        }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

}
