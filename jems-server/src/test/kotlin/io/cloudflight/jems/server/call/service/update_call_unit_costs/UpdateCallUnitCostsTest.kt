package io.cloudflight.jems.server.call.service.update_call_unit_costs

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

class UpdateCallUnitCostsTest: UnitTest() {

    companion object {
        private fun callWithStatus(id: Long, status: CallStatus) = CallDetail(
            id = id,
            name = "",
            status = status,
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 7,
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @RelaxedMockK
    lateinit var auditService: AuditService

    @InjectMockKs
    private lateinit var updateCallUnitCosts: UpdateCallUnitCosts

    @Test
    fun `updateUnitCosts - change of unit costs when call is PUBLISHED`() {
        val ID = 5L
        val call = callWithStatus(id = ID, CallStatus.PUBLISHED)
        every { persistence.existsAllProgrammeUnitCostsByIds(setOf(4, 5)) } returns true
        every { persistence.updateProjectCallUnitCost(ID, setOf(4, 5)) } returns call
        every { persistence.getCallById(ID) } returns call
        updateCallUnitCosts.updateUnitCosts(ID, setOf(4, 5))

        verify { persistence.updateProjectCallUnitCost(ID, setOf(4, 5)) }
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
    }

    @Test
    fun `updateUnitCosts - missing not existing programme unit cost`() {
        val ID = 8L
        every { persistence.existsAllProgrammeUnitCostsByIds(setOf(4, 5)) } returns false
        assertThrows<UnitCostNotFound> { updateCallUnitCosts.updateUnitCosts(ID, setOf(4, 5)) }
    }

}
