package io.cloudflight.jems.server.call.service.update_call_lump_sums

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
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
class UpdateCallLumpSumsTest {

    companion object {
        private fun callWithStatus(id: Long, status: CallStatus, callType: CallType) = CallDetail(
            id = id,
            name = "",
            status = status,
            type = callType,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 7,
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var updateCallLumpSums: UpdateCallLumpSums

    @Test
    fun `updateLumpSums - change of lump sums when call is PUBLISHED`() {
        val ID = 1L
        val call = callWithStatus(id = ID, CallStatus.PUBLISHED, CallType.STANDARD)
        every { persistence.existsAllProgrammeLumpSumsByIds(setOf(2, 3)) } returns true
        every { persistence.updateProjectCallLumpSum(ID, setOf(2, 3)) } returns call.copy(
            lumpSums = listOf(
                ProgrammeLumpSum(
                    id = 2,
                    splittingAllowed = true,
                    isFastTrack = false
                ),
                ProgrammeLumpSum(
                    id = 3,
                    splittingAllowed = true,
                    isFastTrack = false
                )))
        every { persistence.getCallById(ID) } returns call
        updateCallLumpSums.updateLumpSums(ID, setOf(2, 3))

        verify { persistence.updateProjectCallLumpSum(ID, setOf(2, 3)) }

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        Assertions.assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CALL_CONFIGURATION_CHANGED,
                entityRelatedId = ID,
                description = "Configuration of published call id=1 name='' changed:\n" +
                    "lumpSumIds changed from [] to [2, 3]"
            )
        )
    }

    @Test
    fun `updateLumpSums - change of lump sums when call is DRAFT`() {
        val ID = 2L
        val call = callWithStatus(id = ID, CallStatus.DRAFT, CallType.STANDARD)
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
        every { persistence.getCallById(ID) } returns callWithStatus(id = ID, CallStatus.PUBLISHED, CallType.STANDARD).copy(
            lumpSums = listOf(
                ProgrammeLumpSum(id = 2, splittingAllowed = true, isFastTrack = false),
                ProgrammeLumpSum(id = 3, splittingAllowed = true, isFastTrack = false),
            )
        )
        assertThrows<LumpSumsRemovedAfterCallPublished> { updateCallLumpSums.updateLumpSums(ID, setOf(3)) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
        confirmVerified(auditPublisher)
    }

    @Test
    fun `updateLumpSums - missing not existing programme lump sum`() {
        val ID = 4L
        every { persistence.existsAllProgrammeLumpSumsByIds(setOf(2, 3)) } returns false
        assertThrows<LumpSumNotFound> { updateCallLumpSums.updateLumpSums(ID, setOf(2, 3)) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
        confirmVerified(auditPublisher)
    }

}
