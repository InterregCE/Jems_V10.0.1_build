package io.cloudflight.jems.server.call.repository.notifications.project

import io.cloudflight.jems.api.call.dto.notificationConfiguration.ProjectNotificationConfigurationDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.call.controller.toDto
import io.cloudflight.jems.server.call.entity.ProjectNotificationConfigurationEntity
import io.cloudflight.jems.server.call.entity.ProjectNotificationConfigurationId
import io.cloudflight.jems.server.call.repository.CallPersistenceProviderTest.Companion.callEntity
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.call.repository.toModel
import io.cloudflight.jems.server.call.repository.toNotificationEntities
import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
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
                   id = ApplicationStatus.SUBMITTED,
                   active = true,
                   sendToManager = true,
                   sendToLeadPartner = false,
                   sendToProjectPartners = false,
                   sendToProjectAssigned = false,
                   emailBody = null,
                   emailSubject = null
               )
           )
           val expectedConfigs = mutableSetOf(
               ProjectNotificationConfigurationDTO(
                   id = ApplicationStatusDTO.SUBMITTED,
                   active = true,
                   sendToManager = true,
                   sendToLeadPartner = false,
                   sendToProjectPartners = false,
                   sendToProjectAssigned = false,
                   emailBody = null,
                   emailSubject = null
               )
           )
           every { callRepository.getById(CALL_ID) } returns callEntity
           every { projectNotificationConfigurationRepository
               .saveAll(any<MutableSet<ProjectNotificationConfigurationEntity>>()) } returns newConfigs.toNotificationEntities(
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
                ApplicationStatus.SUBMITTED, callEntity(CALL_ID)
            ), active = true,
            sendToManager = true,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            emailBody = null,
            emailSubject = null
        )
        val expectedConfig = ProjectNotificationConfigurationDTO(
            id = ApplicationStatusDTO.SUBMITTED,
            active = true,
            sendToManager = true,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            emailBody = null,
            emailSubject = null
        )
        every { callRepository.getById(CALL_ID)} returns callEntity
        every { projectNotificationConfigurationRepository.findByIdCallEntityId(CALL_ID) } returns mutableSetOf(configEntity)
        assertThat(persistence.getProjectNotificationConfigurations(CALL_ID).toDto())
            .containsExactly(expectedConfig)
    }


    @Test
    fun `should return notification configurations`() {
        val callEntity = callEntity(CALL_ID)
        val configEntity = ProjectNotificationConfigurationEntity(
            ProjectNotificationConfigurationId(
                ApplicationStatus.SUBMITTED, callEntity(CALL_ID)
            ), active = true,
            sendToManager = true,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            emailBody = null,
            emailSubject = null
        )
        every { callRepository.getById(CALL_ID)} returns callEntity
        every {
            projectNotificationConfigurationRepository.findByActiveTrueAndId(
                ProjectNotificationConfigurationId(
                    ApplicationStatus.SUBMITTED,
                    callEntity
                )
            )
        } returns
            ProjectNotificationConfigurationEntity(
                ProjectNotificationConfigurationId(
                    ApplicationStatus.SUBMITTED, callEntity(CALL_ID)
                ), active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                emailBody = null,
                emailSubject = null
            )

        assertThat(persistence.getActiveNotificationOfType(CALL_ID, ApplicationStatus.SUBMITTED)).isEqualTo(ProjectNotificationConfigurationEntity(
            ProjectNotificationConfigurationId(
                ApplicationStatus.SUBMITTED, callEntity(CALL_ID)
            ), active = true,
            sendToManager = true,
            sendToLeadPartner = false,
            sendToProjectPartners = false,
            sendToProjectAssigned = false,
            emailBody = null,
            emailSubject = null
        ).toModel())
    }

}