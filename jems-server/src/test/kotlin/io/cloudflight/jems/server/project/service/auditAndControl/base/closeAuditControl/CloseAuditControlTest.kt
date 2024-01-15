package io.cloudflight.jems.server.project.service.auditAndControl.base.closeAuditControl

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
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
        every { auditControlPersistence.getById(auditControlId = 9L) } returns
                mockk { every { status } returns AuditControlStatus.Ongoing }
        every { auditControlCorrectionPersistence.getOngoingCorrectionsByAuditControlId(9L) } returns emptyList()

        every {
            auditControlPersistence.updateAuditControlStatus(auditControlId = 9L, status = AuditControlStatus.Closed)
        } returns mockk {
            every { id } returns 9L
            every { status } returns AuditControlStatus.Closed
            every { projectId } returns 24L
            every { projectCustomIdentifier } returns "ROHU00024"
            every { projectAcronym } returns "24-Acr"
            every { number } returns 5
        }
        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } answers { }

        assertThat(interactor.closeAuditControl(auditControlId = 9L)).isEqualTo(AuditControlStatus.Closed)

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_AUDIT_CONTROL_IS_CLOSED,
                project = AuditProject("24", "ROHU00024", "24-Acr"),
                entityRelatedId = 9L,
                description = "Audit/Control ROHU00024_AC_5 is closed",
            )
        )
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
