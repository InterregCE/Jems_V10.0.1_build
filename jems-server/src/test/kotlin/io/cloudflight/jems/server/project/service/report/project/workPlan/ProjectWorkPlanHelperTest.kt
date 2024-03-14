package io.cloudflight.jems.server.project.service.report.project.workPlan

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.*
import java.math.BigDecimal
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag.Gray
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag.Green
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanFlag.Yellow

class ProjectWorkPlanHelperTest : UnitTest() {

    private val investment1 = ProjectReportWorkPackageInvestment(
        id = 1L,
        number = 1,
        title = emptySet(),
        deactivated = false,
        period = null,
        nutsRegion3 = null,
        previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        progress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress new")),
        status = ProjectReportWorkPlanInvestmentStatus.Finalized,
        previousStatus = null
    )

    private val investment2= ProjectReportWorkPackageInvestment(
        id = 2L,
        number = 2,
        title = emptySet(),
        deactivated = false,
        period = null,
        nutsRegion3 = null,
        previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        progress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress new")),
        status = ProjectReportWorkPlanInvestmentStatus.Finalized,
        previousStatus = ProjectReportWorkPlanInvestmentStatus.Finalized
    )

    private val investment3 = ProjectReportWorkPackageInvestment(
        id = 3L,
        number = 3,
        title = emptySet(),
        deactivated = false,
        period = null,
        nutsRegion3 = null,
        previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        progress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        status = ProjectReportWorkPlanInvestmentStatus.Finalized,
        previousStatus = ProjectReportWorkPlanInvestmentStatus.Finalized
    )

    private val output = ProjectReportWorkPackageOutput(
        id = 1L,
        number = 1,
        title = emptySet(),
        deactivated = false,
        outputIndicator = null,
        period = null,
        targetValue = BigDecimal.ZERO,
        previousCurrentReport = BigDecimal.ZERO,
        currentReport = BigDecimal.ZERO,
        previouslyReported = BigDecimal.ZERO,
        attachment = null,
        previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        progress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress new"))
    )

    private val deliverableOne = ProjectReportWorkPackageActivityDeliverable(
        id = 1L,
        number = 1,
        title = emptySet(),
        deactivated = false,
        period = null,
        previouslyReported = BigDecimal.ZERO,
        previousCurrentReport = BigDecimal.ZERO,
        currentReport = BigDecimal.ZERO,
        previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        progress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress new")),
        attachment = null
    )

    private val activityOne = ProjectReportWorkPackageActivity(
        id = 1L,
        number = 1,
        title = emptySet(),
        deactivated = false,
        startPeriod = null,
        endPeriod = null,
        previousStatus = ProjectReportWorkPlanStatus.Partly,
        status = ProjectReportWorkPlanStatus.Fully,
        previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        progress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        attachment = null,
        deliverables = emptyList(),
        activityStatusLabel = null
    )

    private val activityTwo = ProjectReportWorkPackageActivity(
        id = 2L,
        number = 2,
        title = emptySet(),
        deactivated = false,
        startPeriod = null,
        endPeriod = null,
        previousStatus = ProjectReportWorkPlanStatus.Fully,
        status = ProjectReportWorkPlanStatus.Fully,
        previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        progress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        attachment = null,
        deliverables = listOf(deliverableOne),
        activityStatusLabel = null
    )

    private val activityThree = ProjectReportWorkPackageActivity(
        id = 3L,
        number = 3,
        title = emptySet(),
        deactivated = false,
        startPeriod = null,
        endPeriod = null,
        previousStatus = ProjectReportWorkPlanStatus.Fully,
        status = ProjectReportWorkPlanStatus.Fully,
        previousProgress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        progress = setOf(InputTranslation(SystemLanguage.EN, "[1] progress")),
        attachment = null,
        deliverables = emptyList(),
        activityStatusLabel = null
    )

