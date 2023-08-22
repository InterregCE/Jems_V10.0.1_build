package io.cloudflight.jems.server.call.service.notificationConfigurations.updatePartnerReportNotificationConfiguration

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdatePartnerReportNotificationConfigurationsTest : UnitTest() {
    companion object {
        private const val CALL_ID = 1L

        private val partnerReportNotificationConfig: List<ProjectNotificationConfiguration> = listOf(
            ProjectNotificationConfiguration(
                id = NotificationType.PartnerReportSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false,
            ),
            ProjectNotificationConfiguration(
                id = NotificationType.PartnerReportReOpen,
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
    private lateinit var updatePartnerReportNotificationConfiguration: UpdatePartnerReportNotificationConfigurations

    @Test
    fun `partner report - update application form field configuration`() {
        every {
            persistence.saveProjectNotificationConfigurations(CALL_ID, any())
        } returnsArgument 1

        val result = updatePartnerReportNotificationConfiguration.update(CALL_ID, partnerReportNotificationConfig)

        assertThat(result).isEqualTo(partnerReportNotificationConfig)
        verify(exactly = 1) {
            persistence.saveProjectNotificationConfigurations(
                CALL_ID,
                partnerReportNotificationConfig
            )
        }
    }
}
