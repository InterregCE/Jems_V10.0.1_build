package io.cloudflight.jems.server.project.service.report.project.workPlan.updateProjectReportWorkPlan

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverableUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOnlyUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutputUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import io.cloudflight.jems.server.project.service.report.project.workPlan.ProjectReportWorkPlanPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class UpdateProjectReportWorkPlanTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 459L

        private val dummyFile = JemsFileMetadata(750L, "cat_gif.gif", ZonedDateTime.now())

        private val oldWorkPlan = ProjectReportWorkPackage(
                id = 45L,
                number = 45,
                deactivated = false,
                specificObjective = setOf(InputTranslation(EN, "[45] specificObjective")),
                specificStatus = ProjectReportWorkPlanStatus.Not,
                specificExplanation = setOf(InputTranslation(EN, "[45] specificExplanation")),
                communicationObjective = setOf(InputTranslation(EN, "[45] communicationObjective")),
                communicationStatus = ProjectReportWorkPlanStatus.Not,
                communicationExplanation = setOf(InputTranslation(EN, "[45] communicationExplanation")),
                completed = false,
                description = setOf(InputTranslation(EN, "[45] description")),
                activities = listOf(
                        ProjectReportWorkPackageActivity(
                                id = 99L,
                                number = 99,
                                title = setOf(InputTranslation(EN, "[99] title")),
                                deactivated = false,
                                progress = setOf(InputTranslation(EN, "[99] progress")),
                                startPeriod = ProjectPeriod(2, 4, 6),
                                endPeriod = ProjectPeriod(3, 7, 9),
                                status = ProjectReportWorkPlanStatus.Not,
                                deliverables = listOf(
                                        ProjectReportWorkPackageActivityDeliverable(
                                                id = 87L,
                                                number = 87,
                                                title = setOf(InputTranslation(EN, "[87] title")),
                                                deactivated = false,
                                                period = ProjectPeriod(4, 10, 12),
                                                previouslyReported = BigDecimal.valueOf(9775L, 2),
                                                currentReport = BigDecimal.valueOf(3654L, 2),
                                                progress = setOf(InputTranslation(EN, "[87] progress")),
                                                attachment = dummyFile,
                                                previousProgress = setOf(InputTranslation(EN, "[87] progress")),
                                                previousCurrentReport = BigDecimal.valueOf(3654L, 2),
                                        ),
                                ),
                                attachment = dummyFile,
                                previousStatus = ProjectReportWorkPlanStatus.Not,
                                previousProgress = setOf(InputTranslation(EN, "[99] progress"))
                        ),
                ),
                outputs = listOf(
                        ProjectReportWorkPackageOutput(
                                id = 61,
                                number = 61,
                                title = setOf(InputTranslation(EN, "[61] title")),
                                deactivated = false,
                                outputIndicator = mockk(),
                                period = ProjectPeriod(5, 13, 15),
                                targetValue = BigDecimal.valueOf(2598L, 2),
                                previouslyReported = BigDecimal.valueOf(1558L, 2),
                                currentReport = BigDecimal.valueOf(6653L, 2),
                                progress = setOf(InputTranslation(EN, "[61] progress")),
                                attachment = dummyFile,
                                previousProgress = setOf(InputTranslation(EN, "[61] progress")),
                                previousCurrentReport = BigDecimal.valueOf(6653L, 2)
                        ),
                ),
                investments = emptyList(),
                previousCommunicationExplanation = setOf(InputTranslation(EN, "[45] communicationExplanation")),
                previousSpecificExplanation = setOf(InputTranslation(EN, "[45] specificExplanation")),
                previousSpecificStatus = ProjectReportWorkPlanStatus.Not,
                previousCompleted = false,
                previousCommunicationStatus = ProjectReportWorkPlanStatus.Not,
                previousDescription = setOf(InputTranslation(SystemLanguage.EN, "[45] description")),
        )

        private val newWorkPlan = ProjectReportWorkPackage(
                id = 45L,
                number = 45,
                deactivated = false,
                specificObjective = setOf(InputTranslation(EN, "[45] specificObjective")),
                specificStatus = ProjectReportWorkPlanStatus.Partly,
                specificExplanation = setOf(InputTranslation(EN, "[45] specificExplanation new")),
                communicationObjective = setOf(InputTranslation(EN, "[45] communicationObjective")),
                communicationStatus = ProjectReportWorkPlanStatus.Partly,
                communicationExplanation = setOf(InputTranslation(EN, "[45] communicationExplanation new")),
                completed = true,
                description = setOf(InputTranslation(EN, "[45] description new")),
                activities = listOf(
                        ProjectReportWorkPackageActivity(
                                id = 99L,
                                number = 99,
                                title = setOf(InputTranslation(EN, "[99] title")),
                                deactivated = false,
                                progress = setOf(InputTranslation(EN, "[99] progress new")),
                                startPeriod = ProjectPeriod(2, 4, 6),
                                endPeriod = ProjectPeriod(3, 7, 9),
                                status = ProjectReportWorkPlanStatus.Partly,
                                deliverables = listOf(
                                        ProjectReportWorkPackageActivityDeliverable(
                                                id = 87L,
                                                number = 87,
                                                title = setOf(InputTranslation(EN, "[87] title")),
                                                deactivated = false,
                                                period = ProjectPeriod(4, 10, 12),
                                                previouslyReported = BigDecimal.valueOf(9775L, 2),
                                                currentReport = BigDecimal.TEN,
                                                progress = setOf(InputTranslation(EN, "[87] progress new")),
                                                attachment = dummyFile,
                                                previousCurrentReport = BigDecimal.TEN,
                                                previousProgress = setOf(InputTranslation(EN, "[87] progress new")),
                                        )
                                ),
                                attachment = dummyFile,
                                previousProgress = setOf(InputTranslation(EN, "[99] progress new")),
                                previousStatus = ProjectReportWorkPlanStatus.Partly,
                                activityStatusLabel = ProjectReportWorkPlanFlag.Gray
                        ),
                ),
                outputs = listOf(
                        ProjectReportWorkPackageOutput(
                                id = 61,
                                number = 61,
                                title = setOf(InputTranslation(EN, "[61] title")),
                                deactivated = false,
                                outputIndicator = mockk(),
                                period = ProjectPeriod(5, 13, 15),
                                targetValue = BigDecimal.valueOf(2598L, 2),
                                previouslyReported = BigDecimal.valueOf(1558L, 2),
                                currentReport = BigDecimal.ONE,
                                progress = setOf(InputTranslation(EN, "[61] progress new")),
                                attachment = dummyFile,
                                previousProgress = setOf(InputTranslation(EN, "[61] progress new")),
                                previousCurrentReport = BigDecimal.ONE,
                        ),
                ),
                investments = emptyList(),
                previousCommunicationStatus = ProjectReportWorkPlanStatus.Partly,
                previousCompleted = true,
                previousSpecificStatus = ProjectReportWorkPlanStatus.Partly,
                previousSpecificExplanation = setOf(InputTranslation(EN, "[45] specificExplanation new")),
                previousCommunicationExplanation = setOf(InputTranslation(EN, "[45] communicationExplanation new")),
                specificStatusLabel = ProjectReportWorkPlanFlag.Gray,
                communicationStatusLabel = ProjectReportWorkPlanFlag.Gray,
                workPlanStatusLabel = ProjectReportWorkPlanFlag.Gray,
                previousDescription = setOf(InputTranslation(SystemLanguage.EN, "[45] description new")),
        )

        private val updateWorkPlanModel = ProjectReportWorkPackageUpdate(
                id = 45L,
                specificStatus = ProjectReportWorkPlanStatus.Partly,
                specificExplanation = setOf(InputTranslation(EN, "[45] specificExplanation new")),
                communicationStatus = ProjectReportWorkPlanStatus.Partly,
                communicationExplanation = setOf(InputTranslation(EN, "[45] communicationExplanation new")),
                completed = true,
                description = setOf(InputTranslation(EN, "[45] description new")),
                activities = listOf(
                        ProjectReportWorkPackageActivityUpdate(
                                id = 99L,
                                status = ProjectReportWorkPlanStatus.Partly,
                                progress = setOf(InputTranslation(EN, "[99] progress new")),
                                deliverables = listOf(
                                        ProjectReportWorkPackageActivityDeliverableUpdate(
                                                id = 87L,
                                                currentReport = BigDecimal.TEN,
                                                progress = setOf(InputTranslation(EN, "[87] progress new")),
                                        ),
                                ),
                        ),
                ),
                outputs = listOf(
                        ProjectReportWorkPackageOutputUpdate(
                                id = 61,
                                currentReport = BigDecimal.ONE,
                                progress = setOf(InputTranslation(EN, "[61] progress new")),
                        ),
                ),
                investments = emptyList()
        )

        private val updateWorkPlanModelNoChanges = ProjectReportWorkPackageUpdate(
                id = 45L,
                specificStatus = ProjectReportWorkPlanStatus.Not,
                specificExplanation = setOf(InputTranslation(EN, "[45] specificExplanation")),
                communicationStatus = ProjectReportWorkPlanStatus.Not,
                communicationExplanation = setOf(InputTranslation(EN, "[45] communicationExplanation")),
                completed = false,
                description = setOf(InputTranslation(EN, "[45] description")),
                activities = listOf(
                        ProjectReportWorkPackageActivityUpdate(
                                id = 99L,
                                status = ProjectReportWorkPlanStatus.Not,
                                progress = setOf(InputTranslation(EN, "[99] progress")),
                                deliverables = listOf(
                                        ProjectReportWorkPackageActivityDeliverableUpdate(
                                                id = 87L,
                                                currentReport = BigDecimal.valueOf(3654L, 2),
                                                progress = setOf(InputTranslation(EN, "[87] progress")),
                                        ),
                                ),
                        ),
                ),
                outputs = listOf(
                        ProjectReportWorkPackageOutputUpdate(
                                id = 61,
                                currentReport = BigDecimal.valueOf(6653L, 2),
                                progress = setOf(InputTranslation(EN, "[61] progress")),
                        ),
                ),
                investments = emptyList()
        )

        private val expectedWpUpdate = ProjectReportWorkPackageOnlyUpdate(
                specificStatus = ProjectReportWorkPlanStatus.Partly,
                specificExplanation = setOf(InputTranslation(EN, "[45] specificExplanation new")),
                communicationStatus = ProjectReportWorkPlanStatus.Partly,
                communicationExplanation = setOf(InputTranslation(EN, "[45] communicationExplanation new")),
                completed = true,
                description = setOf(InputTranslation(EN, "[45] description new")),
        )
    }

    @MockK
    lateinit var reportWorkPlanPersistence: ProjectReportWorkPlanPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updateWorkPlan: UpdateProjectReportWorkPlan

    @BeforeEach
    fun setup() {
        clearMocks(reportWorkPlanPersistence, generalValidator)
    }

    @Test
    fun update() {
        every { reportWorkPlanPersistence.getReportWorkPlanById(PROJECT_ID, reportId = 11) } returnsMany listOf(
                listOf(oldWorkPlan),
                listOf(newWorkPlan),
        )
        every { reportWorkPlanPersistence.updateReportWorkPackage(any(), any()) } answers {}
        every { reportWorkPlanPersistence.updateReportWorkPackageActivity(any(), any(), any()) } answers {}
        every { reportWorkPlanPersistence.updateReportWorkPackageDeliverable(any(), any(), any()) } answers {}
        every { reportWorkPlanPersistence.updateReportWorkPackageOutput(any(), any(), any()) } answers {}

        assertThat(updateWorkPlan.update(PROJECT_ID, reportId = 11L, listOf(updateWorkPlanModel)))
                .containsExactly(newWorkPlan)

        verify(exactly = 1) {
            reportWorkPlanPersistence
                    .updateReportWorkPackage(45L, expectedWpUpdate)
        }
        verify(exactly = 1) {
            reportWorkPlanPersistence
                    .updateReportWorkPackageActivity(99L, ProjectReportWorkPlanStatus.Partly, setOf(InputTranslation(EN, "[99] progress new")))
        }
        verify(exactly = 1) {
            reportWorkPlanPersistence
                    .updateReportWorkPackageDeliverable(87L, BigDecimal.TEN, setOf(InputTranslation(EN, "[87] progress new")))
        }
        verify(exactly = 1) {
            reportWorkPlanPersistence
                    .updateReportWorkPackageOutput(61L, BigDecimal.ONE, setOf(InputTranslation(EN, "[61] progress new")))
        }

        verify(exactly = 2) { reportWorkPlanPersistence.getReportWorkPlanById(PROJECT_ID, reportId = 11L) }
    }

    @Test
    fun `update - no changes`() {
        every { reportWorkPlanPersistence.getReportWorkPlanById(PROJECT_ID, reportId = 12) } returnsMany listOf(
                listOf(oldWorkPlan),
                listOf(oldWorkPlan),
        )
        every { reportWorkPlanPersistence.updateReportWorkPackage(any(), any()) } answers {}
        every { reportWorkPlanPersistence.updateReportWorkPackageActivity(any(), any(), any()) } answers {}
        every { reportWorkPlanPersistence.updateReportWorkPackageDeliverable(any(), any(), any()) } answers {}
        every { reportWorkPlanPersistence.updateReportWorkPackageOutput(any(), any(), any()) } answers {}

        assertThat(updateWorkPlan.update(PROJECT_ID, reportId = 12L, listOf(updateWorkPlanModelNoChanges)))
                .containsExactly(oldWorkPlan)

        verify(exactly = 0) { reportWorkPlanPersistence.updateReportWorkPackage(any(), any()) }
        verify(exactly = 0) { reportWorkPlanPersistence.updateReportWorkPackageActivity(any(), any(), any()) }
        verify(exactly = 0) { reportWorkPlanPersistence.updateReportWorkPackageDeliverable(any(), any(), any()) }
        verify(exactly = 0) { reportWorkPlanPersistence.updateReportWorkPackageOutput(any(), any(), any()) }

        verify(exactly = 2) { reportWorkPlanPersistence.getReportWorkPlanById(PROJECT_ID, reportId = 12L) }
    }

}
