package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormConfigurationDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormConfigurationSummaryDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.ApplicationFormFieldConfigurationDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.FieldVisibilityStatusDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.UpdateApplicationFormConfigurationRequestDTO
import io.cloudflight.jems.api.call.dto.application_form_configuration.UpdateApplicationFormFieldConfigurationRequestDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.get_application_form_configuration.GetApplicationFormConfigurationException
import io.cloudflight.jems.server.call.service.get_application_form_configuration.GetApplicationFormConfigurationInteractor
import io.cloudflight.jems.server.call.service.list_application_form_configurations.ListApplicationFormConfigurationInteractor
import io.cloudflight.jems.server.call.service.list_application_form_configurations.ListApplicationFormConfigurationsException
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfigurationSummary
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.call.service.update_application_form_configuration.UpdateApplicationFormConfigurationException
import io.cloudflight.jems.server.call.service.update_application_form_configuration.UpdateApplicationFormConfigurationInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ApplicationFormConfigurationControllerTest: UnitTest() {

    companion object {
        private const val ID = 1L

        private val configDTO = ApplicationFormConfigurationDTO(
            id = ID,
            name = "name",
            fieldConfigurations = mutableSetOf(
                ApplicationFormFieldConfigurationDTO(
                    id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                    visibilityStatus = FieldVisibilityStatusDTO.STEP_ONE_AND_TWO,
                    validVisibilityStatusSet = mutableSetOf(FieldVisibilityStatusDTO.STEP_ONE_AND_TWO)
                ),
                ApplicationFormFieldConfigurationDTO(
                    id = ApplicationFormFieldSetting.PROJECT_RESULTS_DELIVERY_PERIOD.id,
                    visibilityStatus = FieldVisibilityStatusDTO.NONE,
                    validVisibilityStatusSet = mutableSetOf(
                        FieldVisibilityStatusDTO.NONE,
                        FieldVisibilityStatusDTO.STEP_TWO_ONLY,
                        FieldVisibilityStatusDTO.STEP_ONE_AND_TWO
                    )
                )
            )
        )

        private val updateConfigDTO = UpdateApplicationFormConfigurationRequestDTO(
            id = ID,
            name = "name",
            fieldConfigurations = mutableSetOf(
                UpdateApplicationFormFieldConfigurationRequestDTO(
                    id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                    visibilityStatus = FieldVisibilityStatusDTO.STEP_ONE_AND_TWO
                ),
                UpdateApplicationFormFieldConfigurationRequestDTO(
                    id = ApplicationFormFieldSetting.PROJECT_RESULTS_DELIVERY_PERIOD.id,
                    visibilityStatus = FieldVisibilityStatusDTO.NONE
                )
            )
        )

        private val configModel = ApplicationFormConfiguration(
            id = ID,
            name = "name",
            fieldConfigurations = mutableSetOf(
                ApplicationFormFieldConfiguration(
                    id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                    visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
                ),
                ApplicationFormFieldConfiguration(
                    id = ApplicationFormFieldSetting.PROJECT_RESULTS_DELIVERY_PERIOD.id,
                    visibilityStatus = FieldVisibilityStatus.NONE
                )
            )
        )
    }

    @MockK
    lateinit var getApplicationFormConfiguration: GetApplicationFormConfigurationInteractor

    @MockK
    lateinit var listApplicationFormConfiguration: ListApplicationFormConfigurationInteractor

    @MockK
    lateinit var updateApplicationFormConfiguration: UpdateApplicationFormConfigurationInteractor

    @InjectMockKs
    private lateinit var controller: ApplicationFormConfigurationController

    @Test
    fun `get applicationFormConfiguration by id`() {
        every { getApplicationFormConfiguration.get(ID) } returns configModel
        assertThat(controller.getById(ID)).isEqualTo(configDTO)
    }

    @Test
    fun `get applicationFormConfiguration fails on get exception`() {
        val exception = GetApplicationFormConfigurationException(Exception())
        every { getApplicationFormConfiguration.get(ID) } throws exception
        assertThrows<GetApplicationFormConfigurationException> { controller.getById(ID) }
    }

    @Test
    fun `list applicationFormConfigurations`() {
        val configSummary = ApplicationFormConfigurationSummary(
            id = ID,
            name = "name"
        )
        val configSummaryDTO = ApplicationFormConfigurationSummaryDTO(
            id = ID,
            name = "name"
        )
        every { listApplicationFormConfiguration.list() } returns listOf(configSummary)
        assertThat(controller.list()).containsExactly(configSummaryDTO)
    }

    @Test
    fun `list applicationFormConfigurations fails on list exception`() {
        val exception = ListApplicationFormConfigurationsException(Exception())
        every { listApplicationFormConfiguration.list() } throws exception
        assertThrows<ListApplicationFormConfigurationsException> { controller.list() }
    }

    @Test
    fun `update applicationFormConfiguration`() {
        val capturedConfigModel = slot<ApplicationFormConfiguration>()
        every { updateApplicationFormConfiguration.update(capture(capturedConfigModel)) } returns Unit
        controller.update(updateConfigDTO)
        verify { updateApplicationFormConfiguration.update(configModel) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }

    @Test
    fun `update applicationFormConfiguration fails on update exception`() {
        val exception = UpdateApplicationFormConfigurationException(Exception())
        val capturedConfigModel = slot<ApplicationFormConfiguration>()
        every { updateApplicationFormConfiguration.update(capture(capturedConfigModel)) } throws exception

        assertThrows<UpdateApplicationFormConfigurationException> { controller.update(updateConfigDTO) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }
}
