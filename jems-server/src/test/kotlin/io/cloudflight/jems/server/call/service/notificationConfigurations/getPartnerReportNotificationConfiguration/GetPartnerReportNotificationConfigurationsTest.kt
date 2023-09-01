package io.cloudflight.jems.server.call.service.notificationConfigurations.getPartnerReportNotificationConfiguration

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetPartnerReportNotificationConfigurationsTest: UnitTest() {

    companion object {
        private const val CALL_ID = 3L

        private val partnerReportNotificationConfigurations = listOf(
            ProjectNotificationConfiguration(
                id = NotificationType.PartnerReportSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = true,
                sendToProjectPartners = true,
                sendToProjectAssigned = true,
                sendToControllers = true,
            )
        )

        private val expectedPartnerReportNotificationConfigurations = listOf(
            ProjectNotificationConfiguration(
                id = NotificationType.PartnerReportSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = true,
                sendToProjectPartners = true,
                sendToProjectAssigned = true,
                sendToControllers = true,
            ),
            ProjectNotificationConfiguration(
                id = NotificationType.PartnerReportReOpen,
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
    private lateinit var interactor: GetPartnerReportNotificationConfigurations

    @Test
    fun `get application form field configurations`() {
        every { persistence.getProjectNotificationConfigurations(CALL_ID) } returns partnerReportNotificationConfigurations
        val notifications = interactor.get(CALL_ID)

        assertThat(notifications.map { it.id }).containsExactly(
            NotificationType.PartnerReportSubmitted,
            NotificationType.PartnerReportReOpen,
            NotificationType.PartnerReportControlOngoing,
            NotificationType.PartnerReportCertified,
            NotificationType.PartnerReportReOpenCertified,
            NotificationType.ControlCommunicationFileUpload,
            NotificationType.ControlCommunicationFileDelete,
        )

        assertThat(notifications).containsAll(expectedPartnerReportNotificationConfigurations)
    }
}
