package io.cloudflight.jems.server.call.service.update_project_notification_configurations

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.CallNotificationConfigurationsPersistence
import io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectNotificationConfigurations.UpdateProjectNotificationConfiguration
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateProjectNotificationConfigurationsTest : UnitTest() {

    private val CALL_ID = 1L
    private val projectNotificationConfigStandard: List<ProjectNotificationConfiguration> = listOf(
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
            active = true,
            sendToManager = true,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
        )
    )

    @MockK
    lateinit var callNotificationConfigurationsPersistence: CallNotificationConfigurationsPersistence

    @InjectMockKs
    private lateinit var updateProjectNotificationConfiguration: UpdateProjectNotificationConfiguration

    @Test
    fun `update application form field configuration`() {
        every {
            callNotificationConfigurationsPersistence.saveProjectNotificationConfigurations(
                CALL_ID,
                projectNotificationConfigStandard
            )
        } returns projectNotificationConfigStandard

        val result = updateProjectNotificationConfiguration.update(CALL_ID, projectNotificationConfigStandard)

        assertThat(result).isEqualTo(projectNotificationConfigStandard)
    }
}
