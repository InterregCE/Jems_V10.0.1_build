package io.cloudflight.jems.server.project.service.application.refuse_application

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.assessment.ProjectAssessmentQualityResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.ProjectAuthorization
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.NOT_APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_NOT_APPROVED
import io.cloudflight.jems.server.project.service.application.approve_application.ApproveApplicationInteractorTest
import io.cloudflight.jems.server.project.service.application.projectWithId
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.EligibleApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.first_step.FirstStepEligibleApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectAssessment
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.assessment.ProjectAssessmentQuality
import io.cloudflight.jems.server.user.service.model.UserRolePermission
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

class RefuseApplicationInteractorTest : UnitTest() {

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
            note = "refusing application",
            date = LocalDate.of(2021, 4, 13),
            entryIntoForceDate = LocalDate.of(2021, 4, 13)
        )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @RelaxedMockK
    lateinit var generalValidatorService: GeneralValidatorService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var projectAuthorization: ProjectAuthorization

    @InjectMockKs
    private lateinit var refuseApplication: RefuseApplication

    @MockK
    lateinit var eligibleState: EligibleApplicationState

    @MockK
    lateinit var eligibleStateStep1: FirstStepEligibleApplicationState

    @BeforeEach
    fun clearMocks() {
        clearMocks(auditPublisher)
    }

    @Test
    fun `refuse application when in STEP2`() {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID, status = ELIGIBLE).copy(
            assessmentStep2 = ProjectAssessment(assessmentQuality = ProjectAssessmentQuality(PROJECT_ID, 2, ProjectAssessmentQualityResult.RECOMMENDED_FOR_FUNDING))
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary(ELIGIBLE)
        every { applicationStateFactory.getInstance(any()) } returns eligibleState
        every { eligibleState.refuse(actionInfo) } returns NOT_APPROVED

        assertThat(refuseApplication.refuse(PROJECT_ID, actionInfo)).isEqualTo(NOT_APPROVED)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from ELIGIBLE to NOT_APPROVED"
            )
        )
    }

    @Test
    fun `refuse application when in STEP1`() {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID, status = STEP1_ELIGIBLE).copy(
            assessmentStep1 = ProjectAssessment(assessmentQuality = ProjectAssessmentQuality(PROJECT_ID, 1, ProjectAssessmentQualityResult.RECOMMENDED_FOR_FUNDING))
        )
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns summary(STEP1_ELIGIBLE)
        every { applicationStateFactory.getInstance(any()) } returns eligibleStateStep1
        every { eligibleStateStep1.refuse(actionInfo) } returns STEP1_NOT_APPROVED

        assertThat(refuseApplication.refuse(PROJECT_ID, actionInfo)).isEqualTo(STEP1_NOT_APPROVED)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = PROJECT_ID.toString(), customIdentifier = "01", name = "project acronym"),
                description = "Project application status changed from STEP1_ELIGIBLE to STEP1_NOT_APPROVED"
            )
        )
    }

    @ParameterizedTest(name = "assessment null when in status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "STEP1_ELIGIBLE"])
    fun `quality assessment null`(status: ApplicationStatus) {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID, status = status)
        assertThrows<QualityAssessmentMissing> { refuseApplication.refuse(PROJECT_ID, actionInfo) }
    }

    @ParameterizedTest(name = "missing quality assessment when in status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["ELIGIBLE", "STEP1_ELIGIBLE"])
    fun `quality assessment empty`(status: ApplicationStatus) {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID, status = status).copy(
            assessmentStep1 = ProjectAssessment(assessmentQuality = null),
            assessmentStep2 = ProjectAssessment(assessmentQuality = null),
        )
        assertThrows<QualityAssessmentMissing> { refuseApplication.refuse(PROJECT_ID, actionInfo) }
    }

    @Test
    fun `refuse when submitted precontracted checks modification permission`() {
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(
            PROJECT_ID,
            status = ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED)
            .copy(
                assessmentStep2 = ProjectAssessment(assessmentQuality = ProjectAssessmentQuality(
                    PROJECT_ID, 2,
                    ProjectAssessmentQualityResult.RECOMMENDED_FOR_FUNDING
                ))
            )

        refuseApplication.refuse(PROJECT_ID, actionInfo)

        verify(exactly = 1) { projectAuthorization.hasPermission(UserRolePermission.ProjectStatusDecideModificationNotApproved, PROJECT_ID) }
    }

}
