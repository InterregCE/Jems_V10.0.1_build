package io.cloudflight.jems.server.call.service.update_project_notification_configurations

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectNotificationConfiguration.UpdateProjectNotificationConfigurations
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateProjectNotificationConfigurationsTest : UnitTest() {

    private val CALL_ID = 1L
    private val projectNotificationConfigStandard: List<ProjectNotificationConfiguration> = listOf(
        ProjectNotificationConfiguration(
            id = NotificationType.ProjectSubmitted,
            active = true,
            sendToManager = true,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            sendToControllers = false,
        ),
        ProjectNotificationConfiguration(
            id = NotificationType.ProjectSubmittedStep1,
            active = true,
            sendToManager = true,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            sendToControllers = false,
        )
    )

    @MockK
    lateinit var callNotificationConfigurationsPersistence: CallNotificationConfigurationsPersistence

    @InjectMockKs
    private lateinit var updateProjectNotificationConfigurations: UpdateProjectNotificationConfigurations

    @Test
    fun `update application form field configuration`() {
        every {
            callNotificationConfigurationsPersistence.saveProjectNotificationConfigurations(CALL_ID, any())
        } returnsArgument 1

        val result = updateProjectNotificationConfigurations.update(CALL_ID, projectNotificationConfigStandard)

        assertThat(result).isEqualTo(projectNotificationConfigStandard)
        verify(exactly = 1) { callNotificationConfigurationsPersistence.saveProjectNotificationConfigurations(CALL_ID, projectNotificationConfigStandard) }
    }

}
