package io.cloudflight.jems.server.call.service.update_project_notification_configurations

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.call.service.model.ProjectNotificationConfiguration
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

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

    private val callDetail = CallDetail(
        id = CALL_ID,
        name = "call name",
        status = CallStatus.DRAFT,
        type = CallType.STANDARD,
        startDate = ZonedDateTime.now().minusDays(1),
        endDateStep1 = null,
        endDate = ZonedDateTime.now().plusDays(1),
        isAdditionalFundAllowed = true,
        lengthOfPeriod = 8,
        description = setOf(),
        objectives = listOf(),
        strategies = sortedSetOf(),
        funds = sortedSetOf(),
        flatRates = sortedSetOf(),
        lumpSums = listOf(),
        unitCosts = listOf(),
        applicationFormFieldConfigurations = mutableSetOf(),
        preSubmissionCheckPluginKey = null,
        firstStepPreSubmissionCheckPluginKey = null,
        reportPartnerCheckPluginKey = null,
        projectDefinedUnitCostAllowed = false,
        projectDefinedLumpSumAllowed = true
    )


    @MockK
    lateinit var persistence: CallPersistence

    @InjectMockKs
    private lateinit var updateProjectNotificationConfiguration: UpdateProjectNotificationConfiguration

    @Test
    fun `update application form field configuration`() {
        every {
            persistence.saveProjectNotificationConfigurations(
                CALL_ID,
                projectNotificationConfigStandard
            )
        } returns projectNotificationConfigStandard
        every { persistence.getCallById(CALL_ID) } returns callDetail

        val result = updateProjectNotificationConfiguration.update(CALL_ID, projectNotificationConfigStandard)

        assertThat(result).isEqualTo(projectNotificationConfigStandard)
    }
}
