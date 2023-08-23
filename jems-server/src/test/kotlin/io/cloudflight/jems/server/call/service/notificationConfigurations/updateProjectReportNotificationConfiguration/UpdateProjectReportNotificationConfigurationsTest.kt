package io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectReportNotificationConfiguration

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class UpdateProjectReportNotificationConfigurationsTest : UnitTest() {

    companion object {
        private const val CALL_ID = 1L

        private val projectReportNotificationConfiguration: List<ProjectNotificationConfiguration> = listOf(
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectReportSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false,
            ),
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectReportVerificationOngoing,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false,
            ),
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectReportVerificationFinalized,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false,
            ),
        )
    }

    @MockK
    private lateinit var persistence: CallNotificationConfigurationsPersistence

    @InjectMockKs
    private lateinit var updateProjectReportNotificationConfiguration: UpdateProjectReportNotificationConfigurations

    @Test
    fun `project report - update application form field configuration`() {
        every {
            persistence.saveProjectNotificationConfigurations(CALL_ID, any())
        } returnsArgument 1

        val result = updateProjectReportNotificationConfiguration.update(
            CALL_ID,
            projectReportNotificationConfiguration
        )

        Assertions.assertThat(result)
            .isEqualTo(projectReportNotificationConfiguration)
        verify(exactly = 1) {
            persistence.saveProjectNotificationConfigurations(
                CALL_ID, projectReportNotificationConfiguration
            )
        }
    }

}
