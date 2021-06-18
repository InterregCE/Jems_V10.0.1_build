package io.cloudflight.jems.server.call.service.get_application_form_configuration

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.repository.ApplicationFormConfigurationNotFound
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetApplicationFormConfigurationTest: UnitTest() {

    companion object {
        private const val AF_CONFIG_ID = 2L

        private val applicationFormConfiguration = ApplicationFormConfiguration(
            id = AF_CONFIG_ID,
            name = "name",
            fieldConfigurations = mutableSetOf(
                ApplicationFormFieldConfiguration(
                    id = ApplicationFormFieldSetting.PROJECT_ACRONYM.id,
                    visibilityStatus = FieldVisibilityStatus.STEP_ONE_AND_TWO
                )
            )
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @InjectMockKs
    private lateinit var gettApplicationFormConfiguration: GetApplicationFormConfiguration

    @Test
    fun `get application form configuration`() {
        every { persistence.getApplicationFormConfiguration(AF_CONFIG_ID) } returns applicationFormConfiguration
        assertThat(gettApplicationFormConfiguration.get(AF_CONFIG_ID)).isEqualTo(applicationFormConfiguration)
    }

    @Test
    fun `get application form configuration - not found`() {
        every { persistence.getApplicationFormConfiguration(AF_CONFIG_ID) } throws ApplicationFormConfigurationNotFound()
        assertThrows<ApplicationFormConfigurationNotFound> { (gettApplicationFormConfiguration.get(AF_CONFIG_ID)) }
    }
}
