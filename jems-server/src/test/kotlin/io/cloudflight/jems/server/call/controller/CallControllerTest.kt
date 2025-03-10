package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.CallCostOptionDTO
import io.cloudflight.jems.api.call.dto.CallDTO
import io.cloudflight.jems.api.call.dto.CallDetailDTO
import io.cloudflight.jems.api.call.dto.CallFundRateDTO
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.api.call.dto.PreSubmissionPluginsDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS
import io.cloudflight.jems.api.common.dto.IdNamePairDTO
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
import io.cloudflight.jems.server.call.callFundRate
import io.cloudflight.jems.server.call.service.costOption.getCallCostOption.GetCallCostOptionInteractor
import io.cloudflight.jems.server.call.service.costOption.updateCallCostOption.UpdateCallCostOptionInteractor
import io.cloudflight.jems.server.call.service.create_call.CreateCallInteractor
import io.cloudflight.jems.server.call.service.get_allow_real_costs.GetAllowedRealCostsInteractor
import io.cloudflight.jems.server.call.service.get_call.GetCallInteractor
import io.cloudflight.jems.server.call.service.get_call_checklists.GetCallChecklistsInteractor
import io.cloudflight.jems.server.call.service.list_calls.ListCallsException
import io.cloudflight.jems.server.call.service.list_calls.ListCallsInteractor
import io.cloudflight.jems.server.call.service.model.Call
import io.cloudflight.jems.server.call.service.model.CallChecklist
import io.cloudflight.jems.server.call.service.model.CallCostOption
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.CallSummary
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.call.service.model.PreSubmissionPlugins
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.call.service.publish_call.PublishCallInteractor
import io.cloudflight.jems.server.call.service.update_allow_real_costs.UpdateAllowedRealCostsInteractor
import io.cloudflight.jems.server.call.service.update_call.UpdateCallInteractor
import io.cloudflight.jems.server.call.service.update_call_checklists.UpdateCallChecklistsInteractor
import io.cloudflight.jems.server.call.service.update_call_flat_rates.UpdateCallFlatRatesInteractor
import io.cloudflight.jems.server.call.service.update_call_lump_sums.UpdateCallLumpSumsInteractor
import io.cloudflight.jems.server.call.service.update_call_unit_costs.UpdateCallUnitCostsInteractor
import io.cloudflight.jems.server.call.service.update_pre_submission_check_configuration.UpdatePreSubmissionCheckSettingsInteractor
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.programme.service.costoption.model.PaymentClaim
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

class CallControllerTest : UnitTest() {

    companion object {

        private const val ID = 1L
        private const val PLUGIN_KEY = "pluginKey"
        private const val PLUGIN_KEY_PARTNER_REPORT = "pluginKey-partnerReport"
        private const val PLUGIN_KEY_PARTNER_CONTROL_REPORT = "pluginKey-partnerControlReport"
        private const val PLUGIN_KEY_PROJECT_REPORT = "pluginKey-projectReport"
        private const val PLUGIN_KEY_CONTROL_SAMPLING = "pluginKey-control-sampling"

        private val call = CallSummary(
            id = ID,
            name = "call name",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now().minusDays(1),
            endDate = ZonedDateTime.now().plusDays(1),
            endDateStep1 = null
        )

        val fundDtos = listOf(
            CallFundRateDTO(
                programmeFund = ProgrammeFundDTO(id = 10L, selected = true),
                rate = BigDecimal.TEN,
                adjustable = true
            )
        )

        private val callDetail = CallDetail(
            id = ID,
            name = "call name",
            status = CallStatus.DRAFT,
            type = CallType.STANDARD,
            startDate = call.startDate,
            endDateStep1 = null,
            endDate = call.endDate,
            isAdditionalFundAllowed = true,
            isDirectContributionsAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = EN, translation = "EN desc"),
                InputTranslation(language = SK, translation = "SK desc"),
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
            strategies = sortedSetOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = sortedSetOf(callFundRate(10L)),
            flatRates = sortedSetOf(
                ProjectCallFlatRate(type = OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS, rate = 5, adjustable = true),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(splittingAllowed = true, fastTrack = false, paymentClaim = PaymentClaim.IncurredByBeneficiaries),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(projectId = null, isOneCostCategory = true, paymentClaim = PaymentClaim.IncurredByBeneficiaries),
            ),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = PLUGIN_KEY,
            firstStepPreSubmissionCheckPluginKey = PLUGIN_KEY,
            reportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_REPORT,
            reportProjectCheckPluginKey = PLUGIN_KEY_PROJECT_REPORT,
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
            controlReportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_CONTROL_REPORT,
            controlReportSamplingCheckPluginKey = PLUGIN_KEY_CONTROL_SAMPLING
        )

