package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.CallDTO
import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumListDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostListDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective.PO1
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.AdvancedTechnologies
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.Digitisation
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeSpecificObjectiveDTO
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.AtlanticStrategy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy.EUStrategyBalticSeaRegion
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.call.service.create_call.CreateCallInteractor
import io.cloudflight.jems.server.call.service.get_call.GetCallInteractor
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.publish_call.PublishCallInteractor
import io.cloudflight.jems.server.call.service.update_call.UpdateCallInteractor
import io.cloudflight.jems.server.call.service.update_call_flat_rates.UpdateCallFlatRatesInteractor
import io.cloudflight.jems.server.call.service.update_call_lump_sums.UpdateCallLumpSumsInteractor
import io.cloudflight.jems.server.call.service.update_call_unit_costs.UpdateCallUnitCostsInteractor
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

class CallControllerTest: UnitTest() {

    companion object {

        private const val ID = 1L

        private val call = CallSummary(
            id = ID,
            name = "call name",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1),
        )

        private val callDetail = CallDetail(
            id = ID,
            name = "call name",
            status = CallStatus.DRAFT,
            startDate = call.startDate,
            endDate = call.endDate,
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = EN, translation = "EN desc"),
                InputTranslation(language = SK, translation = "SK desc"),
            ),
            objectives = listOf(ProgrammePriority(
                code = "PRIO_CODE",
                objective = PO1,
                specificObjectives = listOf(
                    ProgrammeSpecificObjective(AdvancedTechnologies, "CODE_ADVA"),
                    ProgrammeSpecificObjective(Digitisation, "CODE_DIGI"),
                )
            )),
            strategies = sortedSetOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = listOf(
                ProgrammeFund(id = 10L, selected = true),
            ),
            flatRates = sortedSetOf(
                ProjectCallFlatRate(type = OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS, rate = 5, isAdjustable = true),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(splittingAllowed = true),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(isOneCostCategory = true),
            ),
        )

        private val callDto = CallDTO(
            id = ID,
            name = "call name",
            status = CallStatus.DRAFT,
            startDateTime = call.startDate,
            endDateTime = call.endDate,
        )

        private val callDetailDto = CallDetailDTO(
            id = ID,
            name = "call name",
            status = CallStatus.DRAFT,
            startDateTime = call.startDate,
            endDateTime = call.endDate,
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = EN, translation = "EN desc"),
                InputTranslation(language = SK, translation = "SK desc"),
            ),
            objectives = listOf(ProgrammePriorityDTO(
                code = "PRIO_CODE",
                objective = PO1,
                specificObjectives = listOf(
                    ProgrammeSpecificObjectiveDTO(AdvancedTechnologies, "CODE_ADVA"),
                    ProgrammeSpecificObjectiveDTO(Digitisation, "CODE_DIGI"),
                )
            )),
            strategies = listOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = listOf(
                ProgrammeFundDTO(id = 10L, selected = true),
            ),
            flatRates = FlatRateSetupDTO(officeAndAdministrationOnDirectCostsFlatRateSetup = FlatRateDTO(rate = 5, isAdjustable = true)),
            lumpSums = listOf(
                ProgrammeLumpSumListDTO(id = 0L, splittingAllowed = true),
            ),
            unitCosts = listOf(
                ProgrammeUnitCostListDTO(id = 0L),
            ),
        )

        private val callUpdateDto = CallUpdateRequestDTO(
            id = ID,
            name = "call name",
            startDateTime = call.startDate,
            endDateTime = call.endDate,
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = EN, translation = "EN desc"),
                InputTranslation(language = SK, translation = "SK desc"),
            ),
            priorityPolicies = setOf(AdvancedTechnologies, Digitisation),
            strategies = setOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            fundIds = setOf(10L),
        )

        private val callUpdate = Call(
            id = ID,
            name = "call name",
            status = null,
            startDate = call.startDate,
            endDate = call.endDate,
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = EN, translation = "EN desc"),
                InputTranslation(language = SK, translation = "SK desc"),
            ),
            priorityPolicies = setOf(AdvancedTechnologies, Digitisation),
            strategies = setOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            fundIds = setOf(10L),
        )

    }

    @MockK
    lateinit var getCall: GetCallInteractor

    @MockK
    lateinit var createCall: CreateCallInteractor

    @MockK
    lateinit var updateCall: UpdateCallInteractor

    @MockK
    lateinit var updateCallFlatRates: UpdateCallFlatRatesInteractor

    @MockK
    lateinit var updateCallLumpSums: UpdateCallLumpSumsInteractor

    @MockK
    lateinit var updateCallUnitCosts: UpdateCallUnitCostsInteractor

    @MockK
    lateinit var publishCall: PublishCallInteractor

    @InjectMockKs
    private lateinit var controller: CallController

    @Test
    fun getCalls() {
        every { getCall.getCalls(any()) } returns PageImpl(listOf(call))
        assertThat(controller.getCalls(Pageable.unpaged()).content).containsExactly(callDto)
    }

    @Test
    fun getCallById() {
        every { getCall.getCallById(ID) } returns callDetail
        assertThat(controller.getCallById(ID)).isEqualTo(callDetailDto)
    }

    @Test
    fun createCall() {
        val slotCallCreate = slot<Call>()
        every { createCall.createCallInDraft(capture(slotCallCreate)) } returns callDetail
        assertThat(controller.createCall(callUpdateDto.copy(id = null))).isEqualTo(callDetailDto)
        assertThat(slotCallCreate.captured).isEqualTo(callUpdate.copy(id = 0))
    }

    @Test
    fun updateCall() {
        val slotCallUpdate = slot<Call>()
        every { updateCall.updateCall(capture(slotCallUpdate)) } returns callDetail.copy(id = 17L)
        assertThat(controller.updateCall(callUpdateDto.copy(id = 17L))).isEqualTo(callDetailDto.copy(id = 17L))
        assertThat(slotCallUpdate.captured).isEqualTo(callUpdate.copy(id = 17L))
    }

    @Test
    fun publishCall() {
        every { publishCall.publishCall(25L) } returns call.copy(id = 25L, status = CallStatus.PUBLISHED)
        assertThat(controller.publishCall(25L)).isEqualTo(callDto.copy(id = 25L, status = CallStatus.PUBLISHED))
    }

    @Test
    fun updateCallFlatRateSetup() {
        val slotFlatRate = slot<Set<ProjectCallFlatRate>>()
        every { updateCallFlatRates.updateFlatRateSetup(30L, capture(slotFlatRate)) } returns callDetail
        controller.updateCallFlatRateSetup(30L, FlatRateSetupDTO(staffCostFlatRateSetup = FlatRateDTO(15, true)))
        assertThat(slotFlatRate.captured).containsExactly(ProjectCallFlatRate(type = FlatRateType.STAFF_COSTS, rate = 15, isAdjustable = true))
    }

    @Test
    fun updateCallLumpSums() {
        val slotLumpSumIds = slot<Set<Long>>()
        every { updateCallLumpSums.updateLumpSums(35L, capture(slotLumpSumIds)) } returns callDetail
        controller.updateCallLumpSums(35L, setOf(45, 69))
        assertThat(slotLumpSumIds.captured).containsExactlyInAnyOrder(45, 69)
    }

    @Test
    fun updateCallUnitCosts() {
        val slotUnitCostIds = slot<Set<Long>>()
        every { updateCallUnitCosts.updateUnitCosts(40L, capture(slotUnitCostIds)) } returns callDetail
        controller.updateCallUnitCosts(40L, setOf(259, 337))
        assertThat(slotUnitCostIds.captured).containsExactlyInAnyOrder(259, 337)
    }

}
