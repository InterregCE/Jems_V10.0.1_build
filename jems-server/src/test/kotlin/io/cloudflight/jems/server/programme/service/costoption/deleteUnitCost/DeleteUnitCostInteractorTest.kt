package io.cloudflight.jems.server.programme.service.costoption.deleteUnitCost

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeUnitCostPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

class DeleteUnitCostInteractorTest {

    companion object {
        private val unitCost = ProgrammeUnitCost(
            id = 1L,
            projectId = 2L,
            name = setOf(InputTranslation(SystemLanguage.EN, "UnitCost")),
            isOneCostCategory = true
        )
    }

    @MockK
    lateinit var persistence: ProgrammeUnitCostPersistence

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    private lateinit var deleteUnitCostInteractor: DeleteUnitCostInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        deleteUnitCostInteractor = DeleteUnitCost(persistence, isProgrammeSetupLocked, auditPublisher)
    }

    @Test
    fun `delete unit cost - OK`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getNumberOfOccurrencesInCalls(1L) } returns 0
        every { persistence.getUnitCost(1L) } returns unitCost
        every { persistence.deleteUnitCost(1L) } answers {}
        val slotAudit = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slotAudit)) } returns Unit

        deleteUnitCostInteractor.deleteUnitCost(1L)
        verify { persistence.deleteUnitCost(1L) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROGRAMME_UNIT_COST_DELETED,
                description = "Programme unit cost (id=${unitCost.id}) '${unitCost.name}' has been deleted"
            )
        )
    }

    @Test
    fun `delete unit cost - already in use for call`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getNumberOfOccurrencesInCalls(1L) } returns 1
        every { persistence.deleteUnitCost(1L) } answers {}

        assertThrows<ToDeleteUnitCostAlreadyUsedInCall> { deleteUnitCostInteractor.deleteUnitCost(1L) }
    }

    @Test
    fun `delete unit cost - not existing`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getNumberOfOccurrencesInCalls(-1L) } returns 0
        every { persistence.getUnitCost(-1L) } throws ResourceNotFoundException("programmeUnitCost")

        assertThrows<ResourceNotFoundException> { deleteUnitCostInteractor.deleteUnitCost(-1L) }
    }

    @Test
    fun `delete unit cost - call already published`() {
        every { persistence.deleteUnitCost(1L) } throws ResourceNotFoundException("programmeUnitCost")
        every { isProgrammeSetupLocked.isLocked() } returns true
        assertThrows<DeleteUnitCostWhenProgrammeSetupRestricted> { deleteUnitCostInteractor.deleteUnitCost(1L) }
    }

}
