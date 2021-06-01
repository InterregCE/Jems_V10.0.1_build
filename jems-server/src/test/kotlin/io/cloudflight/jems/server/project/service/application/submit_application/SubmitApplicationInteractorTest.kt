package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.entity.PluginStatusEntity
import io.cloudflight.jems.server.plugin.repository.PluginStatusRepository
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.DraftApplicationState
import io.cloudflight.jems.server.project.service.create_new_project_version.CreateNewProjectVersionInteractor
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime
import java.util.Optional

class SubmitApplicationInteractorTest : UnitTest() {

    private val projectId = 1L
    private val pluginKey = "standard-pre-condition-check-plugin"
    private val projectInStepTwo = buildProjectSummary(status = ApplicationStatus.DRAFT)
    private val projectInStepOne = buildProjectSummary(status = ApplicationStatus.STEP1_DRAFT)
    private val twoStepCallSetting = buildCallSetting()
    private val oneStepCallSetting = buildCallSetting(endDateStep1 = null)

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var projectWorkflowPersistence: ProjectWorkflowPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @MockK
    lateinit var preConditionCheckPlugin: PreConditionCheckPlugin

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @RelaxedMockK
    lateinit var createNewProjectVersionInteractor: CreateNewProjectVersionInteractor

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher


    //TODO should be replaced after MP2-1510
    @MockK
    lateinit var pluginStatusRepository: PluginStatusRepository

    @InjectMockKs
    private lateinit var submitApplication: SubmitApplication

    @MockK
    lateinit var draftState: DraftApplicationState

    @BeforeEach
    fun reset() {
        clearMocks(preConditionCheckPlugin)
        every { pluginStatusRepository.findById(pluginKey) } returns Optional.of(PluginStatusEntity(pluginKey, true))
    }

    @Test
    fun submit() {
        every {
            jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKey)
        } returns preConditionCheckPlugin
        every { preConditionCheckPlugin.check(projectId) } returns PreConditionCheckResult(emptyList(), true)
        every { projectPersistence.getProjectCallSettings(projectId) } returns twoStepCallSetting
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepTwo
        every { applicationStateFactory.getInstance(any()) } returns draftState
        every { draftState.submit() } returns ApplicationStatus.SUBMITTED
        every {
            projectWorkflowPersistence.getLatestApplicationStatusNotEqualTo(
                projectId,
                ApplicationStatus.RETURNED_TO_APPLICANT
            )
        } returns ApplicationStatus.SUBMITTED


        assertThat(submitApplication.submit(projectId)).isEqualTo(ApplicationStatus.SUBMITTED)

        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 1) { auditPublisher.publishEvent(capture(slotAudit)) }
        assertThat(slotAudit.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = projectId.toString(), name = "project acronym"),
                description = "Project application status changed from DRAFT to SUBMITTED"
            )
        )
    }

    @Test
    fun `should throw exception when pre condition check fails`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepTwo
        every { projectPersistence.getProjectCallSettings(projectId) } returns twoStepCallSetting
        every {
            jemsPluginRegistry.get(
                PreConditionCheckPlugin::class, pluginKey
            )
        } returns preConditionCheckPlugin
        every { preConditionCheckPlugin.check(projectId) } returns PreConditionCheckResult(emptyList(), false)

        assertThrows<SubmitApplicationPreConditionCheckFailedException> { submitApplication.submit(projectId) }
    }

    @Test
    fun `should execute pre condition check when application belongs to one-step call`() {
        every {
            jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKey)
        } returns preConditionCheckPlugin
        every { preConditionCheckPlugin.check(projectId) } returns PreConditionCheckResult(emptyList(), true)
        every { projectPersistence.getProjectCallSettings(projectId) } returns oneStepCallSetting
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepTwo
        every { applicationStateFactory.getInstance(any()) } returns draftState

        submitApplication.submit(projectId)
        verify(exactly = 1) { preConditionCheckPlugin.check(projectId) }
    }

    @Test
    fun `should not execute pre condition check when application belongs to two-step call and application is not in step two`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepOne
        every { projectPersistence.getProjectCallSettings(projectId) } returns twoStepCallSetting
        submitApplication.submit(projectId)
        verify(exactly = 0) { preConditionCheckPlugin.check(projectId) }
    }


    private fun buildCallSetting(
        callId: Long = 1L,
        callName: String = "call",
        startDate: ZonedDateTime = ZonedDateTime.now().minusDays(4),
        endDate: ZonedDateTime = ZonedDateTime.now().plusDays(4),
        endDateStep1: ZonedDateTime? = ZonedDateTime.now().plusDays(2),
        lengthOfPeriod: Int = 1,
        isAdditionalFundAllowed: Boolean = true,
        flatRates: Set<ProjectCallFlatRate> = emptySet(),
        lumpSums: List<ProgrammeLumpSum> = emptyList(),
        unitCosts: List<ProgrammeUnitCost> = emptyList()
    ) =
        ProjectCallSettings(
            callId, callName, startDate, endDate, endDateStep1,
            lengthOfPeriod, isAdditionalFundAllowed, flatRates, lumpSums, unitCosts
        )

    private fun buildProjectSummary(
        id: Long = 1L, callName: String = "call name",
        acronym: String = "project acronym", status: ApplicationStatus
    ) =
        ProjectSummary(id, callName, acronym, status)
}
