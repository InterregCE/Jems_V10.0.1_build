package io.cloudflight.jems.server.call.service.get_project_notification_configuration

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration.GetProjectNotificationConfigurations
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetProjectNotificationConfigurationsTest: UnitTest() {

    companion object {
        private const val CALL_ID = 2L

        private val projectNotificationConfigurations = listOf(
            ProjectNotificationConfiguration(
                id = ApplicationStatus.SUBMITTED,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
            )
        )

        private val expectedProjectNotificationConfigurations = listOf(
            ProjectNotificationConfiguration(
                id = ApplicationStatus.SUBMITTED,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
            ),
            ProjectNotificationConfiguration(
                id = ApplicationStatus.STEP1_SUBMITTED,
                active = false,
                sendToManager = false,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
            )
        )
    }

    @MockK
    lateinit var callNotificationConfigurationsPersistence: CallNotificationConfigurationsPersistence

    @InjectMockKs
    private lateinit var getProjectNotificationConfigurations: GetProjectNotificationConfigurations

    @Test
    fun `get application form field configurations`() {
        every { callNotificationConfigurationsPersistence.getProjectNotificationConfigurations(CALL_ID) } returns projectNotificationConfigurations
        assertThat(getProjectNotificationConfigurations.get(CALL_ID))
            .containsAll(expectedProjectNotificationConfigurations)
    }
}
