package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.call.dto.notificationConfiguration.ProjectNotificationConfigurationDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.callFundRate
import io.cloudflight.jems.server.call.service.get_project_notification_configuration.GetProjectNotificationConfigurationException
import io.cloudflight.jems.server.call.service.get_project_notification_configuration.GetProjectNotificationConfigurationsInteractor
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.update_project_notification_configurations.UpdateProjectNotificationConfigurationsException
import io.cloudflight.jems.server.call.service.update_project_notification_configurations.UpdateProjectNotificationConfigurationsInteractor
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime

class CallNotificationConfigurationControllerTest : UnitTest() {

    companion object {
        private const val ID = 1L

        private val configDTO = listOf(
            ProjectNotificationConfigurationDTO(
                id = ApplicationStatusDTO.SUBMITTED,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                emailBody = null,
                emailSubject = null
            ),
            ProjectNotificationConfigurationDTO(
                id = ApplicationStatusDTO.STEP1_SUBMITTED,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                emailBody = null,
                emailSubject = null
            )
        )

        private val updateConfigDTO = listOf(
            ProjectNotificationConfigurationDTO(
                id = ApplicationStatusDTO.SUBMITTED,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                emailBody = null,
                emailSubject = null
            ),
            ProjectNotificationConfigurationDTO(
                id = ApplicationStatusDTO.STEP1_SUBMITTED,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                emailBody = null,
                emailSubject = null
            )
        )

        private val configModel = listOf(
            ProjectNotificationConfiguration(
                id = ApplicationStatus.SUBMITTED,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                emailBody = null,
                emailSubject = null
            ),
            ProjectNotificationConfiguration(
                id = ApplicationStatus.STEP1_SUBMITTED,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                emailBody = null,
                emailSubject = null
            )
        )
    }

    @MockK
    lateinit var getProjectNotificationConfigurationsInteractor: GetProjectNotificationConfigurationsInteractor

    @MockK
    lateinit var updateProjectNotificationConfigurationsInteractor: UpdateProjectNotificationConfigurationsInteractor

    @InjectMockKs
    private lateinit var controller: CallNotificationConfigurationController

    @Test
    fun `get callNotificationConfigurations by id`() {
        every { getProjectNotificationConfigurationsInteractor.get(ID)} returns configModel
        assertThat(controller.getProjectNotificationsByCallId(ID)).isEqualTo(configDTO)
    }

    @Test
    fun `get callNotificationConfigurations fails on get exception`() {
        val exception = GetProjectNotificationConfigurationException(Exception())
        every { getProjectNotificationConfigurationsInteractor.get(ID) } throws exception
        assertThrows<GetProjectNotificationConfigurationException> { controller.getProjectNotificationsByCallId(ID) }
    }


    @Test
    fun `update callNotificationConfigurations`() {
        val capturedConfigModel = slot<List<ProjectNotificationConfiguration>>()
        every { updateProjectNotificationConfigurationsInteractor.update(ID, capture(capturedConfigModel)) } returns listOf()
        controller.updateProjectNotifications(ID, updateConfigDTO)
        verify { updateProjectNotificationConfigurationsInteractor.update(ID, configModel) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }

    @Test
    fun `update callNotificationConfigurations fails on update exception`() {
        val exception = UpdateProjectNotificationConfigurationsException(Exception())
        val capturedConfigModel = slot<List<ProjectNotificationConfiguration>>()
        every { updateProjectNotificationConfigurationsInteractor.update(ID, capture(capturedConfigModel)) } throws exception

        assertThrows<UpdateProjectNotificationConfigurationsException> { controller.updateProjectNotifications(ID, updateConfigDTO) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }
}
