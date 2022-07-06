package io.cloudflight.jems.server.dataGenerator.call

import io.cloudflight.jems.api.call.ApplicationFormConfigurationApi
import io.cloudflight.jems.api.call.CallApi
import io.cloudflight.jems.api.call.dto.AllowedRealCostsDTO
import io.cloudflight.jems.api.call.dto.CallFundRateDTO
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.CallUpdateRequestDTO
import io.cloudflight.jems.api.call.dto.PreSubmissionPluginsDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.StepSelectionOptionDTO
import io.cloudflight.jems.api.call.dto.applicationFormConfiguration.UpdateApplicationFormFieldConfigurationRequestDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.dataGenerator.CALL_DATA_INITIALIZER_ORDER
import io.cloudflight.jems.server.dataGenerator.PROGRAMME_LUMP_SUMS
import io.cloudflight.jems.server.dataGenerator.PROGRAMME_PRIORITY
import io.cloudflight.jems.server.dataGenerator.PROGRAMME_UNIT_COSTS
import io.cloudflight.jems.server.dataGenerator.SELECTED_PROGRAMME_FUNDS
import io.cloudflight.jems.server.dataGenerator.STANDARD_CALL_DETAIL
import io.cloudflight.jems.server.dataGenerator.STANDARD_CALL_LENGTH_OF_PERIOD
import io.cloudflight.jems.server.dataGenerator.inputTranslation
import io.cloudflight.jems.server.plugin.pre_submission_check.PreSubmissionCheckOff
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.Ordered
import java.math.BigDecimal
import java.time.ZonedDateTime


