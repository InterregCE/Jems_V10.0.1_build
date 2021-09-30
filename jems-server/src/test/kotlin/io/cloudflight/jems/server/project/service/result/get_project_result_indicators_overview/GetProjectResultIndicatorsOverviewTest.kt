package io.cloudflight.jems.server.project.service.result.get_project_result_indicators_overview

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import io.cloudflight.jems.server.project.service.result.ProjectResultPersistence
import io.cloudflight.jems.server.project.service.result.model.OutputRow
import io.cloudflight.jems.server.project.service.result.model.ProjectResult
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetProjectResultIndicatorsOverviewTest: UnitTest() {

    companion object {
        private const val OUTPUT_1_ID = 289L
        private const val RESULT_1_ID = 456L
        private const val RESULT_2_ID = 822L

        private val output_1_O1_R1 = OutputRow(
            workPackageId = 1,
            workPackageNumber = 5,
            outputTitle = setOf(InputTranslation(EN, "first")),
            outputNumber = 1,
            outputTargetValue = BigDecimal.TEN,
            programmeOutputId = OUTPUT_1_ID,
            programmeResultId = RESULT_1_ID,
        )
        private val output_2_O1_R1 = OutputRow(
            workPackageId = 1,
            workPackageNumber = 5,
            outputTitle = setOf(InputTranslation(EN, "second")),
            outputNumber = 2,
            outputTargetValue = BigDecimal.TEN,
            programmeOutputId = OUTPUT_1_ID,
            programmeResultId = RESULT_1_ID,
        )
    }

    @MockK
    lateinit var workPackagePersistence: WorkPackagePersistence
    @MockK
    lateinit var projectResultPersistence: ProjectResultPersistence
    @MockK
    lateinit var listOutputIndicatorsPersistence: OutputIndicatorPersistence
    @MockK
    lateinit var listResultIndicatorsPersistence: ResultIndicatorPersistence

    @InjectMockKs
    lateinit var getProjectResultIndicatorsOverview: GetProjectResultIndicatorsOverview

    @Test
    fun getResultsForProject() {
        // all project outputs
        every { workPackagePersistence.getAllOutputsForProjectIdSortedByNumbers(1L, "1.0") } returns listOf(
            output_1_O1_R1,
            output_2_O1_R1,
        )
        // all project results
        every { projectResultPersistence.getResultsForProject(1L, "1.0") } returns listOf(
            ProjectResult(
                programmeResultIndicatorId = RESULT_2_ID,
                baseline = BigDecimal.TEN,
                targetValue = BigDecimal.TEN,
            ),
            ProjectResult(
                programmeResultIndicatorId = RESULT_2_ID,
                baseline = BigDecimal.TEN,
                targetValue = BigDecimal.TEN,
            ),
            ProjectResult(
                programmeResultIndicatorId = RESULT_2_ID,
                baseline = BigDecimal.ONE,
                targetValue = BigDecimal.ONE,
            ),
        )

        // all programme output indicators
        every { listOutputIndicatorsPersistence.getTop50OutputIndicators() } returns setOf(
            OutputIndicatorSummary(
                id = OUTPUT_1_ID,
                identifier = "O1",
                code = "RCO45",
                name = setOf(InputTranslation(EN, "O1 indicator")),
                programmePriorityCode = "P01.1",
                measurementUnit = setOf(InputTranslation(EN, "FTEs/annual")),
            ),
        )
        // all programme result indicators
        every { listResultIndicatorsPersistence.getTop50ResultIndicators() } returns setOf(
            ResultIndicatorSummary(
                id = RESULT_1_ID,
                identifier = "R1",
                code = "RCR06",
                name = setOf(InputTranslation(EN, "R1 indicator")),
                programmePriorityCode = "P01.1",
                measurementUnit = setOf(InputTranslation(EN, "km/h")),
                baseline = BigDecimal.TEN,
            ),
            ResultIndicatorSummary(
                id = RESULT_2_ID,
                identifier = "R2",
                code = "RCR09",
                name = setOf(InputTranslation(EN, "R2 indicator")),
                programmePriorityCode = "P01.1",
                measurementUnit = setOf(InputTranslation(EN, "lumen")),
                baseline = BigDecimal.TEN,
            ),
        )

        val overviewTableLines = getProjectResultIndicatorsOverview.getProjectResultIndicatorOverview(1L, "1.0")
        assertThat(overviewTableLines).hasSize(3)

        with(overviewTableLines.get(0)) {
            assertThat(outputIndicator?.id).isEqualTo(OUTPUT_1_ID)
            assertThat(outputIndicator?.identifier).isEqualTo("O1")
            assertThat(outputIndicator?.name).containsExactlyInAnyOrder(InputTranslation(EN, "O1 indicator"))
            assertThat(outputIndicator?.measurementUnit).containsExactlyInAnyOrder(InputTranslation(EN, "FTEs/annual"))
            assertThat(outputIndicator?.targetValueSumUp).isEqualByComparingTo(BigDecimal.valueOf(20))

            assertThat(projectOutput?.projectOutputNumber).isEqualTo("5.1")
            assertThat(projectOutput?.projectOutputTitle).containsExactlyInAnyOrder(InputTranslation(EN, "first"))
            assertThat(projectOutput?.projectOutputTargetValue).isEqualByComparingTo(BigDecimal.TEN)

            assertThat(resultIndicator?.id).isEqualTo(RESULT_1_ID)
            assertThat(resultIndicator?.identifier).isEqualTo("R1")
            assertThat(resultIndicator?.name).containsExactlyInAnyOrder(InputTranslation(EN, "R1 indicator"))
            assertThat(resultIndicator?.measurementUnit).containsExactlyInAnyOrder(InputTranslation(EN, "km/h"))
            assertThat(resultIndicator?.baseline).isEmpty()
            assertThat(resultIndicator?.targetValueSumUp).isEqualByComparingTo(BigDecimal.ZERO)

            assertThat(onlyResultWithoutOutputs).isFalse
        }
        with(overviewTableLines.get(1)) {
            assertThat(outputIndicator?.id).isEqualTo(OUTPUT_1_ID)
            assertThat(outputIndicator?.identifier).isEqualTo("O1")
            assertThat(outputIndicator?.name).containsExactlyInAnyOrder(InputTranslation(EN, "O1 indicator"))
            assertThat(outputIndicator?.measurementUnit).containsExactlyInAnyOrder(InputTranslation(EN, "FTEs/annual"))
            assertThat(outputIndicator?.targetValueSumUp).isEqualByComparingTo(BigDecimal.valueOf(20))

            assertThat(projectOutput?.projectOutputNumber).isEqualTo("5.2")
            assertThat(projectOutput?.projectOutputTitle).containsExactlyInAnyOrder(InputTranslation(EN, "second"))
            assertThat(projectOutput?.projectOutputTargetValue).isEqualByComparingTo(BigDecimal.TEN)

            assertThat(resultIndicator?.id).isEqualTo(RESULT_1_ID)
            assertThat(resultIndicator?.identifier).isEqualTo("R1")
            assertThat(resultIndicator?.name).containsExactlyInAnyOrder(InputTranslation(EN, "R1 indicator"))
            assertThat(resultIndicator?.measurementUnit).containsExactlyInAnyOrder(InputTranslation(EN, "km/h"))
            assertThat(resultIndicator?.baseline).isEmpty()
            assertThat(resultIndicator?.targetValueSumUp).isEqualByComparingTo(BigDecimal.ZERO)

            assertThat(onlyResultWithoutOutputs).isFalse
        }
        with(overviewTableLines.get(2)) {
            assertThat(outputIndicator).isNull()
            assertThat(projectOutput).isNull()

            assertThat(resultIndicator?.id).isEqualTo(RESULT_2_ID)
            assertThat(resultIndicator?.identifier).isEqualTo("R2")
            assertThat(resultIndicator?.name).containsExactlyInAnyOrder(InputTranslation(EN, "R2 indicator"))
            assertThat(resultIndicator?.measurementUnit).containsExactlyInAnyOrder(InputTranslation(EN, "lumen"))
            assertThat(resultIndicator?.baseline).hasSize(2)
            assertThat(resultIndicator?.baseline).containsExactlyInAnyOrder(BigDecimal.TEN, BigDecimal.ONE)
            assertThat(resultIndicator?.targetValueSumUp).isEqualByComparingTo(BigDecimal.valueOf(21))

            assertThat(onlyResultWithoutOutputs).isTrue
        }
    }
}
