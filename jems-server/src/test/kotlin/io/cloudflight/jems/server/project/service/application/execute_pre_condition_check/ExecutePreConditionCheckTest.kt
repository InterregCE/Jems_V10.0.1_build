package io.cloudflight.jems.server.project.service.application.execute_pre_condition_check

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
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.ZonedDateTime
import java.util.Optional

internal class ExecutePreConditionCheckTest : UnitTest() {

    private val projectId = 1L

    private val pluginKey = "standard-pre-condition-check-plugin"
    private val projectInStepTwo = buildProjectSummary(status = ApplicationStatus.DRAFT)
    private val projectInStepOne = buildProjectSummary(status = ApplicationStatus.STEP1_DRAFT)
    private val twoStepCallSetting = buildCallSetting(preSubmissionCheckPluginKey= pluginKey )
    private val oneStepCallSetting = buildCallSetting(preSubmissionCheckPluginKey= pluginKey, endDateStep1 = null)

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @RelaxedMockK
    lateinit var preConditionCheckPlugin: PreConditionCheckPlugin

    //TODO should be replaced after MP2-1510
    @MockK
    lateinit var pluginStatusRepository: PluginStatusRepository


    @InjectMockKs
    lateinit var executePreConditionCheck: ExecutePreConditionCheck


    @Test
    fun `should execute pre condition check plugin for the project application when application belongs to two-step call and application is in step two`() {
        every { projectPersistence.getProjectCallSettings(projectId) } returns twoStepCallSetting
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepTwo
        every { pluginStatusRepository.findById(pluginKey) } returns Optional.of(
            PluginStatusEntity(pluginKey, true)
        )
        val pluginExpectedResult = PreConditionCheckResult(listOf(), false)
        every {
            jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKey)
        } returns preConditionCheckPlugin
        every { preConditionCheckPlugin.check(projectId) } returns pluginExpectedResult
        val preConditionActualResult = executePreConditionCheck.execute(projectId)

        assertThat(preConditionActualResult).isEqualTo(pluginExpectedResult)

    }

    @Test
    fun `should execute pre condition check plugin for the project application when application belongs to a one-step call`() {
        every { projectPersistence.getProjectCallSettings(projectId) } returns oneStepCallSetting
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepOne
        every { pluginStatusRepository.findById(pluginKey) } returns Optional.of(
            PluginStatusEntity(pluginKey, true)
        )
        val pluginExpectedResult = PreConditionCheckResult(listOf(), false)
        every {
            jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKey)
        } returns preConditionCheckPlugin
        every { preConditionCheckPlugin.check(projectId) } returns pluginExpectedResult
        val preConditionActualResult = executePreConditionCheck.execute(projectId)
        assertThat(preConditionActualResult).isEqualTo(pluginExpectedResult)

    }

    @Test
    fun `should throw exception when application belongs to two-step call and application is not in step two`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepOne
        every { projectPersistence.getProjectCallSettings(projectId) } returns twoStepCallSetting
        every { pluginStatusRepository.findById(pluginKey) } returns Optional.of(
            PluginStatusEntity(pluginKey, true)
        )
        every {
            jemsPluginRegistry.get(
                PreConditionCheckPlugin::class, pluginKey
            )
        } returns preConditionCheckPlugin

        assertThrows<PreConditionCheckCannotBeExecutedException> { executePreConditionCheck.execute(projectId) }
    }

    @Test
    fun `should return empty result when plugins is disabled`() {
        every { projectPersistence.getProjectCallSettings(projectId) } returns twoStepCallSetting
        every { pluginStatusRepository.findById(pluginKey) } returns Optional.of(PluginStatusEntity(pluginKey, false))
        assertThat ( executePreConditionCheck.execute(projectId) ).isEqualTo(PreConditionCheckResult(emptyList(), true))
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
        unitCosts: List<ProgrammeUnitCost> = emptyList(),
        stateAids : List<ProgrammeStateAid> = emptyList(),
        preSubmissionCheckPluginKey: String? = null
    ) =
        ProjectCallSettings(
            callId, callName, startDate, endDate, endDateStep1,
            lengthOfPeriod, isAdditionalFundAllowed, flatRates, lumpSums, unitCosts, stateAids,
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = preSubmissionCheckPluginKey
        )

    private fun buildProjectSummary(
        id: Long = 1L, callName: String = "call name",
        acronym: String = "project acronym", status: ApplicationStatus
    ) =
        ProjectSummary(id, id.toString(), callName, acronym, status)
}