        private val callDto = CallDTO(
            id = ID,
            name = "call name",
            status = CallStatus.DRAFT,
            startDateTime = call.startDate,
            endDateTime = call.endDate,
            endDateTimeStep1 = null
        )

        private val callDetailDto = CallDetailDTO(
            id = ID,
            name = "call name",
            status = CallStatus.DRAFT,
            type = CallType.STANDARD,
            startDateTime = call.startDate,
            endDateTimeStep1 = null,
            endDateTime = call.endDate,
            additionalFundAllowed = true,
            directContributionsAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = EN, translation = "EN desc"),
                InputTranslation(language = SK, translation = "SK desc"),
            ),
            objectives = listOf(
                ProgrammePriorityDTO(
                    code = "PRIO_CODE",
                    objective = PO1,
                    specificObjectives = listOf(
                        ProgrammeSpecificObjectiveDTO(AdvancedTechnologies, "CODE_ADVA", "RSO1.1"),
                        ProgrammeSpecificObjectiveDTO(Digitisation, "CODE_DIGI", "RSO1.2"),
                    )
                )
            ),
            strategies = listOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = fundDtos,
            flatRates = FlatRateSetupDTO(
                officeAndAdministrationOnDirectCostsFlatRateSetup = FlatRateDTO(rate = 5, adjustable = true)
            ),
            lumpSums = listOf(
                ProgrammeLumpSumListDTO(id = 0L, splittingAllowed = true),
            ),
            unitCosts = listOf(
                ProgrammeUnitCostListDTO(id = 0L),
            ),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = PLUGIN_KEY,
            firstStepPreSubmissionCheckPluginKey = PLUGIN_KEY,
            reportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_REPORT,
            reportProjectCheckPluginKey = PLUGIN_KEY_PROJECT_REPORT,
            controlReportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_CONTROL_REPORT,
            controlReportSamplingCheckPluginKey = PLUGIN_KEY_CONTROL_SAMPLING
        )

        private val callUpdateDto = CallUpdateRequestDTO(
            id = ID,
            name = "call name",
            type = CallType.STANDARD,
            startDateTime = call.startDate,
            endDateTime = call.endDate,
            additionalFundAllowed = true,
            directContributionsAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = EN, translation = "EN desc"),
                InputTranslation(language = SK, translation = "SK desc"),
            ),
            priorityPolicies = setOf(AdvancedTechnologies, Digitisation),
            strategies = setOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = fundDtos.toSet()
        )

        private val callUpdate = Call(
            id = ID,
            name = "call name",
            status = null,
            type = CallType.STANDARD,
            startDate = call.startDate,
            endDate = call.endDate,
            isAdditionalFundAllowed = true,
            isDirectContributionsAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = EN, translation = "EN desc"),
                InputTranslation(language = SK, translation = "SK desc"),
            ),
            priorityPolicies = setOf(AdvancedTechnologies, Digitisation),
            strategies = setOf(EUStrategyBalticSeaRegion, AtlanticStrategy),
            funds = setOf(callFundRate(10L)),
        )

        val costOption = CallCostOption(
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
        )

        val costOptionExpected = CallCostOptionDTO(
            projectDefinedUnitCostAllowed = false,
            projectDefinedLumpSumAllowed = true,
        )

        val callChecklists = listOf(
            CallChecklist(
                id = 1,
                name = "Checklist 1",
                type = ProgrammeChecklistType.CONTROL,
                selected = true,
                lastModificationDate = null
            ),
            CallChecklist(
                id = 2,
                name = "Checklist 2",
                type = ProgrammeChecklistType.VERIFICATION,
                selected = false,
                lastModificationDate = null
            )
        )
    }

    @MockK
    lateinit var getCall: GetCallInteractor

    @MockK
    lateinit var listCalls: ListCallsInteractor

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
    lateinit var getCallCostOption: GetCallCostOptionInteractor

    @MockK
    lateinit var updateCallCostOption: UpdateCallCostOptionInteractor

    @MockK
    lateinit var publishCall: PublishCallInteractor

    @MockK
    lateinit var getAllowedRealCostsInteractor: GetAllowedRealCostsInteractor

    @MockK
    lateinit var updateAllowedRealCostsInteractor: UpdateAllowedRealCostsInteractor

    @MockK
    lateinit var updatePreSubmissionCheckSettings: UpdatePreSubmissionCheckSettingsInteractor

    @MockK
    lateinit var getCallChecklists: GetCallChecklistsInteractor

    @MockK
    lateinit var updateCallSelectedChecklists: UpdateCallChecklistsInteractor

    @InjectMockKs
    private lateinit var controller: CallController

    @Test
    fun getCalls() {
        every { getCall.getCalls(any()) } returns PageImpl(listOf(call))
        assertThat(controller.getCalls(Pageable.unpaged()).content).containsExactly(callDto)
    }

    @Test
    fun `list calls`() {
        val idNamePair = IdNamePair(id = ID, name = "name")
        val idNamePairDTO = IdNamePairDTO(id = ID, name = "name")
        every { listCalls.list(CallStatus.PUBLISHED) } returns listOf(idNamePair)
        assertThat(controller.listCalls(CallStatus.PUBLISHED)).containsExactly(idNamePairDTO)
    }

    @Test
    fun `list calls fails on list exception`() {
        val exception = ListCallsException(Exception())
        every { listCalls.list(null) } throws exception
        assertThrows<ListCallsException> { controller.listCalls(null) }
    }

    @Test
    fun getPublishedCalls() {
        every { getCall.getPublishedCalls(any()) } returns PageImpl(listOf(call.copy(status = CallStatus.PUBLISHED)))
        assertThat(controller.getPublishedCalls(Pageable.unpaged()).content).containsExactly(callDto.copy(status = CallStatus.PUBLISHED))
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
        assertThat(slotFlatRate.captured).containsExactly(
            ProjectCallFlatRate(type = FlatRateType.STAFF_COSTS, rate = 15, adjustable = true)
        )
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

    @Test
    fun getAllowedCostOptions() {
        every { getCallCostOption.getCallCostOption(45L) } returns costOption
        assertThat(controller.getAllowedCostOptions(45L)).isEqualTo(costOptionExpected)
    }

    @Test
    fun updateAllowedCostOptions() {
        val slotOptions = slot<CallCostOption>()
        every { updateCallCostOption.updateCallCostOption(48L, capture(slotOptions)) } returns costOption
        controller.updateAllowedCostOption(48L, costOptionExpected)
        assertThat(slotOptions.captured).isEqualTo(costOption)
    }

    @Test
    fun `update call's pre-submission check settings`() {
        val pluginKey = slot<PreSubmissionPlugins>()
        every { updatePreSubmissionCheckSettings.update(40L, capture(pluginKey)) } returns callDetail

        assertThat(
            controller.updatePreSubmissionCheckSettings(
                40L, PreSubmissionPluginsDTO(
                    pluginKey = PLUGIN_KEY,
                    firstStepPluginKey = PLUGIN_KEY,
                    reportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_REPORT,
                    reportProjectCheckPluginKey = PLUGIN_KEY_PROJECT_REPORT,
                    controlReportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_CONTROL_REPORT,
                    controlReportSamplingCheckPluginKey = PLUGIN_KEY_CONTROL_SAMPLING
                )
            )
        ).isEqualTo(callDetailDto)

        assertThat(pluginKey.captured).isEqualTo(
            PreSubmissionPlugins(
                pluginKey = PLUGIN_KEY,
                firstStepPluginKey = PLUGIN_KEY,
                reportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_REPORT,
                reportProjectCheckPluginKey = PLUGIN_KEY_PROJECT_REPORT,
                controlReportPartnerCheckPluginKey = PLUGIN_KEY_PARTNER_CONTROL_REPORT,
                controlReportSamplingCheckPluginKey = PLUGIN_KEY_CONTROL_SAMPLING
            )
        )
    }

    @Test
    fun `get call checklists - ok`() {
        every { getCallChecklists.getCallChecklists(ID, any()) } returns callChecklists
        val checklists = controller.getChecklists(ID, Pageable.unpaged())
        assertThat(checklists.size).isEqualTo(callChecklists.size)
    }

    @Test
    fun `update call selected checklists - ok`() {
        val selectedIds = setOf(1L, 2L, 3L)
        val slotChecklists = slot<Set<Long>>()
        every { updateCallSelectedChecklists.updateCallChecklists(ID, capture(slotChecklists)) } just runs

        controller.updateSelectedChecklists(ID, selectedIds)
        assertThat(slotChecklists.captured).isEqualTo(selectedIds)
    }
}
