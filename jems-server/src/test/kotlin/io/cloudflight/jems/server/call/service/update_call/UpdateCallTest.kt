package io.cloudflight.jems.server.call.service.update_call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Digitisation
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Growth
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.AtlanticStrategy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.EUStrategyBalticSeaRegion
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.MediterraneanSeaBasin
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.validator.CallValidator
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime
import java.util.stream.Stream

class UpdateCallTest: UnitTest() {

    companion object {
        private const val CALL_ID = 592L
        private const val FUND_ID = 5L

        private val existingCall = CallDetail(
            id = CALL_ID,
            name = "call name",
            status = CallStatus.PUBLISHED,
            startDate = ZonedDateTime.now().plusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(2),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = SystemLanguage.EN, translation = "EN desc"),
                InputTranslation(language = SystemLanguage.SK, translation = "SK desc"),
            ),
            objectives = listOf(
                ProgrammePriority(
                    code = "PRIO_CODE", objective = PO1, specificObjectives = listOf(
                        ProgrammeSpecificObjective(AdvancedTechnologies, "CODE_ADVA"),
                        ProgrammeSpecificObjective(Digitisation, "CODE_DIGI"),
                    )
                )
            ),
            strategies = sortedSetOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = listOf(
                ProgrammeFund(id = FUND_ID, selected = true),
            ),
        )

        private val callToUpdate = Call(
            id = CALL_ID,
            name = "new name",
            status = existingCall.status,
            startDate = existingCall.startDate,
            endDate = existingCall.endDate.plusDays(1),
            isAdditionalFundAllowed = existingCall.isAdditionalFundAllowed,
            lengthOfPeriod = existingCall.lengthOfPeriod!!,
            description = emptySet(),
            priorityPolicies = setOf(AdvancedTechnologies, Digitisation, Growth),
            strategies = setOf(EUStrategyBalticSeaRegion, AtlanticStrategy, MediterraneanSeaBasin),
            fundIds = setOf(FUND_ID),
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @RelaxedMockK
    lateinit var callValidator: CallValidator

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var updateCall: UpdateCall

    @Test
    fun `update published Call - changes are allowed`() {
        every { persistence.getCallIdForNameIfExists(callToUpdate.name) } returns null
        every { persistence.getCallById(CALL_ID) } returns existingCall.copy(name = "old name")
        val slotCallUpdate = slot<Call>()
        every { persistence.updateCall(capture(slotCallUpdate)) } returns existingCall

        updateCall.updateCall(callToUpdate)
        assertThat(slotCallUpdate.captured).isEqualTo(callToUpdate)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.CALL_CONFIGURATION_CHANGED,
                entityRelatedId = CALL_ID,
                description = "Configuration of published call id=592 name='call name' changed:\n" +
                    "name changed from 'old name' to 'call name'"
            )
        )
    }

    @Test
    fun `update Call - change of status is not allowed`() {
        every { persistence.getCallIdForNameIfExists(callToUpdate.name) } returns null
        every { persistence.getCallById(CALL_ID) } returns existingCall

        assertThrows<CallStatusChangeForbidden> {
            updateCall.updateCall(callToUpdate.copy(status = CallStatus.DRAFT))
        }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @ParameterizedTest
    @MethodSource("provideNotAllowedChangesToPublishedCall")
    fun `update published Call - change of {field} is not allowed`(call: Call) {
        every { persistence.getCallIdForNameIfExists(callToUpdate.name) } returns null
        every { persistence.getCallById(CALL_ID) } returns existingCall.copy(name = "old name")

        assertThrows<UpdateRestrictedFieldsWhenCallPublished> { updateCall.updateCall(call) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    private fun provideNotAllowedChangesToPublishedCall(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(callToUpdate.copy(startDate = ZonedDateTime.now())),
            Arguments.of(callToUpdate.copy(isAdditionalFundAllowed = !callToUpdate.isAdditionalFundAllowed)),
            Arguments.of(callToUpdate.copy(lengthOfPeriod = callToUpdate.lengthOfPeriod.plus(1))),
            Arguments.of(callToUpdate.copy(priorityPolicies = emptySet())),
            Arguments.of(callToUpdate.copy(strategies = emptySet())),
            Arguments.of(callToUpdate.copy(fundIds = emptySet())),
        )
    }

}
