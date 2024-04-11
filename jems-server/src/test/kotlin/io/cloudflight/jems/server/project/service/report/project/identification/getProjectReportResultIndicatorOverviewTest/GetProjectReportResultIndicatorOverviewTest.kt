package io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportResultIndicatorOverviewTest

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorsAndResults
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportProjectResult
import io.cloudflight.jems.server.project.service.report.model.project.projectResults.ProjectReportResultPrinciple
import io.cloudflight.jems.server.project.service.report.project.identification.getProjectReportResultIndicatorOverview.GetProjectReportResultIndicatorOverview
import io.cloudflight.jems.server.project.service.report.project.resultPrinciple.ProjectReportResultPrinciplePersistence
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class GetProjectReportResultIndicatorOverviewTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L

        private val period = mockk<ProjectPeriod>()
        private val description = mockk<Set<InputTranslation>>()
        private val measurementUnit = mockk<Set<InputTranslation>>()

        private fun resultIndicatorOverview()
            : Map<ProjectReportResultIndicatorOverview, ProjectReportOutputIndicatorsAndResults> {
            val resultIndicator = ProjectReportResultIndicatorOverview(
                id = 3L,
                identifier = "3",
                name = setOf(InputTranslation(SystemLanguage.EN, "3")),
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "3")),
                baselineIndicator = BigDecimal.valueOf(10),
                baselines = listOf(BigDecimal.valueOf(12L), BigDecimal.valueOf(13L)),
                targetValue = BigDecimal.valueOf(17L),
                previouslyReported = BigDecimal.valueOf(121L),
                currentReport = BigDecimal.valueOf(555L)
            )

            return mapOf(resultIndicator to outputIndicatorOverview(resultIndicator))
        }

        private fun outputIndicatorOverview(
            resultIndicator: ProjectReportResultIndicatorOverview
        ): ProjectReportOutputIndicatorsAndResults {
            val outputIndicator = ProjectReportOutputIndicatorOverview(
                id = 4L,
                identifier = "4",
                name = setOf(InputTranslation(SystemLanguage.EN, "4")),
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "4")),
                targetValue = BigDecimal.valueOf(4711),
                previouslyReported = BigDecimal.valueOf(4812),
                currentReport = BigDecimal.valueOf(4913),
                resultIndicator = resultIndicator,
            )

            return ProjectReportOutputIndicatorsAndResults(
                outputIndicators = mapOf(outputIndicator to listOf(
                    ProjectReportOutputLineOverview(
                        number = 5,
                        workPackageNumber = 5,
                        name = setOf(InputTranslation(SystemLanguage.EN, "5")),
                        measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "5")),
                        targetValue = BigDecimal.valueOf(4700),
                        previouslyReported = BigDecimal.valueOf(4800),
                        currentReport = BigDecimal.valueOf(4900),
                        deactivated = false,
                        outputIndicator = outputIndicator,
                    ),
                    ProjectReportOutputLineOverview(
                        number = 6,
                        workPackageNumber = 6,
                        name = setOf(InputTranslation(SystemLanguage.EN, "6")),
                        measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "6")),
                        targetValue = BigDecimal.valueOf(11),
                        previouslyReported = BigDecimal.valueOf(12),
                        currentReport = BigDecimal.valueOf(13),
                        deactivated = false,
                        outputIndicator = outputIndicator.copy(
                            targetValue = BigDecimal.valueOf(0),
                            previouslyReported = BigDecimal.valueOf(0),
                            currentReport = BigDecimal.valueOf(0),
                            resultIndicator = resultIndicator.copy(
                                targetValue = BigDecimal.valueOf(0),
                                previouslyReported = BigDecimal.valueOf(0),
                                currentReport = BigDecimal.valueOf(0),
                            )
                        ),
                    )
                )),
                results = listOf(
                    ProjectReportProjectResult(
                        resultNumber = 1,
                        deactivated = false,
                        programmeResultIndicatorId = 3L,
                        programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "3")),
                        programmeResultIndicatorIdentifier = "3",
                        baseline = BigDecimal.valueOf(12L),
                        targetValue = BigDecimal.valueOf(10L),
                        currentReport = BigDecimal.valueOf(500L),
                        previouslyReported = BigDecimal.valueOf(100L),
                        periodDetail = period,
                        description = description,
                        measurementUnit = measurementUnit,
                        attachment = null,
                    ),
                    ProjectReportProjectResult(
                        resultNumber = 2,
                        deactivated = true,
                        programmeResultIndicatorId = 3L,
                        programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "3")),
                        programmeResultIndicatorIdentifier = "3",
                        baseline = BigDecimal.valueOf(13L),
                        targetValue = BigDecimal.valueOf(2L),
                        currentReport = BigDecimal.valueOf(50L),
                        previouslyReported = BigDecimal.valueOf(10L),
                        periodDetail = period,
                        description = description,
                        measurementUnit = measurementUnit,
                        attachment = null,
                    ),
                    ProjectReportProjectResult(
                        resultNumber = 3,
                        deactivated = false,
                        programmeResultIndicatorId = 3L,
                        programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "3")),
                        programmeResultIndicatorIdentifier = "3",
                        baseline = BigDecimal.valueOf(12L),
                        targetValue = BigDecimal.valueOf(5L),
                        currentReport = BigDecimal.valueOf(5L),
                        previouslyReported = BigDecimal.valueOf(11L),
                        periodDetail = period,
                        description = description,
                        measurementUnit = measurementUnit,
                        attachment = null,
                    ),
                ),
            )
        }


        private val workPackageOutputs = listOf(
            ProjectReportOutputLineOverview(
                number = 5,
                workPackageNumber = 5,
                name = setOf(InputTranslation(SystemLanguage.EN, "5")),
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "5")),
                deactivated = false,
                outputIndicator = outputIndicator(4L, 3L),
                targetValue = BigDecimal.valueOf(4700),
                previouslyReported = BigDecimal.valueOf(4800),
                currentReport = BigDecimal.valueOf(4900),
            ),
            ProjectReportOutputLineOverview(
                number = 6,
                workPackageNumber = 6,
                name = setOf(InputTranslation(SystemLanguage.EN, "6")),
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "6")),
                deactivated = false,
                outputIndicator = outputIndicator(4L, 3L),
                targetValue = BigDecimal.valueOf(11),
                previouslyReported = BigDecimal.valueOf(12),
                currentReport = BigDecimal.valueOf(13),
            ),
        )

        private fun outputIndicator(id: Long, resultId: Long) = ProjectReportOutputIndicatorOverview(
            id = id,
            identifier = "$id",
            name = setOf(InputTranslation(SystemLanguage.EN, "$id")),
            measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "$id")),
            resultIndicator = resultIndicator(resultId),
            targetValue = BigDecimal.valueOf(0L),
            previouslyReported = BigDecimal.valueOf(0L),
            currentReport = BigDecimal.valueOf(0L),
        )

        private fun resultIndicator(id: Long) = ProjectReportResultIndicatorOverview(
            id = id,
            identifier = "$id",
            name = setOf(InputTranslation(SystemLanguage.EN, "$id")),
            measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "$id")),
            baselineIndicator = BigDecimal.TEN,
            baselines = listOf(BigDecimal.valueOf(12L), BigDecimal.valueOf(13L)),
            targetValue = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ZERO,
            currentReport = BigDecimal.ZERO,
        )

        private val resultPrinciples = ProjectReportResultPrinciple(
            projectResults = listOf(
                ProjectReportProjectResult(
                    resultNumber = 1,
                    deactivated = false,
                    programmeResultIndicatorId = 3L,
                    programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "3")),
                    programmeResultIndicatorIdentifier = "3",
                    baseline = BigDecimal.valueOf(12L),
                    targetValue = BigDecimal.valueOf(10L),
                    currentReport = BigDecimal.valueOf(500L),
                    previouslyReported = BigDecimal.valueOf(100L),
                    periodDetail = period,
                    description = description,
                    measurementUnit = measurementUnit,
                    attachment = null,
                ),
                ProjectReportProjectResult(
                    resultNumber = 2,
                    deactivated = true,
                    programmeResultIndicatorId = 3L,
                    programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "3")),
                    programmeResultIndicatorIdentifier = "3",
                    baseline = BigDecimal.valueOf(13L),
                    targetValue = BigDecimal.valueOf(2L),
                    currentReport = BigDecimal.valueOf(50L),
                    previouslyReported = BigDecimal.valueOf(10L),
                    periodDetail = period,
                    description = description,
                    measurementUnit = measurementUnit,
                    attachment = null,
                ),
                ProjectReportProjectResult(
                    resultNumber = 3,
                    deactivated = false,
                    programmeResultIndicatorId = 3L,
                    programmeResultIndicatorName = setOf(InputTranslation(SystemLanguage.EN, "3")),
                    programmeResultIndicatorIdentifier = "3",
                    baseline = BigDecimal.valueOf(12L),
                    targetValue = BigDecimal.valueOf(5L),
                    currentReport = BigDecimal.valueOf(5L),
                    previouslyReported = BigDecimal.valueOf(11L),
                    periodDetail = period,
                    description = description,
                    measurementUnit = measurementUnit,
                    attachment = null,
                ),
            ),
            equalOpportunitiesDescription = mockk(),
            horizontalPrinciples = mockk(),
            sustainableDevelopmentDescription = mockk(),
            sexualEqualityDescription = mockk()
        )
    }

    @MockK
    private lateinit var workPlanPersistence: ProjectReportWorkPlanPersistence

    @MockK
    private lateinit var resultPrinciplePersistence: ProjectReportResultPrinciplePersistence

    @InjectMockKs
    lateinit var interactor: GetProjectReportResultIndicatorOverview

    @BeforeEach
    fun reset() {
        clearMocks(workPlanPersistence)
    }

    @Test
    fun getIdentification() {
        every { workPlanPersistence.getReportWorkPackageOutputsById(PROJECT_ID, REPORT_ID) } returns workPackageOutputs
        every { resultPrinciplePersistence.getProjectResultPrinciples(PROJECT_ID, REPORT_ID) } returns resultPrinciples

        assertThat(interactor.getResultIndicatorOverview(PROJECT_ID, REPORT_ID))
            .containsExactlyEntriesOf(resultIndicatorOverview())
    }
}