    private val projectReportWorkPackage = ProjectReportWorkPackage(
        id = 1L,
        number = 1,
        deactivated = false,
        specificObjective = emptySet(),
        previousSpecificStatus = ProjectReportWorkPlanStatus.Partly,
        specificStatus = ProjectReportWorkPlanStatus.Fully,
        previousSpecificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] specificExplanation")),
        specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] specificExplanation new")),
        communicationObjective = emptySet(),
        previousCommunicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] communicationExplanation")),
        previousCommunicationStatus = ProjectReportWorkPlanStatus.Fully,
        communicationStatus = ProjectReportWorkPlanStatus.Fully,
        communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] communicationExplanation")),
        previousCompleted = true,
        completed = true,
        previousDescription = setOf(InputTranslation(SystemLanguage.EN, "[1] description")),
        description = setOf(InputTranslation(SystemLanguage.EN, "[1] description")),
        activities = listOf(activityOne, activityTwo, activityThree),
        outputs = listOf(output),
        investments = listOf(investment1, investment2, investment3),
        specificStatusLabel = null,
        communicationStatusLabel = null,
        workPlanStatusLabel = null
    )

    private val projectReportWorkPackageTwo = ProjectReportWorkPackage(
        id = 2L,
        number = 2,
        deactivated = false,
        specificObjective = emptySet(),
        previousSpecificStatus = ProjectReportWorkPlanStatus.Fully,
        specificStatus = ProjectReportWorkPlanStatus.Fully,
        previousSpecificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] specificExplanation")),
        specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] specificExplanation new")),
        communicationObjective = emptySet(),
        previousCommunicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] communicationExplanation")),
        previousCommunicationStatus = ProjectReportWorkPlanStatus.Partly,
        communicationStatus = ProjectReportWorkPlanStatus.Fully,
        communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] communicationExplanation")),
        previousCompleted = false,
        completed = true,
        previousDescription = setOf(InputTranslation(SystemLanguage.EN, "[1] description")),
        description = setOf(InputTranslation(SystemLanguage.EN, "[1] description")),
        activities = emptyList(),
        outputs = emptyList(),
        investments = emptyList(),
        specificStatusLabel = null,
        communicationStatusLabel = null,
        workPlanStatusLabel = null
    )

    private val projectReportWorkPackageThree = ProjectReportWorkPackage(
        id = 3L,
        number = 3,
        deactivated = false,
        specificObjective = emptySet(),
        previousSpecificStatus = ProjectReportWorkPlanStatus.Fully,
        specificStatus = ProjectReportWorkPlanStatus.Fully,
        previousSpecificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] specificExplanation")),
        specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] specificExplanation")),
        communicationObjective = emptySet(),
        previousCommunicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] communicationExplanation")),
        previousCommunicationStatus = ProjectReportWorkPlanStatus.Fully,
        communicationStatus = ProjectReportWorkPlanStatus.Fully,
        communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] communicationExplanation")),
        previousCompleted = true,
        completed = true,
        previousDescription = setOf(InputTranslation(SystemLanguage.EN, "[1] description")),
        description = setOf(InputTranslation(SystemLanguage.EN, "[1] description")),
        activities = emptyList(),
        outputs = emptyList(),
        investments = emptyList(),
        specificStatusLabel = null,
        communicationStatusLabel = null,
        workPlanStatusLabel = null
    )

    private val projectReportWorkPackageFourth = ProjectReportWorkPackage(
        id = 4L,
        number = 4,
        deactivated = false,
        specificObjective = emptySet(),
        previousSpecificStatus = ProjectReportWorkPlanStatus.Fully,
        specificStatus = ProjectReportWorkPlanStatus.Fully,
        previousSpecificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] specificExplanation")),
        specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] specificExplanation")),
        communicationObjective = emptySet(),
        previousCommunicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] communicationExplanation")),
        previousCommunicationStatus = ProjectReportWorkPlanStatus.Fully,
        communicationStatus = ProjectReportWorkPlanStatus.Fully,
        communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[1] communicationExplanation new")),
        previousCompleted = true,
        completed = true,
        previousDescription = setOf(InputTranslation(SystemLanguage.EN, "[1] description")),
        description = setOf(InputTranslation(SystemLanguage.EN, "[1] description")),
        activities = emptyList(),
        outputs = emptyList(),
        investments = emptyList(),
        specificStatusLabel = null,
        communicationStatusLabel = null,
        workPlanStatusLabel = null
    )

    @Test
    fun fillInFlags() {
        val workplans = listOf(
            projectReportWorkPackage,
            projectReportWorkPackageTwo,
            projectReportWorkPackageThree,
            projectReportWorkPackageFourth
        ).fillInFlags()
        assertThat(workplans.find { it.id == 1L }?.specificStatusLabel).isEqualTo(Green)
        assertThat(workplans.find { it.id == 1L }?.communicationStatusLabel).isEqualTo(Gray)
        assertThat(workplans.find { it.id == 1L }?.workPlanStatusLabel).isEqualTo(Yellow)
        assertThat(workplans.find { it.id == 1L }?.activities?.find { it.id == 1L }?.activityStatusLabel).isEqualTo(Green)
        assertThat(workplans.find { it.id == 1L }?.activities?.find { it.id == 2L }?.activityStatusLabel).isEqualTo(Yellow)
        assertThat(workplans.find { it.id == 1L }?.activities?.find { it.id == 3L }?.activityStatusLabel).isEqualTo(Gray)
        assertThat(workplans.find { it.id == 1L }?.investments?.find { it.id == 1L }?.statusLabel).isEqualTo(Green)
        assertThat(workplans.find { it.id == 1L }?.investments?.find { it.id == 2L }?.statusLabel).isEqualTo(Yellow)
        assertThat(workplans.find { it.id == 1L }?.investments?.find { it.id == 3L }?.statusLabel).isEqualTo(Gray)
        assertThat(workplans.find { it.id == 2L }?.specificStatusLabel).isEqualTo(Yellow)
        assertThat(workplans.find { it.id == 2L }?.communicationStatusLabel).isEqualTo(Green)
        assertThat(workplans.find { it.id == 2L }?.workPlanStatusLabel).isEqualTo(Green)
        assertThat(workplans.find { it.id == 3L }?.specificStatusLabel).isEqualTo(Gray)
        assertThat(workplans.find { it.id == 3L }?.communicationStatusLabel).isEqualTo(Gray)
        assertThat(workplans.find { it.id == 3L }?.workPlanStatusLabel).isEqualTo(Gray)
        assertThat(workplans.find { it.id == 4L }?.specificStatusLabel).isEqualTo(Gray)
        assertThat(workplans.find { it.id == 4L }?.communicationStatusLabel).isEqualTo(Yellow)
        assertThat(workplans.find { it.id == 4L }?.workPlanStatusLabel).isEqualTo(Yellow)
    }
}
