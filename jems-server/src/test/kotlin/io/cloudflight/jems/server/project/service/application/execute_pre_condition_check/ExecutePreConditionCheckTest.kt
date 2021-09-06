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
    private val twoStepCallSetting = buildCallSetting()
    private val oneStepCallSetting = buildCallSetting(endDateStep1 = null)

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

    @BeforeAll
    fun setup() {
        every { pluginStatusRepository.findById(pluginKey) } returns Optional.of(
            PluginStatusEntity(pluginKey, true)
        )
    }

    @Test
    fun `should execute pre condition check plugin for the project application when application belongs to twp-step call and application is in step two`() {
        every { projectPersistence.getProjectCallSettings(projectId) } returns twoStepCallSetting
        every { projectPersistence.getProjectSummary(projectId) } returns projectInStepTwo
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
        every {
            jemsPluginRegistry.get(
                PreConditionCheckPlugin::class, pluginKey
            )
        } returns preConditionCheckPlugin

        assertThrows<PreConditionCheckCannotBeExecutedException> { executePreConditionCheck.execute(projectId) }
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
    ) =
        ProjectCallSettings(
            callId, callName, startDate, endDate, endDateStep1,
            lengthOfPeriod, isAdditionalFundAllowed, flatRates, lumpSums, unitCosts, stateAids,
            applicationFormFieldConfigurations = mutableSetOf()
        )

    private fun buildProjectSummary(
        id: Long = 1L, callName: String = "call name",
        acronym: String = "project acronym", status: ApplicationStatus
    ) =
        ProjectSummary(id, id.toString(), callName, acronym, status)
}
