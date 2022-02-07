package io.cloudflight.jems.server.call.service.get_application_form_field_configurations

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.repository.ApplicationFormConfigurationNotFound
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallApplicationFormFieldsConfiguration
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetApplicationFormFieldConfigurationsTest: UnitTest() {

    companion object {
        private const val CALL_ID = 2L

        private val applicationFormFieldConfigurations = mutableSetOf(
            ApplicationFormFieldConfiguration(
                id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
            )
        )

        private val callApplicationFormFieldConfiguration = CallApplicationFormFieldsConfiguration(
            CallType.STANDARD,
            applicationFormFieldConfigurations
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @InjectMockKs
    private lateinit var getApplicationFormFieldConfigurations: GetApplicationFormFieldConfigurations

    @Test
    fun `get application form field configurations`() {
        every { persistence.getApplicationFormFieldConfigurations(CALL_ID) } returns callApplicationFormFieldConfiguration
        assertThat(getApplicationFormFieldConfigurations.get(CALL_ID).applicationFormFieldConfigurations)
            .isEqualTo(applicationFormFieldConfigurations)
    }

    @Test
    fun `get application form field configurations - not found`() {
        every { persistence.getApplicationFormFieldConfigurations(CALL_ID) } throws ApplicationFormConfigurationNotFound()
        assertThrows<ApplicationFormConfigurationNotFound> { (getApplicationFormFieldConfigurations.get(CALL_ID)) }
    }
}