@Order(CALL_DATA_INITIALIZER_ORDER)
class StandardCallDataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

    private val callApi = FeignTestClientFactory.createClientApi(CallApi::class.java, port, config)
    private val afConfigApi = FeignTestClientFactory.createClientApi(ApplicationFormConfigurationApi::class.java, port, config)

    private var callId = 0L

    @Test
    @Order(1)
    @ExpectSelect(157)
    @ExpectInsert(148)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should create standard call`() {
        assertThat(
            callApi.createCall(
                CallUpdateRequestDTO(
                    name = "Standard Call",
                    type = CallType.STANDARD,
                    startDateTime = ZonedDateTime.now().minusDays(1),
                    endDateTime = ZonedDateTime.now().plusDays(1),
                    additionalFundAllowed = true,
                    lengthOfPeriod = STANDARD_CALL_LENGTH_OF_PERIOD,
                    funds = setOf(
                        CallFundRateDTO(SELECTED_PROGRAMME_FUNDS.filter { it.selected }.first(), BigDecimal.TEN, true)
                    ),
                    priorityPolicies = PROGRAMME_PRIORITY.specificObjectives.map { it.programmeObjectivePolicy }
                        .toSet(),
                    description = inputTranslation("desc"),
                )
            ).also { callId = it.id }
        ).isNotNull
    }

    @Test
    @Order(2)
    @ExpectSelect(29)
    @ExpectInsert(0)
    @ExpectUpdate(2)
    @ExpectDelete(2)
    fun `should set application form configuration for the call`() {
        val afConfigSets = afConfigApi.getByCallId(callId)
        val updateAfConfig = afConfigSets.map {
            var availableInStep = it.availableInStep
            var visible = it.visible
            if (it.id == ApplicationFormFieldSetting.PROJECT_INVESTMENT_TITLE.id
                || it.id == ApplicationFormFieldSetting.PROJECT_ACTIVITIES_DELIVERABLES.id) {
                visible = true
                availableInStep = StepSelectionOptionDTO.STEP_TWO_ONLY
            }
            UpdateApplicationFormFieldConfigurationRequestDTO(it.id, visible, availableInStep)
        }.toMutableSet()

        val callDetail = afConfigApi.update(callId, updateAfConfig)

        assertThat(callDetail.id).isEqualTo(callId)
        assertThat(callDetail.applicationFormFieldConfigurations
            .find { it.id == ApplicationFormFieldSetting.PROJECT_INVESTMENT_TITLE.id }?.availableInStep)
            .isEqualTo(StepSelectionOptionDTO.STEP_TWO_ONLY)
        assertThat(callDetail.applicationFormFieldConfigurations
            .find { it.id == ApplicationFormFieldSetting.PROJECT_ACTIVITIES_DELIVERABLES.id }?.availableInStep)
            .isEqualTo(StepSelectionOptionDTO.STEP_TWO_ONLY)
    }

    @Test
    @Order(2)
    @ExpectSelect(24)
    @ExpectInsert(0)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should set no-check pre-submission add-on for the call`() {
        assertThat(
            callApi.updatePreSubmissionCheckSettings(
                callId,
                PreSubmissionPluginsDTO(
                    firstStepPluginKey = PreSubmissionCheckOff.KEY,
                    pluginKey = PreSubmissionCheckOff.KEY
                )
            )
                .preSubmissionCheckPluginKey
        ).isEqualTo(PreSubmissionCheckOff.KEY)
    }

    @Test
    @Order(2)
    @ExpectSelect(6)
    @ExpectInsert(0)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should set call allowed real costs for the call`() {
        assertThat(
            callApi.updateAllowedRealCosts(
                callId,
                AllowedRealCostsDTO(
                    allowRealStaffCosts = true,
                    allowRealTravelAndAccommodationCosts = false,
                    allowRealExternalExpertiseAndServicesCosts = true,
                    allowRealEquipmentCosts = true,
                    allowRealInfrastructureCosts = true
                )
            )
        ).isEqualTo(
            AllowedRealCostsDTO(
                allowRealStaffCosts = true,
                allowRealTravelAndAccommodationCosts = false,
                allowRealExternalExpertiseAndServicesCosts = true,
                allowRealEquipmentCosts = true,
                allowRealInfrastructureCosts = true
            )
        )
    }

    @Test
    @Order(2)
    @ExpectSelect(24)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should set call budget settings for the call`() {
        assertThat(
            callApi.updateCallFlatRateSetup(
                callId,
                FlatRateSetupDTO(
                    staffCostFlatRateSetup = FlatRateDTO(15, true),
                    officeAndAdministrationOnStaffCostsFlatRateSetup = FlatRateDTO(15, true),
                    officeAndAdministrationOnDirectCostsFlatRateSetup = FlatRateDTO(25, false),
                    travelAndAccommodationOnStaffCostsFlatRateSetup = FlatRateDTO(15, true),
                    otherCostsOnStaffCostsFlatRateSetup = FlatRateDTO(40, true)
                )
            ).flatRates
        ).isEqualTo(
            FlatRateSetupDTO(
                staffCostFlatRateSetup = FlatRateDTO(15, true),
                officeAndAdministrationOnStaffCostsFlatRateSetup = FlatRateDTO(15, true),
                officeAndAdministrationOnDirectCostsFlatRateSetup = FlatRateDTO(25, false),
                travelAndAccommodationOnStaffCostsFlatRateSetup = FlatRateDTO(15, true),
                otherCostsOnStaffCostsFlatRateSetup = FlatRateDTO(40, true)
            )
        )
    }

    @Test
    @Order(2)
    @ExpectSelect(24)
    @ExpectInsert(1)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should set lump sums settings for the call`() {
        assertThat(
            callApi.updateCallLumpSums(
                callId = callId, lumpSumIds = PROGRAMME_LUMP_SUMS.map { it.id!! }.toSet()
            )
        ).isNotNull
    }

    @Test
    @Order(2)
    @ExpectSelect(26)
    @ExpectInsert(1)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should set unit costs for the call`() {
        assertThat(
            callApi.updateCallUnitCosts(
                callId = callId, unitCostIds = PROGRAMME_UNIT_COSTS.map { it.id!! }.toSet()
            )
        ).isNotNull
    }

    @Test
    @Order(3)
    @ExpectSelect(22)
    @ExpectInsert(0)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should publish the call`() {
        assertThat(
            callApi.publishCall(callId)
                .status
        ).isEqualTo(CallStatus.PUBLISHED)
    }

    @Test
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ExpectSelect(22)
    @ExpectInsert(0)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should return the call`() {
        STANDARD_CALL_DETAIL = callApi.getCallById(callId)

        assertThat(STANDARD_CALL_DETAIL).isNotNull
    }
}
