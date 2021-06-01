package io.cloudflight.jems.server.call.service.publish_call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

@ExtendWith(MockKExtension::class)
class PublishCallTest {

    companion object {
        private const val ID = 1L
        private val publishedCall = CallSummary(
            id = ID,
            name = "name of just-now-published call",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1),
            endDateStep1 = null
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var publishCall: PublishCall

    @Test
    fun publishCall() {
        every { persistence.publishCall(ID) } returns publishedCall
        assertThat(publishCall.publishCall(ID)).isEqualTo(publishedCall)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.CALL_PUBLISHED,
            entityRelatedId = ID,
            description = "Call id=$ID 'name of just-now-published call' published"
        ))
    }

    @Test
    fun `publishCall - do not log audit message in case of exception`() {
        every { persistence.publishCall(ID) } throws RuntimeException("whatever unexpected exception")
        assertThrows<RuntimeException> { publishCall.publishCall(ID) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

}
