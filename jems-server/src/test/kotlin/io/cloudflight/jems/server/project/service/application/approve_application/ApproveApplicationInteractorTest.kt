package io.cloudflight.jems.server.project.service.application.approve_application

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult.RECOMMENDED_FOR_FUNDING
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_ELIGIBLE
import io.cloudflight.jems.server.project.service.application.projectWithId
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.EligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepEligibleApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate

class ApproveApplicationInteractorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private fun summary(status: ApplicationStatus) = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = status,
        )
        private val actionInfo = ApplicationActionInfo(
            note = "make approval",
            date = LocalDate.of(2021, 4, 13),
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var generalValidatorService: GeneralValidatorService

    @InjectMockKs
    private lateinit var approveApplication: ApproveApplication

    @MockK
    lateinit var eligibleState: EligibleApplicationState

    @MockK
    lateinit var eligibleStateStep1: FirstStepEligibleApplicationState

    @BeforeEach
    fun clearMocks() {
        clearMocks(auditPublisher)
    }

    @Test
    fun `approve when in STEP2`() {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID, status = ELIGIBLE).copy(
            assessmentStep2 = ProjectAssessment(assessmentQuality = ProjectAssessmentQuality(PROJECT_ID, 2, RECOMMENDED_FOR_FUNDING))
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary(ELIGIBLE)
        every { applicationStateFactory.getInstance(any()) } returns eligibleState
        every { eligibleState.approve(actionInfo) } returns APPROVED

        assertThat(approveApplication.approve(PROJECT_ID, actionInfo)).isEqualTo(APPROVED)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from ELIGIBLE to APPROVED"
            )
        )
    }

    @Test
    fun `approve when in STEP1`() {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID, status = STEP1_ELIGIBLE).copy(
            assessmentStep1 = ProjectAssessment(assessmentQuality = ProjectAssessmentQuality(PROJECT_ID, 1, RECOMMENDED_FOR_FUNDING))
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary(STEP1_ELIGIBLE)
        every { applicationStateFactory.getInstance(any()) } returns eligibleStateStep1
        every { eligibleStateStep1.approve(actionInfo) } returns STEP1_APPROVED

        assertThat(approveApplication.approve(PROJECT_ID, actionInfo)).isEqualTo(STEP1_APPROVED)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from STEP1_ELIGIBLE to STEP1_APPROVED"
            )
        )
    }

    @ParameterizedTest(name = "assessment null when in status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "STEP1_ELIGIBLE"])
    fun `quality assessment null`(status: ApplicationStatus) {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID, status = status)
        assertThrows<QualityAssessmentMissing> { approveApplication.approve(PROJECT_ID, actionInfo) }
    }

    @ParameterizedTest(name = "missing quality assessment when in status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "STEP1_ELIGIBLE"])
    fun `quality assessment empty`(status: ApplicationStatus) {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID, status = status).copy(
            assessmentStep1 = ProjectAssessment(assessmentQuality = null),
            assessmentStep2 = ProjectAssessment(assessmentQuality = null),
        )
        assertThrows<QualityAssessmentMissing> { approveApplication.approve(PROJECT_ID, actionInfo) }
    }

}
