package io.cloudflight.jems.server.call.service.publish_call

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.callFundRate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class PublishCallTest : UnitTest() {


    private val id = 1L
    private val publishedCall = CallSummary(
        id = id,
        name = "name of just-now-published call",
        status = CallStatus.DRAFT,
        startDate = ZonedDateTime.now().minusDays(1),
        endDate = ZonedDateTime.now().plusDays(1),
        endDateStep1 = null
    )
    private val callDetail = CallDetail(
        id = id,
        name = "name of just-now-published call",
        status = CallStatus.DRAFT,
        type= CallType.STANDARD,
        startDate = ZonedDateTime.now().minusDays(1),
        endDate = ZonedDateTime.now().plusDays(1),
        endDateStep1 = null,
        isAdditionalFundAllowed = true,
        lengthOfPeriod = 12,
        applicationFormFieldConfigurations = mutableSetOf(),
        strategies = sortedSetOf(ProgrammeStrategy.AtlanticStrategy),
        funds = sortedSetOf(callFundRate(1)),
        objectives = listOf(ProgrammePriority(code = "code", objective = ProgrammeObjective.ISO12)),
        preSubmissionCheckPluginKey = null
    )

    @MockK
    lateinit var persistence: CallPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var publishCall: PublishCall

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher)
    }

    @Test
    fun publishCall() {
        every { persistence.getCallById(id) } returns callDetail.copy(preSubmissionCheckPluginKey = "jems-pre-condition-check-off")
        every { persistence.publishCall(id) } returns publishedCall
        assertThat(publishCall.publishCall(id)).isEqualTo(publishedCall)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CALL_PUBLISHED,
                entityRelatedId = id,
                description = "Call id=$id 'name of just-now-published call' published"
            )
        )
    }

    @Test
    fun `publishCall - do not log audit message in case of exception`() {
        every { persistence.getCallById(id) } returns callDetail
        every { persistence.publishCall(id) } throws RuntimeException("whatever unexpected exception")
        assertThrows<RuntimeException> { publishCall.publishCall(id) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @TestFactory
    fun `should throw CannotPublishCallException when funds or strategies or programmePriorities or pre-submission check plugin key are empty`() =
        listOf(
            Pair("funds", callDetail.copy(funds = sortedSetOf())),
            Pair("programmePriorities", callDetail.copy(objectives = listOf())),
            Pair("pre-submissionCheckPlugin", callDetail.copy(preSubmissionCheckPluginKey = null)),
            Pair("pre-submissionCheckPlugin", callDetail.copy(preSubmissionCheckPluginKey = "")),
        ).map { input ->
            DynamicTest.dynamicTest(
                "should throw CannotPublishCallException when ${input.first} are empty"
            ) {
                every { persistence.getCallById(id) } returns input.second
                assertThrows<CannotPublishCallException> { publishCall.publishCall(id) }
            }
        }
}
