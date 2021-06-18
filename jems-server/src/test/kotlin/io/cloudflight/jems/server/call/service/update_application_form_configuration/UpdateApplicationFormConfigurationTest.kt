package io.cloudflight.jems.server.call.service.update_application_form_configuration

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateApplicationFormConfigurationTest: UnitTest() {

    companion object {
        private const val AF_CONFIG_ID = 1L

        private val applicationFormConfiguration = ApplicationFormConfiguration(
            id = AF_CONFIG_ID,
            name = "name",
            fieldConfigurations = mutableSetOf(
                ApplicationFormFieldConfiguration(
                    id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                    visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
                ),
                ApplicationFormFieldConfiguration(
                    id = ApplicationFormFieldSetting.PROJECT_TITLE.id,
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
    lateinit var persistence: CallPersistence

    @InjectMockKs
    private lateinit var updateApplicationFormConfiguration: UpdateApplicationFormConfiguration

    @Test
    fun `update application form configuration`() {
        every { persistence.updateApplicationFormConfigurations(applicationFormConfiguration) } returns Unit

        updateApplicationFormConfiguration.update(applicationFormConfiguration)
    }

    @Test
    fun `update application form configuration - fail on invalid`() {
        val invalidApplicationFormConfiguration = ApplicationFormConfiguration(
            id = AF_CONFIG_ID,
            name = "name",
            fieldConfigurations = mutableSetOf(
                ApplicationFormFieldConfiguration(
                    id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                    visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
                ),
                ApplicationFormFieldConfiguration(
                    id = "test",
                    visibilityStatus = FieldVisibilityStatus.NONE
                )
            )
        )
        every { persistence.updateApplicationFormConfigurations(invalidApplicationFormConfiguration) } returns Unit

        assertThrows<InvalidFieldStatusException> { updateApplicationFormConfiguration.update(invalidApplicationFormConfiguration) }
    }
}
