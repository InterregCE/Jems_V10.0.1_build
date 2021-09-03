package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormFieldConfigurationDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.StepSelectionOptionDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.UpdateApplicationFormFieldConfigurationRequestDTO
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.get_application_form_field_configurations.GetApplicationFormConfigurationException
import io.cloudflight.jems.server.call.service.get_application_form_field_configurations.GetApplicationFormFieldConfigurationsInteractor
import io.cloudflight.jems.server.call.service.list_calls.ListCallsInteractor
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.call.service.update_application_form_field_configuration.UpdateApplicationFormFieldConfigurationsException
import io.cloudflight.jems.server.call.service.update_application_form_field_configuration.UpdateApplicationFormFieldConfigurationsInteractor
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

class ApplicationFormConfigurationControllerTest: UnitTest() {

    companion object {
        private const val ID = 1L

        private val callDetail = CallDetail(
            id = ID,
            name = "call name",
            status = CallStatus.DRAFT,
            startDate = ZonedDateTime.now().minusDays(1),
            endDateStep1 = null,
            endDate = ZonedDateTime.now().plusDays(1),
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 8,
            description = setOf(
                InputTranslation(language = SystemLanguage.EN, translation = "EN desc"),
                InputTranslation(language = SystemLanguage.SK, translation = "SK desc"),
            ),
            objectives = listOf(
                ProgrammePriority(
                code = "PRIO_CODE",
                objective = ProgrammeObjective.PO1,
                specificObjectives = listOf(
                    ProgrammeSpecificObjective(ProgrammeObjectivePolicy.AdvancedTechnologies, "CODE_ADVA"),
                    ProgrammeSpecificObjective(ProgrammeObjectivePolicy.Digitisation, "CODE_DIGI"),
                )
            )
            ),
            strategies = sortedSetOf(ProgrammeStrategy.EUStrategyBalticSeaRegion, ProgrammeStrategy.AtlanticStrategy),
            funds = listOf(
                ProgrammeFund(id = 10L, selected = true),
            ),
            flatRates = sortedSetOf(
                ProjectCallFlatRate(type = FlatRateType.OFFICE_AND_ADMINISTRATION_ON_OTHER_COSTS, rate = 5, adjustable = true),
            ),
            lumpSums = listOf(
                ProgrammeLumpSum(splittingAllowed = true),
            ),
            unitCosts = listOf(
                ProgrammeUnitCost(isOneCostCategory = true),
            ),
            applicationFormFieldConfigurations = mutableSetOf()
        )

        private val configDTO =  mutableSetOf(
            ApplicationFormFieldConfigurationDTO(
                id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                visible = true,
                availableInStep = StepSelectionOptionDTO.STEP_ONE_AND_TWO,
                visibilityLocked = true,
                stepSelectionLocked = true
            ),
            ApplicationFormFieldConfigurationDTO(
                id = ApplicationFormFieldSetting.PROJECT_RESULTS_DELIVERY_PERIOD.id,
                visible = false,
                visibilityLocked = false,
                availableInStep = StepSelectionOptionDTO.NONE,
                stepSelectionLocked = false
            )
        )

        private val updateConfigDTO = mutableSetOf(
            UpdateApplicationFormFieldConfigurationRequestDTO(
                id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                visible = true,
                availableInStep = StepSelectionOptionDTO.STEP_ONE_AND_TWO
            ),
            UpdateApplicationFormFieldConfigurationRequestDTO(
                id = ApplicationFormFieldSetting.PROJECT_RESULTS_DELIVERY_PERIOD.id,
                visible = false,
                availableInStep = StepSelectionOptionDTO.NONE
            )
        )

        private val configModel = mutableSetOf(
            ApplicationFormFieldConfiguration(
                id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
            ),
            ApplicationFormFieldConfiguration(
                id = ApplicationFormFieldSetting.PROJECT_RESULTS_DELIVERY_PERIOD.id,
                visibilityStatus = FieldVisibilityStatus.NONE
            )
        )
    }

    @MockK
    lateinit var getApplicationFormFieldFieldConfigurations: GetApplicationFormFieldConfigurationsInteractor

    @MockK
    lateinit var listCalls: ListCallsInteractor

    @MockK
    lateinit var updateApplicationFormFieldConfigurations: UpdateApplicationFormFieldConfigurationsInteractor

    @InjectMockKs
    private lateinit var controller: ApplicationFormConfigurationController

    @Test
    fun `get applicationFormFieldConfigurations by id`() {
        every { getApplicationFormFieldFieldConfigurations.get(ID) } returns configModel
        assertThat(controller.getByCallId(ID)).isEqualTo(configDTO)
    }

    @Test
    fun `get applicationFormFieldConfigurations fails on get exception`() {
        val exception = GetApplicationFormConfigurationException(Exception())
        every { getApplicationFormFieldFieldConfigurations.get(ID) } throws exception
        assertThrows<GetApplicationFormConfigurationException> { controller.getByCallId(ID) }
    }


    @Test
    fun `update applicationFormFieldConfigurations`() {
        val capturedConfigModel = slot<MutableSet<ApplicationFormFieldConfiguration>>()
        every { updateApplicationFormFieldConfigurations.update(ID, capture(capturedConfigModel)) } returns callDetail
        controller.update(ID, updateConfigDTO)
        verify { updateApplicationFormFieldConfigurations.update(ID, configModel) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }

    @Test
    fun `update applicationFormFieldConfigurations fails on update exception`() {
        val exception = UpdateApplicationFormFieldConfigurationsException(Exception())
        val capturedConfigModel = slot<MutableSet<ApplicationFormFieldConfiguration>>()
        every { updateApplicationFormFieldConfigurations.update(ID, capture(capturedConfigModel)) } throws exception

        assertThrows<UpdateApplicationFormFieldConfigurationsException> { controller.update(ID, updateConfigDTO) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }
}
