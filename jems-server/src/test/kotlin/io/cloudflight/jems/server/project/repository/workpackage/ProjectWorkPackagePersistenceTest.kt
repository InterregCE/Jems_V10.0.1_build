package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.repository.indicator.OutputIndicatorRepository
import io.cloudflight.jems.server.project.entity.TranslationWorkPackageId
import io.cloudflight.jems.server.project.entity.TranslationWorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageRow
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityRow
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationId
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableId
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationId
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageDeliverableRow
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputRow
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputTransl
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.workpackage.investment.WorkPackageInvestmentRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.WorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.project
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverableTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockKExtension::class)
class ProjectWorkPackagePersistenceTest {

    companion object {
        private const val WORK_PACKAGE_ID = 1L
        private const val WORK_PACKAGE_ID_2 = 654L
        private const val INVESTMENT_ID = 54L
        private const val PROJECT_ID = 64L

        private val activityId1 = WorkPackageActivityId(workPackageId = WORK_PACKAGE_ID, activityNumber = 1)
        private val activityId2 = WorkPackageActivityId(workPackageId = WORK_PACKAGE_ID, activityNumber = 2)
        private val outputId1 = WorkPackageOutputId(workPackageId = WORK_PACKAGE_ID, outputNumber = 1)
        private val outputId2 = WorkPackageOutputId(workPackageId = WORK_PACKAGE_ID, outputNumber = 2)

        private fun trIdAct(activityId: WorkPackageActivityId, lang: SystemLanguage) = WorkPackageActivityTranslationId(
            activityId = activityId,
            language = lang
        )

        private fun trIdActDel(deliverableId: WorkPackageActivityDeliverableId, lang: SystemLanguage) =
            WorkPackageActivityDeliverableTranslationId(
                deliverableId = deliverableId,
                language = lang
            )

        private fun trIdOut(outputId: WorkPackageOutputId, lang: SystemLanguage) = TranslationWorkPackageOutputId(
            workPackageOutputId = outputId,
            language = lang,
        )

        private val deliverableId1_activityId1 =
            WorkPackageActivityDeliverableId(activityId = activityId1, deliverableNumber = 1)
        private val deliverableId2_activityId1 =
            WorkPackageActivityDeliverableId(activityId = activityId1, deliverableNumber = 2)

        private val deliverableId1_activityId2 =
            WorkPackageActivityDeliverableId(activityId = activityId2, deliverableNumber = 1)
        private val deliverableId2_activityId2 =
            WorkPackageActivityDeliverableId(activityId = activityId2, deliverableNumber = 2)
        private val deliverableId3_activityId2 = WorkPackageActivityDeliverableId(
            activityId = activityId2,
            deliverableNumber = 3
        )

        val deliverable2_2 = WorkPackageActivityDeliverableEntity(
            deliverableId = deliverableId2_activityId1,
            startPeriod = 2
        )
        val deliverable2_1 = WorkPackageActivityDeliverableEntity(
            deliverableId = deliverableId1_activityId1,
            startPeriod = 1,
            translatedValues = setOf(
                WorkPackageActivityDeliverableTranslationEntity(
                    translationId = trIdActDel(deliverableId1_activityId1, SK),
                    description = "sk_deliverable_desc"
                ),
                WorkPackageActivityDeliverableTranslationEntity(
                    translationId = trIdActDel(deliverableId1_activityId1, CS),
                    description = ""
                ),
                WorkPackageActivityDeliverableTranslationEntity(
                    translationId = trIdActDel(deliverableId1_activityId1, EN),
                    description = null
                )
            )
        )

        val activity1 = WorkPackageActivityEntity(
            activityId = activityId1,
            startPeriod = 4,
            endPeriod = 6
        )
        val activity1_model = WorkPackageActivity(
            workPackageId = 1L,
            activityNumber = 1,
            startPeriod = 4,
            endPeriod = 6
        )
        val activity2 = WorkPackageActivityEntity(
            activityId = activityId2,
            startPeriod = 1,
            endPeriod = 3,
            translatedValues = setOf(
                WorkPackageActivityTranslationEntity(
                    translationId = trIdAct(activityId1, SK),
                    title = "sk_title",
                    description = ""
                ),
                WorkPackageActivityTranslationEntity(
                    translationId = trIdAct(activityId1, CS),
                    title = null,
                    description = "cs_desc"
                ),
                WorkPackageActivityTranslationEntity(
                    translationId = trIdAct(activityId1, EN),
                    title = " ",
                    description = " "
                )
            ),
            deliverables = setOf(deliverable2_2, deliverable2_1)
        )
        val activity2_model = WorkPackageActivity(
            workPackageId = 1L,
            activityNumber = 2,
            translatedValues = setOf(
                WorkPackageActivityTranslatedValue(language = SK, title = "sk_title", description = ""),
                WorkPackageActivityTranslatedValue(language = CS, title = null, description = "cs_desc"),
                WorkPackageActivityTranslatedValue(language = EN, title = " ", description = " ")
            ),
            startPeriod = 1,
            endPeriod = 3,
            deliverables = listOf(
                WorkPackageActivityDeliverable(
                    deliverableNumber = 1,
                    period = 1,
                    translatedValues = setOf(
                        WorkPackageActivityDeliverableTranslatedValue(
                            language = SK,
                            description = "sk_deliverable_desc"
                        ),
                        WorkPackageActivityDeliverableTranslatedValue(language = CS, description = ""),
                        WorkPackageActivityDeliverableTranslatedValue(language = EN, description = null)
                    )
                ),
                WorkPackageActivityDeliverable(
                    deliverableNumber = 2,
                    period = 2
                )
            )
        )

        const val INDICATOR_ID = 30L
        val indicatorOutput = OutputIndicatorEntity(
            id = INDICATOR_ID,
            identifier = "ID.30",
            code = "tst",
            programmePriorityPolicyEntity = null,
            resultIndicatorEntity = null,
            translatedValues = mutableSetOf()
        )

        val output1 = WorkPackageOutputEntity(
            outputId = outputId1,
            periodNumber = 1,
            programmeOutputIndicatorEntity = indicatorOutput,
        )
        val output1_model = WorkPackageOutput(
            workPackageId = 1L,
            outputNumber = 1,
            periodNumber = 1,
            programmeOutputIndicatorId = INDICATOR_ID,
            programmeOutputIndicatorIdentifier = "ID.30"
        )
        val output2 = WorkPackageOutputEntity(
            outputId = outputId2,
            periodNumber = 2,
        )
        val output2_model = WorkPackageOutput(
            workPackageId = 1L,
            outputNumber = 2,
            periodNumber = 2,
        )

        val workPackageWithActivities = WorkPackageEntity(
            id = WORK_PACKAGE_ID,
            project = project,
            number = 1,
            activities = listOf(activity2, activity1), // for testing sorting
            translatedValues = setOf(
                WorkPackageTransl(
                    translationId = TranslationWorkPackageId(WORK_PACKAGE_ID, CS),
                    name = "WP CS name"
                )
            )
        )

        val activity = WorkPackageActivity(
            workPackageId = 1L,
            translatedValues = setOf(
                WorkPackageActivityTranslatedValue(language = EN, title = null, description = "en_desc"),
                WorkPackageActivityTranslatedValue(language = CS, title = "", description = null),
                WorkPackageActivityTranslatedValue(language = SK, title = "sk_title", description = "sk_desc")
            ),
            startPeriod = 1,
            endPeriod = 3,
            deliverables = listOf(
                WorkPackageActivityDeliverable(
                    period = 1,
                    translatedValues = setOf(
                        WorkPackageActivityDeliverableTranslatedValue(language = EN, description = "en_deliv_desc"),
                        WorkPackageActivityDeliverableTranslatedValue(language = CS, description = null)
                    )
                )
            )
        )

        val output = WorkPackageOutput(
            workPackageId = 1L,
            outputNumber = 1,
            translatedValues = setOf(
                WorkPackageOutputTranslatedValue(language = EN, title = null, description = "en_desc"),
                WorkPackageOutputTranslatedValue(language = CS, title = "", description = null),
                WorkPackageOutputTranslatedValue(language = SK, title = "sk_title", description = "sk_desc"),
            ),
            periodNumber = 3,
            programmeOutputIndicatorId = INDICATOR_ID,
            targetValue = BigDecimal.TEN
        )

        val workPackageWithOutputs = WorkPackageEntity(
            id = WORK_PACKAGE_ID,
            project = project,
            outputs = listOf(output2, output1),
        )

        val workPackageInvestment = WorkPackageInvestment(
            id = null,
            investmentNumber = 3,
            address = null
        )
    }

