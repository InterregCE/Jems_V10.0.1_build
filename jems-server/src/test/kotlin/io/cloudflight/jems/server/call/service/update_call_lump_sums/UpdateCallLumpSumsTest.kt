package io.cloudflight.jems.server.call.service.update_call_lump_sums

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

class UpdateCallLumpSumsTest: UnitTest() {

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
    private lateinit var updateCallLumpSums: UpdateCallLumpSums

    @Test
    fun `updateLumpSums - change of lump sums when call is PUBLISHED`() {
        val ID = 1L
        val call = callWithStatus(id = ID, CallStatus.PUBLISHED)
        every { persistence.existsAllProgrammeLumpSumsByIds(setOf(2, 3)) } returns true
        every { persistence.updateProjectCallLumpSum(ID, setOf(2, 3)) } returns call
        every { persistence.getCallById(ID) } returns call
        updateCallLumpSums.updateLumpSums(ID, setOf(2, 3))

        verify { persistence.updateProjectCallLumpSum(ID, setOf(2, 3)) }
    }

    @Test
    fun `updateLumpSums - change of lump sums when call is DRAFT`() {
        val ID = 2L
        val call = callWithStatus(id = ID, CallStatus.DRAFT)
        every { persistence.existsAllProgrammeLumpSumsByIds(setOf(3)) } returns true
        every { persistence.updateProjectCallLumpSum(ID, setOf(3)) } returns call
        every { persistence.getCallById(ID) } returns call
        updateCallLumpSums.updateLumpSums(ID, setOf(3))

        verify { persistence.updateProjectCallLumpSum(ID, setOf(3)) }
    }

    @Test
    fun `updateLumpSums - forbidden change when call is published`() {
        val ID = 3L
        every { persistence.existsAllProgrammeLumpSumsByIds(setOf(3)) } returns true
        every { persistence.getCallById(ID) } returns callWithStatus(id = ID, CallStatus.PUBLISHED).copy(
            lumpSums = listOf(
                ProgrammeLumpSum(id = 2, splittingAllowed = true),
                ProgrammeLumpSum(id = 3, splittingAllowed = true),
            )
        )
        assertThrows<LumpSumsRemovedAfterCallPublished> { updateCallLumpSums.updateLumpSums(ID, setOf(3)) }
    }

    @Test
    fun `updateLumpSums - missing not existing programme lump sum`() {
        val ID = 4L
        every { persistence.existsAllProgrammeLumpSumsByIds(setOf(2, 3)) } returns false
        assertThrows<LumpSumNotFound> { updateCallLumpSums.updateLumpSums(ID, setOf(2, 3)) }
    }

}
