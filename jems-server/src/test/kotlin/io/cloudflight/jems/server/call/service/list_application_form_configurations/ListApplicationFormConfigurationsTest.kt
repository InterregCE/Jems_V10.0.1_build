package io.cloudflight.jems.server.call.service.list_application_form_configurations

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.ApplicationFormConfigurationSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListApplicationFormConfigurationsTest: UnitTest() {

    companion object {
        private const val AF_CONFIG_ID = 1L

        private val applicationFormConfigurationSummary = ApplicationFormConfigurationSummary(
            id = AF_CONFIG_ID,
            name = "name"
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @InjectMockKs
    private lateinit var listApplicationFormConfigurations: ListApplicationFormConfigurations

    @Test
    fun `list application form configurations`() {
        every { persistence.listApplicationFormConfigurations() } returns listOf(applicationFormConfigurationSummary)
        assertThat(listApplicationFormConfigurations.list()).containsExactly(applicationFormConfigurationSummary)
    }

    @Test
    fun `list application form configurations - none found`() {
        every { persistence.listApplicationFormConfigurations() } returns emptyList()
        assertThat(listApplicationFormConfigurations.list()).isEmpty()
    }
}
