package io.cloudflight.jems.server.call.service.create_call

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Digitisation
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.AtlanticStrategy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.EUStrategyBalticSeaRegion
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.callFundRate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.validator.CallValidator
import io.cloudflight.jems.server.call.userWithId
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

class CreateCallTest : UnitTest() {

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
            priorityPolicies = setOf(Digitisation, AdvancedTechnologies),
            strategies = setOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = setOf(callFundRate(FUND_ID)),
        )

        private val expectedCallDetail = CallDetail(
            id = 0L,
            name = "call to create",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 9,
            description = setOf(
                InputTranslation(language = SystemLanguage.EN, translation = "EN desc"),
                InputTranslation(language = SystemLanguage.SK, translation = "SK desc"),
            ),
            objectives = listOf(
                ProgrammePriority(
                    code = "PRIO_CODE",
                    objective = ProgrammeObjective.PO1,
                    specificObjectives = listOf(
                        ProgrammeSpecificObjective(AdvancedTechnologies, "CODE_ADVA"),
                        ProgrammeSpecificObjective(Digitisation, "CODE_DIGI"),
                    )
                )
            ),
            strategies = sortedSetOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = sortedSetOf(callFundRate(FUND_ID)),
            applicationFormFieldConfigurations = ApplicationFormFieldSetting.getDefaultApplicationFormFieldConfigurations()
        )
    }

    private val expectedCallDetailWith2StepEnabled = expectedCallDetail.copy(
        endDateStep1 = ZonedDateTime.now().plusHours(4)
    )

    @MockK
    lateinit var persistence: CallPersistence

    @MockK
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var callValidator: CallValidator

    @InjectMockKs
    private lateinit var createCall: CreateCall

    @BeforeEach
    fun resetMocks() {
        clearMocks(persistence)
    }

    @Test
    fun `createCallInDraft - OK`() {
        val USER_ID = 5L
        every { persistence.getCallIdForNameIfExists("call to create") } returns null
        every { securityService.currentUser } returns userWithId(USER_ID)
        val slotCall = slot<Call>()
        val slotUserId = slot<Long>()
        every { persistence.createCall(capture(slotCall), capture(slotUserId)) } returns expectedCallDetail
        every {
            persistence.saveApplicationFormFieldConfigurations(
                expectedCallDetail.id,
                ApplicationFormFieldSetting.getDefaultApplicationFormFieldConfigurations()
            )
        } returns expectedCallDetail
        every {
            persistence.updateProjectCallStateAids(
                expectedCallDetail.id,
                emptySet()
            )
        } returns expectedCallDetail

        assertThat(createCall.createCallInDraft(callToCreate)).isEqualTo(expectedCallDetail)
        assertThat(slotCall.captured).isEqualTo(callToCreate.copy(status = CallStatus.DRAFT))

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }

        verify(exactly = 1) {
            persistence.saveApplicationFormFieldConfigurations(
                expectedCallDetail.id,
                ApplicationFormFieldSetting.getDefaultApplicationFormFieldConfigurations()
            )
        }
        with(slotAudit.captured.auditCandidate) {
            assertThat(action).isEqualTo(AuditAction.CALL_ADDED)
            assertThat(description).startsWith(
                "A new call id=0 name='call to create' was created as:\n" +
                    "name set to 'call to create',\n" +
                    "status set to DRAFT,\n" +
                    "startDate set to "
            )
            assertThat(description).endsWith(
                "isAdditionalFundAllowed set to enabled,\n" +
                    "lengthOfPeriod set to 9,\n" +
                    "description set to [\n" +
                    "  InputTranslation(language=EN, translation=EN desc)\n" +
                    "  InputTranslation(language=SK, translation=SK desc)\n" +
                    "],\n" +
                    "objectives set to [\n" +
                    "  AdvancedTechnologies\n" +
                    "  Digitisation\n" +
                    "],\n" +
                    "strategies set to [\n" +
                    "  EUStrategyBalticSeaRegion\n" +
                    "  AtlanticStrategy\n" +
                    "],\n" +
                    "fundIds set to [54],\n" +
                    "funds set to [\n" +
                    "  CallFundRate(" +
                    "programmeFund=ProgrammeFund(id=54, selected=true, type=OTHER, abbreviation=[], description=[]), " +
                    "rate=10, adjustable=true)\n" +
                    "]"
            )
        }
    }

    @Test
    fun `createCallInDraft - name is not unique`() {
        every { persistence.getCallIdForNameIfExists("call to create") } returns 89L
        assertThrows<CallNameNotUnique> { createCall.createCallInDraft(callToCreate) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @Test
    fun `createCallInDraft - wrong call status`() {
        every { persistence.getCallIdForNameIfExists("call to create") } returns null
        assertThrows<CallCreatedIsNotDraft> { createCall.createCallInDraft(callToCreate.copy(status = CallStatus.PUBLISHED)) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @Test
    fun `createCallInDraft - when error in persistence do not write audit log`() {
        every { persistence.getCallIdForNameIfExists("call to create") } returns null
        every { persistence.createCall(any(), any()) } throws RuntimeException("whatever not-expected exception")

        assertThrows<RuntimeException> { createCall.createCallInDraft(callToCreate) }
        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @Test
    fun `createCAllInDraft - 2 step enabled OK`() {
        val USER_ID = 5L
        every { persistence.getCallIdForNameIfExists("call to create") } returns null
        every { securityService.currentUser } returns userWithId(USER_ID)
        every {
            persistence.saveApplicationFormFieldConfigurations(
                expectedCallDetail.id,
                ApplicationFormFieldSetting.getDefaultApplicationFormFieldConfigurations()
            )
        } returns expectedCallDetail
        every {
            persistence.updateProjectCallStateAids(
                expectedCallDetail.id,
                emptySet()
            )
        } returns expectedCallDetail

        val slotCall = slot<Call>()
        val slotUserId = slot<Long>()
        every {
            persistence.createCall(capture(slotCall), capture(slotUserId))
        } returns expectedCallDetailWith2StepEnabled
        assertThat(
            createCall.createCallInDraft(
                callToCreate.copy(
                    endDateStep1 = ZonedDateTime.now().plusHours(4)
                )
            )
        ).isEqualTo(expectedCallDetailWith2StepEnabled)

        verify(exactly = 1) {
            persistence.saveApplicationFormFieldConfigurations(
                expectedCallDetail.id,
                ApplicationFormFieldSetting.getDefaultApplicationFormFieldConfigurations()
            )
        }
    }
}
