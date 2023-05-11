package io.cloudflight.jems.server.project.service.report.project.workPlan.getProjectReportWorkPlan

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import java.math.BigDecimal
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetProjectReportWorkPlanTest: UnitTest() {

    companion object {
        private val dummyFile = JemsFileMetadata(750L, "cat_gif.gif", ZonedDateTime.now())

        private val outputIndicator = mockk< OutputIndicatorSummary>()

        private val workPlan = ProjectReportWorkPackage(
            id = 45L,
            number = 45,
            deactivated = false,
            specificObjective = setOf(InputTranslation(SystemLanguage.EN, "[45] specificObjective")),
            specificStatus = ProjectReportWorkPlanStatus.Partly,
            specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[45] specificExplanation new")),
            communicationObjective = setOf(InputTranslation(SystemLanguage.EN, "[45] communicationObjective")),
            communicationStatus = ProjectReportWorkPlanStatus.Partly,
            communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[45] communicationExplanation new")),
            completed = true,
            description = setOf(InputTranslation(SystemLanguage.EN, "[45] description new")),
            activities = listOf(
                ProjectReportWorkPackageActivity(
                    id = 99L,
                    number = 99,
                    title = setOf(InputTranslation(SystemLanguage.EN, "[99] title")),
                    deactivated = false,
                    progress = setOf(InputTranslation(SystemLanguage.EN, "[99] progress new")),
                    startPeriod = ProjectPeriod(2, 4, 6),
                    endPeriod = ProjectPeriod(3, 7, 9),
                    status = ProjectReportWorkPlanStatus.Partly,
                    deliverables = listOf(
                        ProjectReportWorkPackageActivityDeliverable(
                            id = 87L,
                            number = 87,
                            title = setOf(InputTranslation(SystemLanguage.EN, "[87] title")),
                            deactivated = false,
                            period = ProjectPeriod(4, 10, 12),
                            previouslyReported = BigDecimal.valueOf(9775L, 2),
                            currentReport = BigDecimal.TEN,
                            progress = setOf(InputTranslation(SystemLanguage.EN, "[87] progress new")),
                            attachment = dummyFile,
                            previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[87] progress new")),
                            previousCurrentReport = BigDecimal.TEN,
                        ),
                    ),
                    attachment = dummyFile,
                    previousStatus = ProjectReportWorkPlanStatus.Partly,
                    previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[99] progress new"))
                ),
            ),
            outputs = listOf(
                ProjectReportWorkPackageOutput(
                    id = 61,
                    number = 61,
                    title = setOf(InputTranslation(SystemLanguage.EN, "[61] title")),
                    deactivated = false,
                    outputIndicator = outputIndicator,
                    period = ProjectPeriod(5, 13, 15),
                    targetValue = BigDecimal.valueOf(2598L, 2),
                    previouslyReported = BigDecimal.valueOf(1558L, 2),
                    currentReport = BigDecimal.ONE,
                    progress = setOf(InputTranslation(SystemLanguage.EN, "[61] progress new")),
                    attachment = dummyFile,
                    previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[61] progress new")),
                    previousCurrentReport = BigDecimal.ONE
                ),
            ),
            investments = emptyList(),
            previousCommunicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[45] communicationExplanation new")),
            previousSpecificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[45] specificExplanation new")),
            previousSpecificStatus = ProjectReportWorkPlanStatus.Partly,
            previousCompleted = true,
            previousCommunicationStatus = ProjectReportWorkPlanStatus.Partly,
        )

        private val expectedWorkPlan = ProjectReportWorkPackage(
            id = 45L,
            number = 45,
            deactivated = false,
            specificObjective = setOf(InputTranslation(SystemLanguage.EN, "[45] specificObjective")),
            specificStatus = ProjectReportWorkPlanStatus.Partly,
            specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[45] specificExplanation new")),
            communicationObjective = setOf(InputTranslation(SystemLanguage.EN, "[45] communicationObjective")),
            communicationStatus = ProjectReportWorkPlanStatus.Partly,
            communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[45] communicationExplanation new")),
            completed = true,
            description = setOf(InputTranslation(SystemLanguage.EN, "[45] description new")),
            activities = listOf(
                ProjectReportWorkPackageActivity(
                    id = 99L,
                    number = 99,
                    title = setOf(InputTranslation(SystemLanguage.EN, "[99] title")),
                    deactivated = false,
                    progress = setOf(InputTranslation(SystemLanguage.EN, "[99] progress new")),
                    startPeriod = ProjectPeriod(2, 4, 6),
                    endPeriod = ProjectPeriod(3, 7, 9),
                    status = ProjectReportWorkPlanStatus.Partly,
                    deliverables = listOf(
                        ProjectReportWorkPackageActivityDeliverable(
                            id = 87L,
                            number = 87,
                            title = setOf(InputTranslation(SystemLanguage.EN, "[87] title")),
                            deactivated = false,
                            period = ProjectPeriod(4, 10, 12),
                            previouslyReported = BigDecimal.valueOf(9775L, 2),
                            currentReport = BigDecimal.TEN,
                            progress = setOf(InputTranslation(SystemLanguage.EN, "[87] progress new")),
                            attachment = dummyFile,
                            previousCurrentReport = BigDecimal.TEN,
                            previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[87] progress new")),
                        )
                    ),
                    attachment = dummyFile,
                    previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[99] progress new")),
                    previousStatus = ProjectReportWorkPlanStatus.Partly,
                    activityStatusLabel = ProjectReportWorkPlanFlag.Gray
                ),
            ),
            outputs = listOf(
                ProjectReportWorkPackageOutput(
                    id = 61,
                    number = 61,
                    title = setOf(InputTranslation(SystemLanguage.EN, "[61] title")),
                    deactivated = false,
                    outputIndicator = outputIndicator,
                    period = ProjectPeriod(5, 13, 15),
                    targetValue = BigDecimal.valueOf(2598L, 2),
                    previouslyReported = BigDecimal.valueOf(1558L, 2),
                    currentReport = BigDecimal.ONE,
                    progress = setOf(InputTranslation(SystemLanguage.EN, "[61] progress new")),
                    attachment = dummyFile,
                    previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[61] progress new")),
                    previousCurrentReport = BigDecimal.ONE,
                ),
            ),
            investments = emptyList(),
            previousCommunicationStatus = ProjectReportWorkPlanStatus.Partly,
            previousCompleted = true,
            previousSpecificStatus = ProjectReportWorkPlanStatus.Partly,
            previousSpecificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[45] specificExplanation new")),
            previousCommunicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[45] communicationExplanation new")),
            specificStatusLabel = ProjectReportWorkPlanFlag.Gray,
            communicationStatusLabel = ProjectReportWorkPlanFlag.Gray,
            workPlanStatusLabel = ProjectReportWorkPlanFlag.Gray
        )
    }

    @MockK
    private lateinit var reportWorkPlanPersistence: ProjectReportWorkPlanPersistence

    @InjectMockKs
    private lateinit var interactor: GetProjectReportWorkPlan

    @BeforeEach
    fun reset() {
        clearMocks(reportWorkPlanPersistence)
    }

    @Test
    fun get() {
        every { reportWorkPlanPersistence.getReportWorkPlanById(projectId = 10L, reportId = 20L) } returns listOf(workPlan)
        assertThat(interactor.get(10L, reportId = 20L)).isEqualTo(listOf(expectedWorkPlan))
    }
}
