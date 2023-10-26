package io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.common.validator.GeneralValidatorDefaultImpl
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.ProjectAuditAndControlValidator
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class CloseProjectAuditControlTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 34L
        private const val AUDIT_CONTROL_ID = 45L
        private val PROJECT_SUMMARY = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "01",
            callId = 1L,
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED,
        )
    }

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistenceProvider

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectPersistenceProvider: ProjectPersistenceProvider

    @MockK
    lateinit var correctionPersistence: AuditControlCorrectionPersistence

    var generalValidator: GeneralValidatorService = GeneralValidatorDefaultImpl()

    @InjectMockKs
    lateinit var auditControlValidator: ProjectAuditAndControlValidator

    @OverrideMockKs
    lateinit var interactor: CloseProjectAuditControl

    @BeforeEach()
    fun setup() {
        clearMocks(auditControlPersistence, auditPublisher, projectPersistenceProvider)
    }

    @Test
    fun closeAuditControl() {
        every { auditControlPersistence.getByIdAndProjectId(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID) } returns
                mockk { every { status } returns AuditStatus.Ongoing }
        every { projectPersistenceProvider.getProjectSummary(projectId = PROJECT_ID) } returns PROJECT_SUMMARY
        every { correctionPersistence.getOngoingCorrectionsByAuditControlId(AUDIT_CONTROL_ID) } returns emptyList()
        every {
            auditControlPersistence.updateProjectAuditStatus(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID, auditStatus = AuditStatus.Closed)
        } returns mockk {
            every { status } returns AuditStatus.Closed
            every { projectCustomIdentifier } returns "ROHU00024"
            every { number } returns 5
        }
        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        assertThat(interactor.closeAuditControl(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID)).isEqualTo(AuditStatus.Closed)

        with(slotAudit.captured.auditCandidate) {
            assertThat(action).isEqualTo(AuditAction.PROJECT_AUDIT_CONTROL_IS_CLOSED)
            assertThat(description).isEqualTo("Audit/control ROHU00024_AC_5 is closed")
        }
    }

    @Test
    fun `closeAuditControl - status closed`() {
        every { correctionPersistence.getOngoingCorrectionsByAuditControlId(AUDIT_CONTROL_ID) } returns emptyList()
        every { auditControlPersistence.getByIdAndProjectId(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID) } returns mockk {
            every { status } returns AuditStatus.Closed
        }

        assertThrows<AuditControlNotOngoingException> { interactor.closeAuditControl(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID) }
    }

    @Test
    fun `closeAuditControl - corrections not closed`() {
        every { correctionPersistence.getOngoingCorrectionsByAuditControlId(AUDIT_CONTROL_ID) } returns listOf(mockk{})
        every { auditControlPersistence.getByIdAndProjectId(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID) } returns mockk {
            every { status } returns AuditStatus.Ongoing
        }


        assertThrows<CorrectionsStillOpenException> { interactor.closeAuditControl(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID) }
    }
}
