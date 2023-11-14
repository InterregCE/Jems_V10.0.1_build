package io.cloudflight.jems.server.project.service.auditAndControl.base.closeAuditControl

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class CloseAuditControlTest : UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 45L
    }

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistence

    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @MockK
    private lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var interactor: CloseAuditControl

    @BeforeEach
    fun setup() {
        clearMocks(auditControlPersistence, auditControlCorrectionPersistence, auditPublisher)
    }

    @Test
    fun closeAuditControl() {
        /*
        every { auditControlPersistence.getById(auditControlId = AUDIT_CONTROL_ID) } returns
                mockk { every { status } returns AuditControlStatus.Ongoing }
        every { projectPersistenceProvider.getProjectSummary(projectId = PROJECT_ID) } returns PROJECT_SUMMARY
        every { correctionPersistence.getOngoingCorrectionsByAuditControlId(AUDIT_CONTROL_ID) } returns emptyList()
        every {
            auditControlPersistence.updateProjectAuditStatus(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID, auditStatus = AuditControlStatus.Closed)
        } returns mockk {
            every { status } returns AuditControlStatus.Closed
            every { projectCustomIdentifier } returns "ROHU00024"
            every { number } returns 5
        }
        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        assertThat(interactor.closeAuditControl(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID)).isEqualTo(AuditControlStatus.Closed)

        with(slotAudit.captured.auditCandidate) {
            assertThat(action).isEqualTo(AuditAction.PROJECT_AUDIT_CONTROL_IS_CLOSED)
            assertThat(description).isEqualTo("Audit/control ROHU00024_AC_5 is closed")
        }
        */
    }

    @Test
    fun `closeAuditControl - status closed`() {
        every { auditControlPersistence.getById(auditControlId = 7L) } returns
                mockk { every { status } returns AuditControlStatus.Closed }

        assertThrows<AuditControlClosedException> { interactor.closeAuditControl(auditControlId = 7L) }
    }

    @Test
    fun `closeAuditControl - corrections not closed`() {
        every { auditControlPersistence.getById(auditControlId = AUDIT_CONTROL_ID) } returns
                mockk { every { status } returns AuditControlStatus.Ongoing }
        every { auditControlCorrectionPersistence.getOngoingCorrectionsByAuditControlId(AUDIT_CONTROL_ID) } returns
                listOf(mockk())

        assertThrows<CorrectionsStillOpenException> { interactor.closeAuditControl(auditControlId = AUDIT_CONTROL_ID) }
    }

}
