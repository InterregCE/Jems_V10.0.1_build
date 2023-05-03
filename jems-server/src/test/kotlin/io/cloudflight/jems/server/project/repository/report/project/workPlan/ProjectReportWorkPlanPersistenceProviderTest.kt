package io.cloudflight.jems.server.project.repository.report.project.workPlan

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorTranslEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorTranslEntity
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityDeliverableTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageActivityTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageInvestmentTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageOutputTranslEntity
import io.cloudflight.jems.server.project.entity.report.project.workPlan.ProjectReportWorkPackageTranslEntity
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportOutputLineOverview
import io.cloudflight.jems.server.project.service.report.model.project.identification.overview.ProjectReportResultIndicatorOverview
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageInvestment
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOnlyUpdate
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPlanStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.Optional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectReportWorkPlanPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 362L

        private val dummyAttachment = JemsFileMetadataEntity(
                id = 970L,
                projectId = 4L,
                partnerId = null,
                path = "",
                minioBucket = "",
                minioLocation = "",
                name = "some_file.txt",
                type = mockk(),
                size = 1475,
                user = mockk(),
                uploaded = ZonedDateTime.now(),
                description = "dummy attachment description",
        )

        private fun wp(id: Long, report: ProjectReportEntity) = ProjectReportWorkPackageEntity(
                id = id,
                reportEntity = report,
                number = id.toInt(),
                deactivated = false,
                workPackageId = 548L,
                specificStatus = ProjectReportWorkPlanStatus.Partly,
                communicationStatus = ProjectReportWorkPlanStatus.Fully,
                completed = true,
        ).apply {
            translatedValues.add(
                    ProjectReportWorkPackageTranslEntity(
                            TranslationId(this, SystemLanguage.EN),
                            specificObjective = "[$id] specificObjective",
                            specificExplanation = "[$id] specificExplanation",
                            communicationObjective = "[$id] communicationObjective",
                            communicationExplanation = "[$id] communicationExplanation",
                            description = "[$id] description",
                    ),
            )
        }

        private fun investment(id: Long, wp: ProjectReportWorkPackageEntity) = ProjectReportWorkPackageInvestmentEntity(
                id = id,
                workPackageEntity = wp,
                number = id.toInt(),
                deactivated = false,
                address = null,
                expectedDeliveryPeriod = null,
        ).apply {
            translatedValues.add(
                    ProjectReportWorkPackageInvestmentTranslEntity(
                            TranslationId(this, SystemLanguage.EN),
                            title = "[$id] title",
                            justificationExplanation = "[$id] justificationExplanation",
                            justificationTransactionalRelevance = "[$id] justificationTransactionalRelevance",
                            justificationBenefits = "",
                            justificationPilot = "",
                            risk = "",
                            documentation = "",
                            documentationExpectedImpacts = "",
                            ownershipSiteLocation = "",
                            ownershipRetain = "",
                            ownershipMaintenance = "",
                            progress = "[$id] progress"
                    ),
            )
        }

        private fun activity(id: Long, wp: ProjectReportWorkPackageEntity) = ProjectReportWorkPackageActivityEntity(
                id = id,
                workPackageEntity = wp,
                number = id.toInt(),
                deactivated = false,
                activityId = 587L,
                startPeriodNumber = 2,
                endPeriodNumber = 3,
                status = ProjectReportWorkPlanStatus.Fully,
                attachment = dummyAttachment,
        ).apply {
            translatedValues.add(
                    ProjectReportWorkPackageActivityTranslEntity(
                            TranslationId(this, SystemLanguage.EN),
                            title = "[$id] title",
                            progress = "[$id] progress",
                    ),
            )
        }

        private fun deliverable(id: Long, activity: ProjectReportWorkPackageActivityEntity) = ProjectReportWorkPackageActivityDeliverableEntity(
                id = id,
                activityEntity = activity,
                number = id.toInt(),
                deactivated = true,
                deliverableId = 659L,
                periodNumber = 12,
                previouslyReported = BigDecimal.valueOf(1596L, 2),
                currentReport = BigDecimal.valueOf(2145L, 2),
                attachment = null,
        ).apply {
            translatedValues.add(
                    ProjectReportWorkPackageActivityDeliverableTranslEntity(
                            TranslationId(this, SystemLanguage.EN),
                            title = "[$id] title deliverable",
                            progress = "[$id] progress deliverable",
                    ),
            )
        }

        private fun outputIndicatorEntity(): OutputIndicatorEntity {
            return OutputIndicatorEntity(
                    id = 447L,
                    identifier = "indic ident",
                    code = "indic code",
                    resultIndicatorEntity = resultIndicatorEntity(),
                    programmePriorityPolicyEntity = null,
                    milestone = BigDecimal.valueOf(7556L, 2),
                    finalTarget = BigDecimal.valueOf(7556L, 2),
            ).apply {
                translatedValues.add(
                        OutputIndicatorTranslEntity(
                                TranslationId(this, SystemLanguage.EN),
                                name = "indicator name",
                                measurementUnit = "measurement",
                        ),
                )
            }
        }

        private fun resultIndicatorEntity(): ResultIndicatorEntity {
            return ResultIndicatorEntity(
                    id = 536L,
                    identifier = "result ident",
                    code = "result code",
                    programmePriorityPolicyEntity = null,
                    baseline = BigDecimal.TEN,
            ).apply {
                translatedValues.add(
                        ResultIndicatorTranslEntity(
                                TranslationId(this, SystemLanguage.EN),
                                name = "result name",
                                measurementUnit = "measurement"
                        )
                )
            }
        }

        private fun output(id: Long, wp: ProjectReportWorkPackageEntity) = ProjectReportWorkPackageOutputEntity(
                id = id,
                workPackageEntity = wp,
                number = id.toInt(),
                deactivated = false,
                programmeOutputIndicator = outputIndicatorEntity(),
                periodNumber = 15,
                targetValue = BigDecimal.valueOf(1296L, 2),
                previouslyReported = BigDecimal.valueOf(1577L, 2),
                currentReport = BigDecimal.valueOf(2875L, 2),
                attachment = dummyAttachment,
        ).apply {
            translatedValues.add(
                    ProjectReportWorkPackageOutputTranslEntity(
                            TranslationId(this, SystemLanguage.EN),
                            title = "[$id] output title",
                            progress = "[$id] output progress",
                    )
            )
        }

        private fun expectedWorkPlan(wpId: Long, activityId: Long, deliverableId: Long, outputId: Long, investmentId: Long) =
                ProjectReportWorkPackage(
                        id = wpId,
                        number = wpId.toInt(),
                        deactivated = false,
                        specificObjective = setOf(InputTranslation(SystemLanguage.EN, "[$wpId] specificObjective")),
                        specificStatus = ProjectReportWorkPlanStatus.Partly,
                        specificExplanation = setOf(InputTranslation(SystemLanguage.EN, "[$wpId] specificExplanation")),
                        communicationObjective = setOf(InputTranslation(SystemLanguage.EN, "[$wpId] communicationObjective")),
                        communicationStatus = ProjectReportWorkPlanStatus.Fully,
                        communicationExplanation = setOf(InputTranslation(SystemLanguage.EN, "[$wpId] communicationExplanation")),
                        completed = true,
                        description = setOf(InputTranslation(SystemLanguage.EN, "[$wpId] description")),
                        activities = listOf(
                                ProjectReportWorkPackageActivity(
                                        id = activityId,
                                        number = activityId.toInt(),
                                        title = setOf(InputTranslation(SystemLanguage.EN, "[$activityId] title")),
                                        deactivated = false,
                                        startPeriod = ProjectPeriod(2, 3, 4),
                                        endPeriod = ProjectPeriod(3, 5, 6),
                                        status = ProjectReportWorkPlanStatus.Fully,
                                        progress = setOf(InputTranslation(SystemLanguage.EN, "[$activityId] progress")),
                                        attachment = JemsFileMetadata(dummyAttachment.id, dummyAttachment.name, dummyAttachment.uploaded),
                                        deliverables = listOf(
                                                ProjectReportWorkPackageActivityDeliverable(
                                                        id = deliverableId,
                                                        number = deliverableId.toInt(),
                                                        title = setOf(InputTranslation(SystemLanguage.EN, "[$deliverableId] title deliverable")),
                                                        deactivated = true,
                                                        period = ProjectPeriod(12, 23, 24),
                                                        previouslyReported = BigDecimal.valueOf(1596L, 2),
                                                        currentReport = BigDecimal.valueOf(2145L, 2),
                                                        progress = setOf(InputTranslation(SystemLanguage.EN, "[$deliverableId] progress deliverable")),
                                                        attachment = null,
                                                ),
                                        ),
                                ),
                        ),
                        outputs = listOf(
                                ProjectReportWorkPackageOutput(
                                        id = outputId,
                                        number = outputId.toInt(),
                                        title = setOf(InputTranslation(SystemLanguage.EN, "[$outputId] output title")),
                                        deactivated = false,
                                        outputIndicator = OutputIndicatorSummary(
                                                447L, "indic ident", "indic code",
                                                name = setOf(InputTranslation(SystemLanguage.EN, "indicator name")),
                                                null, setOf(InputTranslation(SystemLanguage.EN, "measurement"))
                                        ),
                                        period = ProjectPeriod(15, 29, 30),
                                        targetValue = BigDecimal.valueOf(1296L, 2),
                                        previouslyReported = BigDecimal.valueOf(1577L, 2),
                                        currentReport = BigDecimal.valueOf(2875L, 2),
                                        progress = setOf(InputTranslation(SystemLanguage.EN, "[$outputId] output progress")),
                                        attachment = JemsFileMetadata(dummyAttachment.id, dummyAttachment.name, dummyAttachment.uploaded),
                                ),
                        ),
                        investments = listOf(
                                ProjectReportWorkPackageInvestment(
                                        id = investmentId,
                                        number = investmentId.toInt(),
                                        title = setOf(InputTranslation(language = SystemLanguage.EN, translation = "[$investmentId] title")),
                                        deactivated = false,
                                        period = null,
                                        nutsRegion3 = null,
                                        progress = setOf(InputTranslation(language = SystemLanguage.EN, translation = "[$investmentId] progress"))
                                )
                        )
                )

        private fun expectedOutput(outputId: Long) = listOf(
                ProjectReportOutputLineOverview(
                        number = outputId.toInt(),
                        workPackageNumber = 15,
                        name = setOf(InputTranslation(SystemLanguage.EN, "[$outputId] output title")),
                        measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "measurement")),
                        deactivated = false,
                        outputIndicator = outputIndicator,
                        targetValue = BigDecimal.valueOf(1296, 2),
                        previouslyReported = BigDecimal.valueOf(1577, 2),
                        currentReport = BigDecimal.valueOf(2875, 2),
                )
        )

        private val resultIndicator = ProjectReportResultIndicatorOverview(
                id = 536L,
                identifier = "result ident",
                name = setOf(InputTranslation(SystemLanguage.EN, "result name")),
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "measurement")),
                baseline = BigDecimal.valueOf(10),
                targetValue = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                currentReport = BigDecimal.ZERO,
        )

        private val outputIndicator = ProjectReportOutputIndicatorOverview(
                id = 447,
                identifier = "indic ident",
                name = setOf(InputTranslation(SystemLanguage.EN, "indicator name")),
                measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "measurement")),
                resultIndicator = resultIndicator,
                targetValue = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                currentReport = BigDecimal.ZERO,
        )

    }

    @MockK
    lateinit var reportRepository: ProjectReportRepository

    @MockK
    lateinit var workPlanRepository: ProjectReportWorkPackageRepository

    @MockK
    lateinit var workPlanInvestmentRepository: ProjectReportWorkPackageInvestmentRepository

    @MockK
    lateinit var workPlanActivityRepository: ProjectReportWorkPackageActivityRepository

    @MockK
    lateinit var workPlanActivityDeliverableRepository: ProjectReportWorkPackageActivityDeliverableRepository

    @MockK
    lateinit var workPlanOutputRepository: ProjectReportWorkPackageOutputRepository

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var fileService: JemsProjectFileService

    @InjectMockKs
    lateinit var persistence: ProjectReportWorkPlanPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(
                reportRepository,
                workPlanRepository,
                workPlanActivityRepository,
                workPlanActivityDeliverableRepository,
                workPlanOutputRepository,
                projectPersistence,
                fileService
        )
    }

    @Test
    fun getReportWorkPlanById() {
        val report = mockk<ProjectReportEntity>()
        every { report.applicationFormVersion } returns "4.12.0"
        every { reportRepository.getByIdAndProjectId(id = 14L, projectId = PROJECT_ID) } returns report

        val workPackage = wp(id = 15L, report = report)
        val activity = activity(id = 18L, wp = workPackage)
        val deliverable = deliverable(id = 27L, activity = activity)
        val output = output(id = 35L, wp = workPackage)
        val investment = investment(id = 72L, wp = workPackage)

        every { workPlanInvestmentRepository.findAllByWorkPackageEntityReportEntityOrderByNumber(report) } returns mutableListOf(investment)
        every { workPlanActivityRepository.findAllByWorkPackageEntityReportEntityOrderByNumber(report) } returns
                mutableListOf(activity)
        every { workPlanActivityDeliverableRepository.findAllByActivityEntityWorkPackageEntityReportEntityOrderByNumber(report) } returns
                mutableListOf(deliverable)
        every { workPlanOutputRepository.findAllByWorkPackageEntityReportEntityOrderByNumber(report) } returns
                mutableListOf(output)
        every { workPlanRepository.findAllByReportEntityOrderByNumber(report) } returns
                mutableListOf(workPackage)
        every { projectPersistence.getProjectPeriods(PROJECT_ID, "4.12.0") } returns listOf(
                ProjectPeriod(2, 3, 4),
                ProjectPeriod(3, 5, 6),
                ProjectPeriod(12, 23, 24),
                ProjectPeriod(15, 29, 30),
        )

        assertThat(persistence.getReportWorkPlanById(projectId = PROJECT_ID, reportId = 14L))
                .containsExactly(expectedWorkPlan(wpId = 15L, activityId = 18L, deliverableId = 27L, outputId = 35L, investmentId = 72L))
    }

    @Test
    fun getReportWorkPackageOutputsById() {
        val report = mockk<ProjectReportEntity>()
        every { report.applicationFormVersion } returns "5.13.1"
        every { reportRepository.getByIdAndProjectId(id = 14L, projectId = PROJECT_ID) } returns report

        val workPackage = wp(id = 15L, report = report)
        val output = output(id = 35L, wp = workPackage)

        every { workPlanOutputRepository.findAllByWorkPackageEntityReportEntityOrderByNumber(report) } returns
                mutableListOf(output)

        assertThat(persistence.getReportWorkPackageOutputsById(projectId = PROJECT_ID, reportId = 14L))
                .usingRecursiveComparison().isEqualTo(expectedOutput(outputId = 35L))
    }

    @Test
    fun existsByActivityId() {
        val reportId = 200L
        val wpId = 220L
        every { workPlanActivityRepository.existsByActivityId(15L, wpId, reportId = reportId, PROJECT_ID) } returns true
        assertThat(persistence.existsByActivityId(PROJECT_ID, reportId = reportId, wpId, activityId = 15L)).isTrue
    }

    @Test
    fun existsByDeliverableId() {
        val reportId = 201L
        val wpId = 221L
        every {
            workPlanActivityDeliverableRepository
                    .existsByDeliverableId(deliverableId = 150L, 15L, wpId, reportId, PROJECT_ID)
        } returns true
        assertThat(persistence.existsByDeliverableId(PROJECT_ID, reportId = reportId, wpId, activityId = 15L, deliverableId = 150L)).isTrue
    }

    @Test
    fun existsByOutputId() {
        val reportId = 202L
        val wpId = 222L
        every { workPlanOutputRepository.existsByOutputId(17L, wpId, reportId = reportId, PROJECT_ID) } returns false
        assertThat(persistence.existsByOutputId(PROJECT_ID, reportId = reportId, wpId, outputId = 17L)).isFalse
    }

    @Test
    fun updateReportWorkPackage() {
        val wp = wp(id = 45L, report = mockk())
        every { workPlanRepository.findById(45L) } returns Optional.of(wp)

        val data = ProjectReportWorkPackageOnlyUpdate(
                specificStatus = ProjectReportWorkPlanStatus.Not,
                specificExplanation = setOf(
                        InputTranslation(SystemLanguage.EN, "language already present spec"),
                        InputTranslation(SystemLanguage.SK, "new language spec"),
                        InputTranslation(SystemLanguage.DE, null),
                ),
                communicationStatus = ProjectReportWorkPlanStatus.Not,
                communicationExplanation = setOf(
                        InputTranslation(SystemLanguage.EN, "language already present comm"),
                        InputTranslation(SystemLanguage.SK, "new language comm"),
                        InputTranslation(SystemLanguage.DE, null),
                ),
                completed = false,
                description = setOf(
                        InputTranslation(SystemLanguage.EN, "language already present desc"),
                        InputTranslation(SystemLanguage.SK, "new language desc"),
                        InputTranslation(SystemLanguage.DE, null),
                ),
        )

        assertThat(wp.specificStatus).isNotEqualTo(ProjectReportWorkPlanStatus.Not)
        assertThat(wp.communicationStatus).isNotEqualTo(ProjectReportWorkPlanStatus.Not)
        assertThat(wp.completed).isTrue()

        persistence.updateReportWorkPackage(workPackageId = 45L, data)

        assertThat(wp.specificStatus).isEqualTo(ProjectReportWorkPlanStatus.Not)
        assertThat(wp.translatedValues.map { Pair(it.language(), it.specificExplanation) }).containsExactly(
                Pair(SystemLanguage.EN, "language already present spec"),
                Pair(SystemLanguage.SK, "new language spec"),
                Pair(SystemLanguage.DE, ""),
        )
        assertThat(wp.communicationStatus).isEqualTo(ProjectReportWorkPlanStatus.Not)
        assertThat(wp.translatedValues.map { Pair(it.language(), it.communicationExplanation) }).containsExactly(
                Pair(SystemLanguage.EN, "language already present comm"),
                Pair(SystemLanguage.SK, "new language comm"),
                Pair(SystemLanguage.DE, ""),
        )
        assertThat(wp.completed).isFalse()
        assertThat(wp.translatedValues.map { Pair(it.language(), it.description) }).containsExactly(
                Pair(SystemLanguage.EN, "language already present desc"),
                Pair(SystemLanguage.SK, "new language desc"),
                Pair(SystemLanguage.DE, ""),
        )
    }

    @Test
    fun updateReportWorkPackageActivity() {
        val activity = activity(id = 99L, wp = mockk())
        every { workPlanActivityRepository.findById(99L) } returns Optional.of(activity)

        val progress = setOf(
                InputTranslation(SystemLanguage.EN, "language already present"),
                InputTranslation(SystemLanguage.SK, "new language"),
                InputTranslation(SystemLanguage.DE, null),
        )
        assertThat(activity.status).isNotEqualTo(ProjectReportWorkPlanStatus.Partly)

        persistence.updateReportWorkPackageActivity(activityId = 99L, ProjectReportWorkPlanStatus.Partly, progress)

        assertThat(activity.status).isEqualTo(ProjectReportWorkPlanStatus.Partly)
        assertThat(activity.translatedValues.map { Pair(it.language(), it.progress) }).containsExactly(
                Pair(SystemLanguage.EN, "language already present"),
                Pair(SystemLanguage.SK, "new language"),
                Pair(SystemLanguage.DE, ""),
        )
    }

    @Test
    fun updateReportWorkPackageDeliverable() {
        val deliverable = deliverable(id = 64L, activity = mockk())
        every { workPlanActivityDeliverableRepository.findById(64L) } returns Optional.of(deliverable)

        val progress = setOf(
                InputTranslation(SystemLanguage.EN, "language already present"),
                InputTranslation(SystemLanguage.SK, "new language"),
                InputTranslation(SystemLanguage.DE, null),
        )
        persistence.updateReportWorkPackageDeliverable(
                deliverableId = 64L,
                currentReport = BigDecimal.TEN,
                progress = progress,
        )

        assertThat(deliverable.currentReport).isEqualTo(BigDecimal.TEN)
        assertThat(deliverable.translatedValues.map { Pair(it.language(), it.progress) }).containsExactly(
                Pair(SystemLanguage.EN, "language already present"),
                Pair(SystemLanguage.SK, "new language"),
                Pair(SystemLanguage.DE, ""),
        )
    }

    @Test
    fun updateReportWorkPackageOutput() {
        val output = output(id = 38L, wp = mockk())
        every { workPlanOutputRepository.findById(38L) } returns Optional.of(output)

        val progress = setOf(
                InputTranslation(SystemLanguage.EN, "language already present"),
                InputTranslation(SystemLanguage.SK, "new language"),
                InputTranslation(SystemLanguage.DE, null),
        )
        persistence.updateReportWorkPackageOutput(
                outputId = 38L,
                currentReport = BigDecimal.TEN,
                progress = progress,
        )

        assertThat(output.currentReport).isEqualTo(BigDecimal.TEN)
        assertThat(output.translatedValues.map { Pair(it.language(), it.progress) }).containsExactly(
                Pair(SystemLanguage.EN, "language already present"),
                Pair(SystemLanguage.SK, "new language"),
                Pair(SystemLanguage.DE, ""),
        )
    }
}
