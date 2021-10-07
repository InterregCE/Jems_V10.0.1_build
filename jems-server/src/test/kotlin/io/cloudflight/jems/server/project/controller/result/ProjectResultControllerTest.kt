package io.cloudflight.jems.server.project.controller.result

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.result.IndicatorOverviewLineDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultUpdateRequestDTO
import io.cloudflight.jems.api.project.dto.result.ProjectResultDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.result.get_project_result.GetProjectResultInteractor
import io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview.GetProjectResultIndicatorsOverviewInteractor
import io.cloudflight.jems.server.project.service.result.model.IndicatorOutput
import io.cloudflight.jems.server.project.service.result.model.IndicatorOverviewLine
import io.cloudflight.jems.server.project.service.result.model.IndicatorResult
import io.cloudflight.jems.server.project.service.result.model.ProjectOutput
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.result.update_project_results.UpdateProjectResultsInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectResultControllerTest: UnitTest() {

    companion object {
        val description = setOf(
            InputTranslation(language = EN, translation = null),
            InputTranslation(language = CS, translation = "cs_desc"),
            InputTranslation(language = SK, translation = ""),
        )
        val result1 = ProjectResult(
            resultNumber = 1,
            programmeResultIndicatorId = 5L,
            programmeResultIndicatorIdentifier = "ABB05",
            baseline  = BigDecimal.ZERO,
            targetValue = BigDecimal.ONE,
            periodNumber = 4,
            description = description
        )
        val result2 = ProjectResult(
            baseline  = BigDecimal.ZERO,
            resultNumber = 2,
        )
    }

    @MockK
    lateinit var getResult: GetProjectResultInteractor

    @MockK
    lateinit var getProjectResultIndicatorsOverview: GetProjectResultIndicatorsOverviewInteractor

    @MockK
    lateinit var updateResults: UpdateProjectResultsInteractor

    @InjectMockKs
    private lateinit var controller: ProjectResultController

    @Test
    fun getProjectResults() {
        every { getResult.getResultsForProject(1L, null) } returns listOf(result1, result2)

        assertThat(controller.getProjectResults(1L)).containsExactly(
            ProjectResultDTO(
                resultNumber = 1,
                programmeResultIndicatorId = 5L,
                programmeResultIndicatorIdentifier = "ABB05",
                baseline  = BigDecimal.ZERO,
                targetValue = BigDecimal.ONE,
                periodNumber = 4,
                description = description
            ),
            ProjectResultDTO(
                baseline  = BigDecimal.ZERO,
                resultNumber = 2,
            )
        )
    }

    @Test
    fun updateActivities() {
        val resultSlot = slot<List<ProjectResult>>()
        every { updateResults.updateResultsForProject(1L, capture(resultSlot)) } returns emptyList()

        val resultDto1 = ProjectResultUpdateRequestDTO(
            programmeResultIndicatorId = 15L,
            baseline = BigDecimal.ZERO,
            targetValue = BigDecimal.ONE,
            periodNumber = 7,
            description = setOf(InputTranslation(EN, "en desc"), InputTranslation(CS, ""), InputTranslation(SK, null)),
        )
        val resultDto2 = ProjectResultUpdateRequestDTO(baseline = BigDecimal.ZERO)

        controller.updateProjectResults(1L, listOf(resultDto1, resultDto2))
        assertThat(resultSlot.captured).containsExactly(
            ProjectResult(
                programmeResultIndicatorId = 15L,
                baseline = BigDecimal.ZERO,
                targetValue = BigDecimal.ONE,
                periodNumber = 7,
                description = setOf(InputTranslation(EN, "en desc"), InputTranslation(CS, ""), InputTranslation(SK, null)),
            ),
            ProjectResult(baseline = BigDecimal.ZERO,)
        )
    }

    @Test
    fun getProjectResultsIndicatorsOverviewDataForTable() {
        val indicatorOverviewLine1 = IndicatorOverviewLine(
            outputIndicator = IndicatorOutput(
                id = 1L,
                identifier = "RCO45",
                name = setOf(InputTranslation(EN, "output indicator name")),
                measurementUnit = setOf(InputTranslation(EN, "km/h")),
                targetValueSumUp = BigDecimal.ONE,
            ),
            projectOutput = ProjectOutput(
                projectOutputNumber = "2.1",
                projectOutputTitle = setOf(InputTranslation(EN, "km/h")),
                projectOutputTargetValue = BigDecimal.TEN,
            ),
            resultIndicator = null,
            onlyResultWithoutOutputs = false,
        )

        val indicatorOverviewLine2 = IndicatorOverviewLine(
            outputIndicator = null,
            projectOutput = null,
            resultIndicator = IndicatorResult(
                id = 17L,
                identifier = "RCR15",
                name = setOf(InputTranslation(EN, "result indicator name")),
                measurementUnit = setOf(InputTranslation(EN, "persons")),
                baseline = setOf(BigDecimal.ZERO, BigDecimal.ONE),
                targetValueSumUp = BigDecimal.ONE,
            ),
            onlyResultWithoutOutputs = true,
        )

        every { getProjectResultIndicatorsOverview.getProjectResultIndicatorOverview(10L) } returns
            listOf(indicatorOverviewLine1, indicatorOverviewLine2)

        assertThat(controller.getProjectResultIndicatorOverview(10L)).containsExactly(
            IndicatorOverviewLineDTO(
                outputIndicatorId = 1L,
                outputIndicatorIdentifier = "RCO45",
                outputIndicatorName = setOf(InputTranslation(EN, "output indicator name")),
                outputIndicatorMeasurementUnit = setOf(InputTranslation(EN, "km/h")),
                outputIndicatorTargetValueSumUp = BigDecimal.ONE,

                projectOutputNumber = "2.1",
                projectOutputTitle = setOf(InputTranslation(EN, "km/h")),
                projectOutputTargetValue = BigDecimal.TEN,

                resultIndicatorId = null,
                resultIndicatorIdentifier = null,
                resultIndicatorName = null,
                resultIndicatorMeasurementUnit = null,
                resultIndicatorBaseline = null,
                resultIndicatorTargetValueSumUp = null,

                onlyResultWithoutOutputs = false,
            ),
            IndicatorOverviewLineDTO(
                outputIndicatorId = null,
                outputIndicatorIdentifier = null,
                outputIndicatorName = null,
                outputIndicatorMeasurementUnit = null,
                outputIndicatorTargetValueSumUp = null,

                projectOutputNumber = null,
                projectOutputTitle = null,
                projectOutputTargetValue = null,

                resultIndicatorId = 17L,
                resultIndicatorIdentifier = "RCR15",
                resultIndicatorName = setOf(InputTranslation(EN, "result indicator name")),
                resultIndicatorMeasurementUnit = setOf(InputTranslation(EN, "persons")),
                resultIndicatorBaseline = setOf(BigDecimal.ZERO, BigDecimal.ONE),
                resultIndicatorTargetValueSumUp = BigDecimal.ONE,

                onlyResultWithoutOutputs = true,
            ),
        )
    }

}
