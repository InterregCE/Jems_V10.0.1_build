package io.cloudflight.jems.server.call.service.create_call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Digitalization
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.AtlanticStrategy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.EUStrategyBalticSeaRegion
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.validator.CallValidator
import io.cloudflight.jems.server.call.userWithId
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
import java.time.ZonedDateTime

class CreateCallTest: UnitTest() {

    companion object {
        private const val FUND_ID = 54L
        private val callToCreate = Call(
            name = "call to create",
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 9,
            description = setOf(
                InputTranslation(language = SystemLanguage.EN, translation = "EN desc"),
                InputTranslation(language = SystemLanguage.SK, translation = "SK desc"),
            ),
            priorityPolicies = setOf(Digitalization, AdvancedTechnologies),
            strategies = setOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            fundIds = setOf(FUND_ID),
        )

        private val expectedCallDetail = CallDetail(
            id = 0L,
            name = "call to create",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 9,
            description = setOf(
                InputTranslation(language = SystemLanguage.EN, translation = "EN desc"),
                InputTranslation(language = SystemLanguage.SK, translation = "SK desc"),
            ),
            objectives = listOf(ProgrammePriority(
                code = "PRIO_CODE",
                objective = ProgrammeObjective.PO1,
                specificObjectives = listOf(
                    ProgrammeSpecificObjective(AdvancedTechnologies, "CODE_ADVA"),
                    ProgrammeSpecificObjective(Digitalization, "CODE_DIGI"),
                )
            )),
            strategies = sortedSetOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = listOf(ProgrammeFund(id = FUND_ID, selected = true)),
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @MockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var callValidator: CallValidator

    @InjectMockKs
    private lateinit var createCall: CreateCall

    @Test
    fun `createCallInDraft - OK`() {
        val USER_ID = 5L
        every { persistence.getCallIdForNameIfExists("call to create") } returns null
        every { securityService.currentUser } returns userWithId(USER_ID)
        val slotCall = slot<Call>()
        val slotUserId = slot<Long>()
        every { persistence.createCall(capture(slotCall), capture(slotUserId)) } returns expectedCallDetail

        assertThat(createCall.createCallInDraft(callToCreate)).isEqualTo(expectedCallDetail)
        assertThat(slotCall.captured).isEqualTo(callToCreate.copy(status = CallStatus.DRAFT))

        val slotAudit = slot<AuditCandidate>()
        verify(exactly = 1) { auditService.logEvent(capture(slotAudit)) }

        assertThat(slotAudit.captured.action).isEqualTo(AuditAction.CALL_ADDED)
        assertThat(slotAudit.captured.description).startsWith("A new call id=0 name='call to create' was created as:\n" +
            "name set to call to create,\n" +
            "status set to DRAFT,\n" +
            "startDate set to ")
        assertThat(slotAudit.captured.description).endsWith("isAdditionalFundAllowed set to true,\n" +
            "lengthOfPeriod set to 9,\n" +
            "description set to [InputTranslation(language=EN, translation=EN desc), InputTranslation(language=SK, translation=SK desc)],\n" +
            "objectives set to [AdvancedTechnologies, Digitalization],\n" +
            "strategies set to [EUStrategyBalticSeaRegion, AtlanticStrategy],\n" +
            "fundIds set to [54]")
    }

    @Test
    fun `createCallInDraft - name is not unique`() {
        every { persistence.getCallIdForNameIfExists("call to create") } returns 89L
        assertThrows<CallNameNotUnique> { createCall.createCallInDraft(callToCreate) }
        verify(exactly = 0) { auditService.logEvent(any<AuditCandidate>()) }
    }

    @Test
    fun `createCallInDraft - wrong call status`() {
        every { persistence.getCallIdForNameIfExists("call to create") } returns null
        assertThrows<CallCreatedIsNotDraft> { createCall.createCallInDraft(callToCreate.copy(status = CallStatus.PUBLISHED)) }
        verify(exactly = 0) { auditService.logEvent(any<AuditCandidate>()) }
    }

    @Test
    fun `createCallInDraft - when error in persistence do not write audit log`() {
        every { persistence.getCallIdForNameIfExists("call to create") } returns null
        every { persistence.createCall(any(), any()) } throws RuntimeException("whatever not-expected exception")

        assertThrows<RuntimeException> { createCall.createCallInDraft(callToCreate) }
        verify(exactly = 0) { auditService.logEvent(any<AuditCandidate>()) }
    }

}
