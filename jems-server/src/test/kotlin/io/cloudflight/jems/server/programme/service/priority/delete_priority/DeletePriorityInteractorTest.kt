package io.cloudflight.jems.server.programme.service.priority.delete_priority

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.IndustrialTransition
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.testPriority
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpStatus

@ExtendWith(MockKExtension::class)
class DeletePriorityInteractorTest {

    @MockK
    lateinit var persistence: ProgrammePriorityPersistence

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var deletePriority: DeletePriority

    @Test
    fun `delete priority - OK`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getPriorityById(1L) } returns testPriority
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns setOf(IndustrialTransition)
        every { persistence.getObjectivePoliciesAlreadyUsedByResultIndicator() } returns emptyList()
        every { persistence.getObjectivePoliciesAlreadyUsedByOutputIndicator() } returns emptyList()
        every { persistence.delete(1L) } answers {}
        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        deletePriority.deletePriority(1L)
        verify { persistence.delete(1L) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROGRAMME_PRIORITY_DELETED,
                description = "Programme priority '${testPriority.code}' '${testPriority.title}' has been deleted"
            )
        )
    }

    @Test
    fun `delete priority failed - specific objective in use`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getPriorityById(1L) } returns testPriority
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns setOf(RenewableEnergy)

        val ex = assertThrows<ToDeletePriorityAlreadyUsedInCall> { deletePriority.deletePriority(1L) }
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(ex.i18nMessage).isEqualTo(I18nMessage("use.case.delete.programme.priority.already.used.in.call"))
    }

    @Test
    fun `delete priority failed - not existing`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getPriorityById(-1L) } throws ResourceNotFoundException("programmePriority")
        assertThrows<ResourceNotFoundException> { deletePriority.deletePriority(-1L) }
    }

    @Test
    fun `delete priority failed - programme setup already locked`() {
        every { isProgrammeSetupLocked.isLocked() } returns true
        assertThrows<DeletionWhenProgrammeSetupRestricted> { deletePriority.deletePriority(-2L) }
    }

    @Test
    fun `delete priority failed - used in result indicator`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getPriorityById(1L) } returns testPriority
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns emptyList()
        every { persistence.getObjectivePoliciesAlreadyUsedByResultIndicator() } returns setOf(RenewableEnergy)

        val ex = assertThrows<ToDeletePriorityAlreadyUsedInResultIndicator> { deletePriority.deletePriority(1L) }
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(ex.i18nMessage).isEqualTo(I18nMessage("use.case.delete.programme.priority.already.used.in.result.indicator"))
    }

    @Test
    fun `delete priority failed - used in output indicator`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getPriorityById(2L) } returns testPriority
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns emptyList()
        every { persistence.getObjectivePoliciesAlreadyUsedByResultIndicator() } returns emptyList()
        every { persistence.getObjectivePoliciesAlreadyUsedByOutputIndicator() } returns setOf(RenewableEnergy)

        val ex = assertThrows<ToDeletePriorityAlreadyUsedInOutputIndicator> { deletePriority.deletePriority(2L) }
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(ex.i18nMessage).isEqualTo(I18nMessage("use.case.delete.programme.priority.already.used.in.output.indicator"))
    }
}