    @MockK
    lateinit var repository: WorkPackageRepository

    @MockK
    lateinit var repositoryActivity: WorkPackageActivityRepository

    @MockK
    lateinit var repositoryOutput: WorkPackageOutputRepository

    @RelaxedMockK
    lateinit var investmentRepository: WorkPackageInvestmentRepository

    @RelaxedMockK
    lateinit var outputIndicatorRepository: OutputIndicatorRepository

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    private lateinit var projectVersionUtils: ProjectVersionUtils

    private lateinit var persistence: WorkPackagePersistenceProvider

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = WorkPackagePersistenceProvider(
            repository,
            repositoryActivity,
            repositoryOutput,
            investmentRepository,
            outputIndicatorRepository,
            projectVersionUtils
        )
    }

    @Test
    fun `get full-rich work packages`() {
        val emptyWP = WorkPackageEntity(
            id = WORK_PACKAGE_ID_2,
            project = project,
            number = 2,
        )
        every { repository.findAllByProjectId(eq(1)) } returns
            listOf(
                workPackageWithActivities.copy(activities = emptyList(), outputs = emptyList()),
                emptyWP
            )
        every {
            repositoryActivity.findAllByActivityIdWorkPackageIdIn(
                setOf(
                    WORK_PACKAGE_ID,
                    WORK_PACKAGE_ID_2
                )
            )
        } returns listOf(activity2, activity1)
        every {
            repositoryOutput.findAllByOutputIdWorkPackageIdIn(
                setOf(
                    WORK_PACKAGE_ID,
                    WORK_PACKAGE_ID_2
                )
            )
        } returns listOf(output2, output1)

        val result = persistence.getWorkPackagesWithOutputsAndActivitiesByProjectId(1L, null)
        assertThat(result.size).isEqualTo(2)
        assertThat(result.map { it.id }).containsExactly(WORK_PACKAGE_ID, WORK_PACKAGE_ID_2)
        assertThat(result.map { it.workPackageNumber }).containsExactly(1, 2)
        assertThat(result[0].translatedValues).containsExactly(ProjectWorkPackageTranslatedValue(CS, "WP CS name"))
        assertThat(result[0].activities).containsExactly(activity1_model, activity2_model)
        assertThat(result[0].outputs).containsExactly(output1_model, output2_model)
    }

    @Test
    fun `get full-rich work packages for previous version`() {
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val version = "3.0"
        val id = 1L
        val wpId = 2L
        val mockWPRow: WorkPackageRow = mockk()
        every { mockWPRow.id } returns wpId
        every { mockWPRow.language } returns EN
        every { mockWPRow.name } returns "name"
        every { mockWPRow.number } returns 3
        every { mockWPRow.specificObjective } returns "specificObjective"
        every { mockWPRow.objectiveAndAudience } returns "objectiveAndAudience"
        val mockWPARow: WorkPackageActivityRow = mockk()
        every { mockWPARow.workPackageId } returns wpId
        every { mockWPARow.activityNumber } returns 3
        every { mockWPARow.language } returns EN
        every { mockWPARow.startPeriod } returns 1
        every { mockWPARow.endPeriod } returns 2
        every { mockWPARow.title } returns "title"
        every { mockWPARow.description } returns "description"
        val mockWPDRow: WorkPackageDeliverableRow = mockk()
        every { mockWPDRow.deliverableNumber } returns 4
        every { mockWPDRow.language } returns EN
        every { mockWPDRow.startPeriod } returns 1
        every { mockWPDRow.description } returns "description"
        val mockWPORow: WorkPackageOutputRow = mockk()
        every { mockWPORow.workPackageId } returns wpId
        every { mockWPORow.outputNumber } returns 5
        every { mockWPORow.language } returns EN
        every { mockWPORow.programmeOutputIndicatorId } returns 1L
        every { mockWPORow.programmeOutputIndicatorIdentifier } returns "programmeOutputIndicatorIdentifier"
        every { mockWPORow.targetValue } returns BigDecimal.TEN
        every { mockWPORow.periodNumber } returns 1
        every { mockWPORow.title } returns "title"
        every { mockWPORow.description } returns "description"

        every { projectVersionRepo.findTimestampByVersion(id, version) } returns timestamp
        every { repository.findAllByProjectIdAsOfTimestamp(id, timestamp) } returns listOf(mockWPRow)
        every {
            repositoryActivity.findAllByActivityIdWorkPackageIdAsOfTimestamp(
                setOf(wpId),
                timestamp
            )
        } returns listOf(mockWPARow)
        every {
            repositoryActivity.findAllDeliverablesByWorkPackageIdAndActivityIdAsOfTimestamp(
                wpId,
                3,
                timestamp
            )
        } returns listOf(mockWPDRow)
        every { repositoryOutput.findAllByOutputIdWorkPackageIdAsOfTimestamp(setOf(wpId), timestamp) } returns listOf(
            mockWPORow
        )

        // test
        val result = persistence.getWorkPackagesWithOutputsAndActivitiesByProjectId(id, version)

        assertThat(result.size).isEqualTo(1)
        assertThat(result[0]).isEqualTo(
            ProjectWorkPackage(
                id = mockWPRow.id,
                workPackageNumber = mockWPRow.number!!,
                translatedValues = setOf(
                    ProjectWorkPackageTranslatedValue(
                        language = EN,
                        name = mockWPRow.name,
                        specificObjective = mockWPRow.specificObjective,
                        objectiveAndAudience = mockWPRow.objectiveAndAudience
                    )
                ),
                activities = listOf(
                    WorkPackageActivity(
                        activityNumber = mockWPARow.activityNumber,
                        workPackageId = mockWPARow.workPackageId,
                        translatedValues = setOf(
                            WorkPackageActivityTranslatedValue(
                                EN,
                                mockWPARow.title,
                                mockWPARow.description
                            )
                        ),
                        startPeriod = mockWPARow.startPeriod,
                        endPeriod = mockWPARow.endPeriod,
                        deliverables = listOf(
                            WorkPackageActivityDeliverable(
                                deliverableNumber = mockWPDRow.deliverableNumber,
                                translatedValues = setOf(
                                    WorkPackageActivityDeliverableTranslatedValue(
                                        EN,
                                        mockWPDRow.description
                                    )
                                ),
                                period = mockWPDRow.startPeriod
                            )
                        )
                    )
                ),
                outputs = listOf(
                    WorkPackageOutput(
                        workPackageId = mockWPORow.workPackageId,
                        outputNumber = mockWPORow.outputNumber,
                        programmeOutputIndicatorId = mockWPORow.programmeOutputIndicatorId,
                        programmeOutputIndicatorIdentifier = mockWPORow.programmeOutputIndicatorIdentifier,
                        targetValue = mockWPORow.targetValue,
                        periodNumber = mockWPORow.periodNumber,
                        translatedValues = setOf(
                            WorkPackageOutputTranslatedValue(
                                EN,
                                mockWPORow.title,
                                mockWPORow.description
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `get work package activities - not-existing work package`() {
        every { repository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getWorkPackageActivitiesForWorkPackage(-1, 1L) }
        assertThat(ex.entity).isEqualTo("workPackage")
    }

    @Test
    fun `work package activities and deliverables are correctly mapped and sorted`() {
        every { repository.findById(eq(1)) } returns Optional.of(workPackageWithActivities)
        assertThat(persistence.getWorkPackageActivitiesForWorkPackage(1, 1L)).containsExactly(
            activity1_model, activity2_model,
        )
    }

    @Test
    fun updateWorkPackageActivities() {
        val workPackageSlot = slot<WorkPackageEntity>()
        every { repository.findById(WORK_PACKAGE_ID) } returns Optional.of(
            WorkPackageEntity(
                id = WORK_PACKAGE_ID,
                project = project,
                activities = emptyList()
            )
        )
        // we do not need to test mapping back to model as that is covered by getWorkPackageActivitiesForWorkPackage
        every { repository.save(capture(workPackageSlot)) } returnsArgument 0

        val toBeSaved = listOf(
            activity,
            WorkPackageActivity(
                workPackageId = 1L,
                startPeriod = 4,
                endPeriod = 6,
                deliverables = listOf(
                    WorkPackageActivityDeliverable(period = 4),
                    WorkPackageActivityDeliverable(period = 5),
                    WorkPackageActivityDeliverable(period = 6)
                )
            )
        )

        persistence.updateWorkPackageActivities(WORK_PACKAGE_ID, toBeSaved)

        assertThat(workPackageSlot.captured.activities).containsExactly(
            WorkPackageActivityEntity(
                activityId = activityId1,
                translatedValues = setOf(
                    WorkPackageActivityTranslationEntity(
                        translationId = trIdAct(activityId1, EN),
                        title = null,
                        description = "en_desc"
                    ),
                    WorkPackageActivityTranslationEntity(
                        translationId = trIdAct(activityId1, CS),
                        title = "",
                        description = null
                    ),
                    WorkPackageActivityTranslationEntity(
                        translationId = trIdAct(activityId1, SK),
                        title = "sk_title",
                        description = "sk_desc"
                    )
                ),
                startPeriod = 1,
                endPeriod = 3,
                deliverables = setOf(
                    WorkPackageActivityDeliverableEntity(
                        deliverableId = deliverableId1_activityId1,
                        startPeriod = 1,
                        translatedValues = setOf(
                            WorkPackageActivityDeliverableTranslationEntity(
                                translationId = trIdActDel(
                                    deliverableId1_activityId1,
                                    EN
                                ), description = "en_deliv_desc"
                            ),
                            WorkPackageActivityDeliverableTranslationEntity(
                                translationId = trIdActDel(
                                    deliverableId1_activityId1,
                                    CS
                                ), description = null
                            )
                        )
                    )
                )
            ),
            WorkPackageActivityEntity(
                activityId = activityId2,
                startPeriod = 4,
                endPeriod = 6,
                deliverables = setOf(
                    WorkPackageActivityDeliverableEntity(deliverableId = deliverableId1_activityId2, startPeriod = 4),
                    WorkPackageActivityDeliverableEntity(deliverableId = deliverableId2_activityId2, startPeriod = 5),
                    WorkPackageActivityDeliverableEntity(deliverableId = deliverableId3_activityId2, startPeriod = 6)
                )
            )
        )
    }

    @Test
    fun updateWorkPackageOutputs() {
        val workPackageSlot = slot<WorkPackageEntity>()
        every { repository.findById(WORK_PACKAGE_ID) } returns Optional.of(
            WorkPackageEntity(
                id = WORK_PACKAGE_ID,
                project = project,
                outputs = emptyList(),
            )
        )
        every { outputIndicatorRepository.findById(INDICATOR_ID) } returns Optional.of(indicatorOutput)
        // we do not need to test mapping back to model as that is covered by getWorkPackageOutputsForWorkPackage
        every { repository.save(capture(workPackageSlot)) } returnsArgument 0

        val toBeSaved = listOf(
            output,
            WorkPackageOutput(
                workPackageId = 1L,
                periodNumber = 7,
                programmeOutputIndicatorId = null,
            )
        )

        persistence.updateWorkPackageOutputs(WORK_PACKAGE_ID, toBeSaved)

        assertThat(workPackageSlot.captured.outputs).containsExactly(
            WorkPackageOutputEntity(
                outputId = outputId1,
                translatedValues = setOf(
                    WorkPackageOutputTransl(
                        translationId = trIdOut(outputId1, EN),
                        title = null,
                        description = "en_desc"
                    ),
                    WorkPackageOutputTransl(translationId = trIdOut(outputId1, CS), title = "", description = null),
                    WorkPackageOutputTransl(
                        translationId = trIdOut(outputId1, SK),
                        title = "sk_title",
                        description = "sk_desc"
                    ),
                ),
                periodNumber = 3,
                programmeOutputIndicatorEntity = indicatorOutput,
                targetValue = BigDecimal.TEN
            ),
            WorkPackageOutputEntity(
                outputId = outputId2,
                periodNumber = 7,
                programmeOutputIndicatorEntity = null,
            ),
        )
    }

    @Test
    fun `getWorkPackageOutputs - not-existing work package`() {
        every { repository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getWorkPackageOutputsForWorkPackage(-1, 1L) }
        assertThat(ex.entity).isEqualTo("workPackage")
    }

    @Test
    fun `getWorkPackageOutputs are correctly mapped and sorted`() {
        every { repository.findById(eq(1)) } returns Optional.of(workPackageWithOutputs)
        assertThat(persistence.getWorkPackageOutputsForWorkPackage(1, 1L)).containsExactly(
            output1_model, output2_model
        )
    }

    @Test
    fun countWorkPackageInvestments() {
        every { investmentRepository.countAllByWorkPackageId(1L) } returns 62
        assertThat(persistence.countWorkPackageInvestments(1L)).isEqualTo(62)
    }

    @Test
    fun `work package investment is added`() {
        every { repository.findById(1) } returns Optional.of(workPackageWithActivities)
        val predictedWorkPackageInvestment =
            workPackageInvestment.toWorkPackageInvestmentEntity(workPackageWithActivities).copy(
                id = 2
            )
        every { investmentRepository.save(any()) } returns predictedWorkPackageInvestment
        val sortedInvestments = listOf(predictedWorkPackageInvestment)
        every { investmentRepository.findAllByWorkPackageId(1, Sort.by("id")) } returns PageImpl(sortedInvestments)
        every { investmentRepository.saveAll(sortedInvestments) } returns sortedInvestments

        val result = persistence.addWorkPackageInvestment(1, workPackageInvestment)

        assertThat(result).isEqualTo(2)
        verify { investmentRepository.findAllByWorkPackageId(1, Sort.by("id")) }
        verify { investmentRepository.saveAll(sortedInvestments) }
    }

    @Test
    fun `should throw InvestmentNotFoundInProjectException when investment does not exist in the project`() {
        every { investmentRepository.existsByWorkPackageProjectIdAndId(PROJECT_ID, INVESTMENT_ID) } returns false
        assertThrows<InvestmentNotFoundInProjectException> {
            (persistence.throwIfInvestmentNotExistsInProject(PROJECT_ID, INVESTMENT_ID))
        }
    }


    @Test
    fun `should return Unit when investment exists in the project`() {
        every { investmentRepository.existsByWorkPackageProjectIdAndId(PROJECT_ID, INVESTMENT_ID) } returns true
        assertThat(persistence.throwIfInvestmentNotExistsInProject(PROJECT_ID, INVESTMENT_ID)).isEqualTo(Unit)
    }


}
