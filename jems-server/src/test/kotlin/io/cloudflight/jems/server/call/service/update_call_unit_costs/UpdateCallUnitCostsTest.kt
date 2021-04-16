package io.cloudflight.jems.server.call.service.update_call_unit_costs

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
class UpdateCallUnitCostsTest {

    companion object {
        private fun callWithStatus(id: Long, status: CallStatus) = CallDetail(
            id = id,
            name = "",
            status = status,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 7,
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var updateCallUnitCosts: UpdateCallUnitCosts

    @Test
    fun `updateUnitCosts - change of unit costs when call is PUBLISHED`() {
        val ID = 5L
        val call = callWithStatus(id = ID, CallStatus.PUBLISHED)
        every { persistence.existsAllProgrammeUnitCostsByIds(setOf(4, 5)) } returns true
        every { persistence.updateProjectCallUnitCost(ID, setOf(4, 5)) } returns call.copy(
            unitCosts = listOf(
                ProgrammeUnitCost(id = 4, isOneCostCategory = true),
                ProgrammeUnitCost(id = 5, isOneCostCategory = false),
            )
        )
        every { persistence.getCallById(ID) } returns call
        updateCallUnitCosts.updateUnitCosts(ID, setOf(4, 5))

        verify { persistence.updateProjectCallUnitCost(ID, setOf(4, 5)) }

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        Assertions.assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CALL_CONFIGURATION_CHANGED,
                entityRelatedId = ID,
                description = "Configuration of published call id=$ID name='' changed:\n" +
                    "unitCostIds changed from [] to [4, 5]"
            )
        )
    }

    @Test
    fun `updateUnitCosts - change of unit costs when call is DRAFT`() {
        val ID = 6L
        val call = callWithStatus(id = ID, CallStatus.DRAFT)
        every { persistence.existsAllProgrammeUnitCostsByIds(setOf(5)) } returns true
        every { persistence.updateProjectCallUnitCost(ID, setOf(5)) } returns call
        every { persistence.getCallById(ID) } returns call
        updateCallUnitCosts.updateUnitCosts(ID, setOf(5))

        verify { persistence.updateProjectCallUnitCost(ID, setOf(5)) }
    }

    @Test
    fun `updateUnitCosts - forbidden change when call is published`() {
        val ID = 7L
        every { persistence.existsAllProgrammeUnitCostsByIds(setOf(5)) } returns true
        every { persistence.getCallById(ID) } returns callWithStatus(id = ID, CallStatus.PUBLISHED).copy(
            unitCosts = listOf(
                ProgrammeUnitCost(id = 4, isOneCostCategory = true),
                ProgrammeUnitCost(id = 5, isOneCostCategory = false),
            )
        )
        assertThrows<UnitCostsRemovedAfterCallPublished> { updateCallUnitCosts.updateUnitCosts(ID, setOf(5)) }

        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
        confirmVerified(auditPublisher)
    }

    @Test
    fun `updateUnitCosts - missing not existing programme unit cost`() {
        val ID = 8L
        every { persistence.existsAllProgrammeUnitCostsByIds(setOf(4, 5)) } returns false
        assertThrows<UnitCostNotFound> { updateCallUnitCosts.updateUnitCosts(ID, setOf(4, 5)) }

        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
        confirmVerified(auditPublisher)
    }

}
