package io.cloudflight.jems.server.call.controller

import io.cloudflight.jems.api.call.dto.notificationConfiguration.ProjectNotificationConfigurationDTO
import io.cloudflight.jems.api.notification.dto.NotificationTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.notificationConfigurations.ProjectNotificationConfiguration
import io.cloudflight.jems.server.call.service.notificationConfigurations.getPartnerReportNotificationConfiguration.GetPartnerReportNotificationConfigurationsException
import io.cloudflight.jems.server.call.service.notificationConfigurations.getPartnerReportNotificationConfiguration.GetPartnerReportNotificationConfigurationsInteractor
import io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration.GetProjectNotificationConfigurationException
import io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectNotificationConfiguration.GetProjectNotificationConfigurationsInteractor
import io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectReportNotificationConfiguration.GetProjectReportNotificationConfigurationsException
import io.cloudflight.jems.server.call.service.notificationConfigurations.getProjectReportNotificationConfiguration.GetProjectReportNotificationConfigurationsInteractor
import io.cloudflight.jems.server.call.service.notificationConfigurations.updatePartnerReportNotificationConfiguration.UpdatePartnerReportNotificationConfigurationsException
import io.cloudflight.jems.server.call.service.notificationConfigurations.updatePartnerReportNotificationConfiguration.UpdatePartnerReportNotificationConfigurationsInteractor
import io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectNotificationConfiguration.UpdateProjectNotificationConfigurationsException
import io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectNotificationConfiguration.UpdateProjectNotificationConfigurationsInteractor
import io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectReportNotificationConfiguration.UpdateProjectReportNotificationConfigurationsException
import io.cloudflight.jems.server.call.service.notificationConfigurations.updateProjectReportNotificationConfiguration.UpdateProjectReportNotificationConfigurationsInteractor
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CallNotificationConfigurationControllerTest : UnitTest() {

    companion object {
        private const val ID = 1L

        private val configDTO = listOf(
            ProjectNotificationConfigurationDTO(
                id = NotificationTypeDTO.ProjectSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false
            ),
            ProjectNotificationConfigurationDTO(
                id = NotificationTypeDTO.ProjectSubmittedStep1,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false
            )
        )

        private val updateConfigDTO = listOf(
            ProjectNotificationConfigurationDTO(
                id = NotificationTypeDTO.ProjectSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false
            ),
            ProjectNotificationConfigurationDTO(
                id = NotificationTypeDTO.ProjectSubmittedStep1,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false
            )
        )

        private val configModel = listOf(
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectSubmitted,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false
            ),
            ProjectNotificationConfiguration(
                id = NotificationType.ProjectSubmittedStep1,
                active = true,
                sendToManager = true,
                sendToLeadPartner = false,
                sendToProjectPartners = false,
                sendToProjectAssigned = false,
                sendToControllers = false
            )
        )
    }

    @MockK
    lateinit var getProjectNotificationConfigurationsInteractor: GetProjectNotificationConfigurationsInteractor

    @MockK
    lateinit var updateProjectNotificationConfigurationsInteractor: UpdateProjectNotificationConfigurationsInteractor

    @MockK
    lateinit var getPartnerReportNotificationConfigurationsInteractor: GetPartnerReportNotificationConfigurationsInteractor

    @MockK
    lateinit var updatePartnerReportNotificationConfigurationsInteractor: UpdatePartnerReportNotificationConfigurationsInteractor

    @MockK
    lateinit var getProjectReportNotificationConfigurationsInteractor: GetProjectReportNotificationConfigurationsInteractor

    @MockK
    lateinit var updateProjectReportNotificationConfigurationsInteractor: UpdateProjectReportNotificationConfigurationsInteractor


    @InjectMockKs
    private lateinit var controller: CallNotificationConfigurationController

    @Test
    fun `get callNotificationConfigurations by id`() {
        every { getProjectNotificationConfigurationsInteractor.get(ID) } returns configModel
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

    @Test
    fun `get partnerReport callNotificationConfigurations by id`() {
        every { getPartnerReportNotificationConfigurationsInteractor.get(ID) } returns configModel
        assertThat(controller.getPartnerReportNotificationsByCallId(ID)).isEqualTo(configDTO)
    }

    @Test
    fun `get partnerReport callNotificationConfigurations fails on get exception`() {
        val exception = GetPartnerReportNotificationConfigurationsException(Exception())
        every { getPartnerReportNotificationConfigurationsInteractor.get(ID) } throws exception
        assertThrows<GetPartnerReportNotificationConfigurationsException> { controller.getPartnerReportNotificationsByCallId(ID) }
    }

    @Test
    fun `update partnerReport callNotificationConfigurations`() {
        val capturedConfigModel = slot<List<ProjectNotificationConfiguration>>()
        every { updatePartnerReportNotificationConfigurationsInteractor.update(ID, capture(capturedConfigModel)) } returns listOf()
        controller.updatePartnerReportNotifications(ID, updateConfigDTO)
        verify { updatePartnerReportNotificationConfigurationsInteractor.update(ID, configModel) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }

    @Test
    fun `update partnerReport callNotificationConfigurations fails on update exception`() {
        val exception = UpdatePartnerReportNotificationConfigurationsException(Exception())
        val capturedConfigModel = slot<List<ProjectNotificationConfiguration>>()
        every { updatePartnerReportNotificationConfigurationsInteractor.update(ID, capture(capturedConfigModel)) } throws exception

        assertThrows<UpdatePartnerReportNotificationConfigurationsException> { controller.updatePartnerReportNotifications(ID, updateConfigDTO) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }

    @Test
    fun `get projectReport callNotificationConfigurations by id`() {
        every { getProjectReportNotificationConfigurationsInteractor.get(ID) } returns configModel
        assertThat(controller.getProjectReportNotificationsByCallId(ID)).isEqualTo(configDTO)
    }

    @Test
    fun `get projectReport callNotificationConfigurations fails on get exception`() {
        val exception = GetProjectReportNotificationConfigurationsException(Exception())
        every { getProjectReportNotificationConfigurationsInteractor.get(ID) } throws exception
        assertThrows<GetProjectReportNotificationConfigurationsException> { controller.getProjectReportNotificationsByCallId(ID) }
    }

    @Test
    fun `update projectReport callNotificationConfigurations`() {
        val capturedConfigModel = slot<List<ProjectNotificationConfiguration>>()
        every { updateProjectReportNotificationConfigurationsInteractor.update(ID, capture(capturedConfigModel)) } returns listOf()
        controller.updateProjectReportNotifications(ID, updateConfigDTO)
        verify { updateProjectReportNotificationConfigurationsInteractor.update(ID, configModel) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }

    @Test
    fun `update projectReport callNotificationConfigurations fails on update exception`() {
        val exception = UpdateProjectReportNotificationConfigurationsException(Exception())
        val capturedConfigModel = slot<List<ProjectNotificationConfiguration>>()
        every { updateProjectReportNotificationConfigurationsInteractor.update(ID, capture(capturedConfigModel)) } throws exception

        assertThrows<UpdateProjectReportNotificationConfigurationsException> { controller.updateProjectReportNotifications(ID, updateConfigDTO) }
        assertThat(capturedConfigModel.captured).isEqualTo(configModel)
    }

}
