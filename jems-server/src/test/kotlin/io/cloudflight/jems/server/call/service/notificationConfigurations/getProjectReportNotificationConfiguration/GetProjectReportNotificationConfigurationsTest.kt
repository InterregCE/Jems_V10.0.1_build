package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectReportNotificationConfiguration

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetProjectReportNotificationConfigurationsTest: UnitTest() {

    companion object {
        private const val CALL_ID = 4L

        private val projectReportNotificationConfigurations = listOf(
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectReportSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = true,
                sendToProjectPartners = true,
                sendToProjectAssigned = true,
                sendToControllers = true,
            )
        )

        private val expectedProjectReportNotificationConfigurations = listOf(
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectReportSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = true,
                sendToProjectPartners = true,
                sendToProjectAssigned = true,
                sendToControllers = true,
            ),
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectReportVerificationOngoing,
                active = false,
                sendToManager = false,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false,
            )
        )
    }

    @MockK
    private lateinit var persistence: CallNotificationConfigurationsPersistence

    @InjectMockKs
    private lateinit var interactor: GetProjectReportNotificationConfigurations

    @Test
    fun `get application form field configurations`() {
        every { persistence.getProjectNotificationConfigurations(CALL_ID) } returns projectReportNotificationConfigurations
        val notifications = interactor.get(CALL_ID)

        assertThat(notifications.map { it.id }).containsExactly(
            NotificationType.ProjectReportSubmitted,
            NotificationType.ProjectReportVerificationOngoing,
            NotificationType.ProjectReportVerificationFinalized,
        )

        assertThat(notifications).containsAll(expectedProjectReportNotificationConfigurations)
    }
}
