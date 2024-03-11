package io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
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
                id = NotificationType.ProjectSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = true,
                sendToProjectPartners = true,
                sendToProjectAssigned = true,
                sendToControllers = true,
            )
        )

        private val expectedProjectNotificationConfigurations = listOf(
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = true,
                sendToProjectPartners = true,
                sendToProjectAssigned = true,
                sendToControllers = true,
            ),
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectSubmittedStep1,
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
    private lateinit var interactor: GetProjectNotificationConfigurations

    @Test
    fun `get application form field configurations`() {
        every { persistence.getProjectNotificationConfigurations(CALL_ID) } returns projectNotificationConfigurations
        val notifications = interactor.get(CALL_ID)

        assertThat(notifications.map { it.id }).containsExactly(
            NotificationType.ProjectSubmittedStep1,
            NotificationType.ProjectSubmitted,
            NotificationType.ProjectApprovedStep1,
            NotificationType.ProjectApprovedWithConditionsStep1,
            NotificationType.ProjectIneligibleStep1,
            NotificationType.ProjectNotApprovedStep1,
            NotificationType.ProjectApproved,
            NotificationType.ProjectApprovedWithConditions,
            NotificationType.ProjectIneligible,
            NotificationType.ProjectNotApproved,
            NotificationType.ProjectReturnedToApplicant,
            NotificationType.ProjectResubmitted,
            NotificationType.ProjectReturnedForConditions,
            NotificationType.ProjectConditionsSubmitted,
            NotificationType.ProjectContracted,
            NotificationType.ProjectInModification,
            NotificationType.ProjectModificationSubmitted,
            NotificationType.ProjectModificationApproved,
            NotificationType.ProjectModificationRejected,
            NotificationType.SharedFolderFileUpload,
            NotificationType.SharedFolderFileDelete,
            NotificationType.ProjectClosed,
        )

        assertThat(notifications).containsAll(expectedProjectNotificationConfigurations)
    }
}
