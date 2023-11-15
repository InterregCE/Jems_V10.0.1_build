package io.cloudflight.jems.server.project.service.auditAndControl.reopenAuditControl

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.repository.auditAndControl.AuditControlPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.base.reopenAuditControl.AuditControlNotClosedException
import io.cloudflight.jems.server.project.service.auditAndControl.base.reopenAuditControl.ReopenAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class ReopenAuditControlTest: UnitTest() {

    companion object {
        private const val AUDIT_CONTROL_ID = 45L
    }

    @MockK
    lateinit var auditControlPersistence: AuditControlPersistenceProvider

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var reopenAuditControl: ReopenAuditControl

    @Test
    fun `reopen audit control`() {
        every { auditControlPersistence.getById(auditControlId = AUDIT_CONTROL_ID) } returns
            mockk { every { status } returns AuditControlStatus.Closed }

        every {
            auditControlPersistence.updateAuditControlStatus(AUDIT_CONTROL_ID, status = AuditControlStatus.Ongoing)
        } returns mockk {
            every { id } returns AUDIT_CONTROL_ID
            every { status } returns AuditControlStatus.Ongoing
            every { projectId } returns 24L
            every { projectCustomIdentifier } returns "ROHU00024"
            every { projectAcronym } returns "Acr-24"
            every { number } returns 5
        }

        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        assertThat(reopenAuditControl.reopenAuditControl(auditControlId = AUDIT_CONTROL_ID)).isEqualTo(AuditControlStatus.Ongoing)

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PROJECT_AUDIT_CONTROL_IS_REOPENED,
            project = AuditProject("24", "ROHU00024", "Acr-24"),
            entityRelatedId = AUDIT_CONTROL_ID,
            description = "Audit/Control ROHU00024_AC_5 is set back to draft",
        ))
    }

    @Test
    fun `reopen audit control - status ongoing`() {
        every { auditControlPersistence.getById(auditControlId = -1L) } returns
            mockk { every { status } returns AuditControlStatus.Ongoing }

        assertThrows<AuditControlNotClosedException> {
            reopenAuditControl.reopenAuditControl(auditControlId = -1L)
        }
    }

}
