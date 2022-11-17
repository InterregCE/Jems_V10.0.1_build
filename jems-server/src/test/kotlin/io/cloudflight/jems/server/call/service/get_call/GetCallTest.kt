package io.cloudflight.jems.server.call.service.get_call

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Digitisation
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.callFundRate
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

class GetCallTest : UnitTest() {

    companion object {
        private const val FUND_ID = 22L

        private val callDetail = CallDetail(
            id = 569L,
            name = "existing call",
            status = CallStatus.PUBLISHED,
            type = CallType.STANDARD,
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
                    objective = PO1,
                    specificObjectives = listOf(
                        ProgrammeSpecificObjective(AdvancedTechnologies, "CODE_ADVA"),
                        ProgrammeSpecificObjective(Digitisation, "CODE_DIGI"),
                    )
                )
            ),
            strategies = sortedSetOf(ProgrammeStrategy.EUStrategyBalticSeaRegion, ProgrammeStrategy.AtlanticStrategy),
            funds = sortedSetOf(callFundRate(FUND_ID)),
            flatRates = sortedSetOf(
                ProjectCallFlatRate(
                    type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS,
                    rate = 5,
                    adjustable = true
                ),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(splittingAllowed = true, fastTrack = false),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(projectId = null, isOneCostCategory = true),
            ),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null,
            firstStepPreSubmissionCheckPluginKey = null,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
        )

        private val call = CallSummary(
            id = callDetail.id,
            name = "existing call",
            status = CallStatus.DRAFT,
            startDate = callDetail.startDate,
            endDate = callDetail.endDate,
            endDateStep1 = null
        )

    }

    @MockK
    lateinit var persistence: CallPersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var getCall: GetCall

    @Test
    fun `get published calls`() {
        every { securityService.currentUser } returns applicantUser
        every { persistence.getPublishedAndOpenCalls(any()) } returns PageImpl(listOf(call))
        assertThat(getCall.getPublishedCalls(Pageable.unpaged()).content).containsExactly(call)
    }

    @Test
    fun `get calls`() {
        every { securityService.currentUser } returns applicantUser
        every { persistence.getCalls(any()) } returns PageImpl(listOf(call))
        assertThat(getCall.getCalls(Pageable.unpaged()).content).containsExactly(call)
    }

    @Test
    fun getCallById() {
        every { persistence.getCallById(callId = callDetail.id) } returns callDetail
        assertThat(getCall.getCallById(callId = callDetail.id)).isEqualTo(callDetail)
    }

}
