package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.entity.PluginStatusEntity
import io.cloudflight.jems.server.plugin.repository.PluginStatusRepository
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.application.workflow.states.DraftApplicationState
import io.cloudflight.jems.server.project.service.application.workflow.states.ReturnedToApplicantForConditionsApplicationState
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
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
    private val projectInStepTwoReturnedForConditions = buildProjectSummary(status = ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS)
    private val twoStepCallSetting = buildCallSetting(preSubmissionCheckPluginKey = pluginKey)
    private val oneStepCallSetting = buildCallSetting(preSubmissionCheckPluginKey = pluginKey)

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var applicationStateFactory: ApplicationStateFactory

    @MockK
    lateinit var preConditionCheckPlugin: PreConditionCheckPlugin

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher


    //TODO should be replaced after MP2-1510
    @MockK
    lateinit var pluginStatusRepository: PluginStatusRepository

    @InjectMockKs
    private lateinit var submitApplication: SubmitApplication

    @MockK
    lateinit var draftState: DraftApplicationState

    @MockK
    lateinit var returnToApplicantForConditionsState: ReturnedToApplicantForConditionsApplicationState

    @BeforeEach
    fun reset() {
        clearAllMocks()
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

        assertThat(submitApplication.submit(projectId)).isEqualTo(ApplicationStatus.SUBMITTED)
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
        every { draftState.submit() } returns ApplicationStatus.SUBMITTED

        submitApplication.submit(projectId)
        verify(exactly = 1) { preConditionCheckPlugin.check(projectId) }
    }

    @Test
    fun `should not execute pre condition check when application belongs to two-step call and application is not in step two`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepOne
        every { projectPersistence.getProjectCallSettings(projectId) } returns oneStepCallSetting
        every { applicationStateFactory.getInstance(any()) } returns draftState
        every { draftState.submit() } returns ApplicationStatus.SUBMITTED

        submitApplication.submit(projectId)
        verify(exactly = 0) { preConditionCheckPlugin.check(projectId) }
    }

    @Test
    fun `should not execute pre condition check when plugin is disabled`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepTwo
        every { projectPersistence.getProjectCallSettings(projectId) } returns twoStepCallSetting
        every { pluginStatusRepository.findById(pluginKey) } returns Optional.of(PluginStatusEntity(pluginKey, false))
        every { applicationStateFactory.getInstance(any()) } returns draftState
        every { draftState.submit() } returns ApplicationStatus.SUBMITTED

        submitApplication.submit(projectId)
        verify(exactly = 0) { preConditionCheckPlugin.check(projectId) }
    }

    @Test
    fun `submit from status RETURNED_TO_APPLICANT_FOR_CONDITIONS - update current version`() {
        every {
            jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKey)
        } returns preConditionCheckPlugin
        every { preConditionCheckPlugin.check(projectId) } returns PreConditionCheckResult(emptyList(), true)
        every { projectPersistence.getProjectCallSettings(projectId) } returns twoStepCallSetting
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepTwoReturnedForConditions
        every { applicationStateFactory.getInstance(any()) } returns returnToApplicantForConditionsState
        every { returnToApplicantForConditionsState.submit() } returns ApplicationStatus.CONDITIONS_SUBMITTED

        assertThat(submitApplication.submit(projectId)).isEqualTo(ApplicationStatus.CONDITIONS_SUBMITTED)
    }

    private fun buildCallSetting(
        callId: Long = 1L,
        callName: String = "call",
        callType: CallType = CallType.STANDARD,
        startDate: ZonedDateTime = ZonedDateTime.now().minusDays(4),
        endDate: ZonedDateTime = ZonedDateTime.now().plusDays(4),
        endDateStep1: ZonedDateTime? = ZonedDateTime.now().plusDays(2),
        lengthOfPeriod: Int = 1,
        isAdditionalFundAllowed: Boolean = true,
        flatRates: Set<ProjectCallFlatRate> = emptySet(),
        lumpSums: List<ProgrammeLumpSum> = emptyList(),
        unitCosts: List<ProgrammeUnitCost> = emptyList(),
        stateAids : List<ProgrammeStateAid> = emptyList(),
        preSubmissionCheckPluginKey : String? = null,
        firstStepPreSubmissionCheckPluginKey : String? = null
    ) =
        ProjectCallSettings(
            callId, callName, callType, startDate, endDate, endDateStep1,
            lengthOfPeriod, isAdditionalFundAllowed, flatRates, lumpSums, unitCosts, stateAids,
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = preSubmissionCheckPluginKey,
            firstStepPreSubmissionCheckPluginKey = firstStepPreSubmissionCheckPluginKey,
            costOption = mockk(),
        )

    private fun buildProjectSummary(
        id: Long = 1L, callName: String = "call name",
        acronym: String = "project acronym", status: ApplicationStatus
    ) =
        ProjectSummary(id, id.toString(), 1L, callName, acronym, status)
}
