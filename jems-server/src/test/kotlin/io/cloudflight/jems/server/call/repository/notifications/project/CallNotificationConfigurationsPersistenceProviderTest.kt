package io.cloudflight.jems.server.call.repository.notifications.project

import io.cloudflight.jems.api.call.dto.notificationConfiguration.ProjectNotificationConfigurationDTO
import io.cloudflight.jems.api.notification.dto.NotificationTypeDTO
import io.cloudflight.jems.server.call.controller.toDto
import io.cloudflight.jems.server.call.entity.notificationConfiguration.ProjectNotificationConfigurationEntity
import io.cloudflight.jems.server.call.entity.notificationConfiguration.ProjectNotificationConfigurationId
import io.cloudflight.jems.server.call.repository.CallPersistenceProviderTest.Companion.callEntity
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.call.repository.toNotificationEntity
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class CallNotificationConfigurationsPersistenceProviderTest {

    companion object {
        private const val CALL_ID = 2L
    }

    @MockK
    private lateinit var projectNotificationConfigurationRepository: ProjectNotificationConfigurationRepository

    @MockK
    private lateinit var callRepository: CallRepository


    @InjectMockKs
    private lateinit var persistence: CallNotificationConfigurationsPersistenceProvider

    @Test
    fun `should save set of project notification configurations for the call`() {
        val callEntity = callEntity(CALL_ID)
        val newConfigs = listOf(
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false
            )
        )
        val expectedConfigs = mutableSetOf(
            ProjectNotificationConfigurationDTO(
                id = NotificationTypeDTO.ProjectSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false,
            )
        )
        every { callRepository.getReferenceById(CALL_ID) } returns callEntity
        every {
            projectNotificationConfigurationRepository
                .saveAll(any<MutableSet<ProjectNotificationConfigurationEntity>>())
        } returns newConfigs.toNotificationEntity(
            callEntity
        ).toList()

        assertThat(
            persistence.saveProjectNotificationConfigurations(
                CALL_ID,
                newConfigs
            ).toDto()
        ).containsAll(expectedConfigs)
    }

    @Test
    fun `should return set of project notification configurations for the call`() {
        val callEntity = callEntity(CALL_ID)
        val configEntity = ProjectNotificationConfigurationEntity(
            ProjectNotificationConfigurationId(
                NotificationType.ProjectSubmitted, callEntity(CALL_ID)
            ), active = true,
            sendToManager = true,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            sendToControllers = false,
            emailSubject = "",
            emailBody = ""
        )
        val expectedConfig = ProjectNotificationConfigurationDTO(
            id = NotificationTypeDTO.ProjectSubmitted,
            active = true,
            sendToManager = true,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            sendToControllers = false
        )
        every { callRepository.getReferenceById(CALL_ID) } returns callEntity
        every { projectNotificationConfigurationRepository.findByIdCallEntityId(CALL_ID) } returns listOf(configEntity)
        assertThat(persistence.getProjectNotificationConfigurations(CALL_ID).toDto())
            .containsExactly(expectedConfig)
    }


    @Test
    fun `should return notification configurations`() {
        every {
            projectNotificationConfigurationRepository.findByActiveTrueAndIdCallEntityIdAndIdId(
                CALL_ID,
                NotificationType.ProjectSubmitted,
            )
        } returns
                ProjectNotificationConfigurationEntity(
                    ProjectNotificationConfigurationId(
                        NotificationType.ProjectSubmitted, callEntity(CALL_ID)
                    ), active = true,
                    sendToManager = true,
                    sendToLeadPartner = false,
                    sendToProjectPartners = false,
                    sendToProjectAssigned = false,
                    sendToControllers = false,
                    emailSubject = "sub",
                    emailBody = "body"
                )

        assertThat(persistence.getActiveNotificationOfType(CALL_ID, NotificationType.ProjectSubmitted)).isEqualTo(
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false,
                emailSubject = "sub",
                emailBody = "body",
            )
        )
    }

}
