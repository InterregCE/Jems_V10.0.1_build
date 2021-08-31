package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.repository.indicator.OutputIndicatorRepository
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageRow
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerRow
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
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityPartnerRepository
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.workpackage.investment.WorkPackageInvestmentRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.WorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil.Companion.project
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Optional

class ProjectWorkPackagePersistenceTest : UnitTest() {

    companion object {
        private const val WORK_PACKAGE_ID = 1L
        private const val WORK_PACKAGE_ID_2 = 654L
        private const val INVESTMENT_ID = 54L
        private const val PROJECT_ID = 64L

        private val activityId1 = WorkPackageActivityId(workPackageId = WORK_PACKAGE_ID, activityNumber = 1)
        private val activityId2 = WorkPackageActivityId(workPackageId = WORK_PACKAGE_ID, activityNumber = 2)
        private val outputId1 = WorkPackageOutputId(workPackageId = WORK_PACKAGE_ID, outputNumber = 1)
        private val outputId2 = WorkPackageOutputId(workPackageId = WORK_PACKAGE_ID, outputNumber = 2)

        private fun trIdAct(activityEntity: WorkPackageActivityEntity, lang: SystemLanguage) =
            WorkPackageActivityTranslationId(
                sourceEntity = activityEntity,
                language = lang
            )

        private fun trIdActDel(deliverableEntity: WorkPackageActivityDeliverableEntity, lang: SystemLanguage) =
            WorkPackageActivityDeliverableTranslationId(
                sourceEntity = deliverableEntity,
                language = lang
            )

        private val deliverableId1_activityId1 =
            WorkPackageActivityDeliverableId(activityId = activityId1, deliverableNumber = 1)
        private val deliverableId2_activityId1 =
            WorkPackageActivityDeliverableId(activityId = activityId1, deliverableNumber = 2)

        private val deliverable2_2 = WorkPackageActivityDeliverableEntity(
            deliverableId = deliverableId2_activityId1,
            startPeriod = 2
        )
        private val deliverable2_1 = WorkPackageActivityDeliverableEntity(
            deliverableId = deliverableId1_activityId1,
            startPeriod = 1,
            translatedValues = mutableSetOf()
        ).apply {
            translatedValues.addAll(
                setOf(
                    WorkPackageActivityDeliverableTranslationEntity(
                        translationId = trIdActDel(this, SK),
                        description = "sk_deliverable_desc"
                    ),
                    WorkPackageActivityDeliverableTranslationEntity(
                        translationId = trIdActDel(this, CS),
                        description = ""
                    ),
                    WorkPackageActivityDeliverableTranslationEntity(
                        translationId = trIdActDel(this, EN),
                        description = null
                    )
                )
            )
        }

        val activity1 = WorkPackageActivityEntity(
            activityId = activityId1,
            startPeriod = 4,
            endPeriod = 6
        )
        val activity1Partner1 = WorkPackageActivityPartnerEntity(
            WorkPackageActivityPartnerId(
                workPackageActivityId = activityId1,
                projectPartnerId = 3
            )
        )
        val activity1_model = WorkPackageActivity(
            workPackageId = 1L,
            activityNumber = 1,
            startPeriod = 4,
            endPeriod = 6,
            partnerIds = setOf(activity1Partner1.id.projectPartnerId)
        )
        val activity2 = WorkPackageActivityEntity(
            activityId = activityId2,
            startPeriod = 1,
            endPeriod = 3,
            translatedValues = mutableSetOf(),
            deliverables = setOf(deliverable2_2, deliverable2_1)
        ).apply {
            translatedValues.addAll(
                setOf(
                    WorkPackageActivityTranslationEntity(
                        translationId = trIdAct(this, SK),
                        title = "sk_title",
                        description = ""
                    ),
                    WorkPackageActivityTranslationEntity(
                        translationId = trIdAct(this, CS),
                        title = null,
                        description = "cs_desc"
                    ),
                    WorkPackageActivityTranslationEntity(
                        translationId = trIdAct(this, EN),
                        title = " ",
                        description = " "
                    )
                )
            )
        }
        val activity2_model = WorkPackageActivity(
            workPackageId = 1L,
            activityNumber = 2,
            title = setOf(
                InputTranslation(language = SK, translation = "sk_title"),
            ),
            description = setOf(
                InputTranslation(language = CS, translation = "cs_desc"),
            ),
            startPeriod = 1,
            endPeriod = 3,
            deliverables = listOf(
                WorkPackageActivityDeliverable(
                    deliverableNumber = 1,
                    period = 1,
                    description = setOf(
                        InputTranslation(
                            language = SK,
                            translation = "sk_deliverable_desc"
                        ),
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
            activities = mutableListOf(activity2, activity1), // for testing sorting
            translatedValues = mutableSetOf()
        ).apply {
            translatedValues.addAll(
                setOf(
                    WorkPackageTransl(
                        translationId = TranslationId(this, CS),
                        name = "WP CS name"
                    )
                )
            )
        }

        val activity = WorkPackageActivity(
            workPackageId = 1L,
            title = setOf(
                InputTranslation(language = SK, translation = "sk_title")
            ),
            description = setOf(
                InputTranslation(language = EN, translation = "en_desc"),
                InputTranslation(language = SK, translation = "sk_desc")
            ),
            startPeriod = 1,
            endPeriod = 3,
            deliverables = listOf(
                WorkPackageActivityDeliverable(
                    period = 1,
                    description = setOf(InputTranslation(language = EN, translation = "en_deliv_desc"))
                )
            )
        )

        val output = WorkPackageOutput(
            workPackageId = 1L,
            outputNumber = 1,
            title = setOf(

                InputTranslation(language = SK, translation = "sk_title"),
            ),
            description = setOf(
                InputTranslation(language = EN, translation = "en_desc"),
                InputTranslation(language = SK, translation = "sk_desc"),
            ),
            periodNumber = 3,
            programmeOutputIndicatorId = INDICATOR_ID,
            targetValue = BigDecimal.TEN
        )

        val workPackageWithOutputs = WorkPackageEntity(
            id = WORK_PACKAGE_ID,
            project = project,
            outputs = mutableListOf(output2, output1),
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
    lateinit var repositoryActivityPartner: WorkPackageActivityPartnerRepository

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
            repositoryActivityPartner,
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
                workPackageWithActivities.also {
                    it.activities.clear()
                    it.outputs.clear()
                },
                emptyWP
            )
        val wkPackages = setOf(WORK_PACKAGE_ID, WORK_PACKAGE_ID_2)
        every {
            repositoryActivity.findAllByActivityIdWorkPackageIdIn(wkPackages)
        } returns listOf(activity2, activity1)
        every {
            repositoryOutput.findAllByOutputIdWorkPackageIdIn(wkPackages)
        } returns listOf(output2, output1)
        every { repositoryActivityPartner.findAllByIdWorkPackageActivityIdWorkPackageIdIn(wkPackages) } returns listOf(
            activity1Partner1
        )

        val result = persistence.getWorkPackagesWithOutputsAndActivitiesByProjectId(1L, null)
        assertThat(result.size).isEqualTo(2)
        assertThat(result.map { it.id }).containsExactly(WORK_PACKAGE_ID, WORK_PACKAGE_ID_2)
        assertThat(result.map { it.workPackageNumber }).containsExactly(1, 2)
        assertThat(result[0].name).containsExactly(InputTranslation(CS, "WP CS name"))
        assertThat(result[0].activities).containsExactly(activity1_model, activity2_model)
        assertThat(result[0].outputs).containsExactly(output1_model, output2_model)
    }

    @Test
    fun `get full-rich work packages for previous version`() {
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val version = "3.0"
        val id = 1L
        val wpId = 2L
        val activityNumber = 3
        val mockWPRow: WorkPackageRow = mockk()
        every { mockWPRow.id } returns wpId
        every { mockWPRow.language } returns EN
        every { mockWPRow.name } returns "name"
        every { mockWPRow.number } returns 3
        every { mockWPRow.specificObjective } returns "specificObjective"
        every { mockWPRow.objectiveAndAudience } returns "objectiveAndAudience"
        val mockWPARow: WorkPackageActivityRow = mockk()
        every { mockWPARow.workPackageId } returns wpId
        every { mockWPARow.activityNumber } returns activityNumber
        every { mockWPARow.language } returns EN
        every { mockWPARow.startPeriod } returns 1
        every { mockWPARow.endPeriod } returns 2
        every { mockWPARow.title } returns "title"
        every { mockWPARow.description } returns "description"
        val mockWPAPRow: WorkPackageActivityPartnerRow = mockk()
        every { mockWPAPRow.workPackageId } returns wpId
        every { mockWPAPRow.activityNumber } returns activityNumber
        every { mockWPAPRow.projectPartnerId } returns 5
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
                activityNumber,
                timestamp
            )
        } returns listOf(mockWPDRow)
        every { repositoryOutput.findAllByOutputIdWorkPackageIdAsOfTimestamp(setOf(wpId), timestamp) } returns listOf(
            mockWPORow
        )
        every { repositoryActivityPartner.findAllByWorkPackageIdsAsOfTimestamp(setOf(wpId), timestamp) } returns listOf(
            mockWPAPRow
        )

        // test
        val result = persistence.getWorkPackagesWithOutputsAndActivitiesByProjectId(id, version)

        assertThat(result.size).isEqualTo(1)
        assertThat(result[0]).isEqualTo(
            ProjectWorkPackage(
                id = mockWPRow.id,
                workPackageNumber = mockWPRow.number!!,
                name = setOf(InputTranslation(mockWPRow.language!!, mockWPRow.name)),
                specificObjective = setOf(InputTranslation(mockWPRow.language!!, mockWPRow.specificObjective)),
                objectiveAndAudience = setOf(InputTranslation(mockWPRow.language!!, mockWPRow.objectiveAndAudience)),
                activities = listOf(
                    WorkPackageActivity(
                        activityNumber = mockWPARow.activityNumber,
                        workPackageId = mockWPARow.workPackageId,
                        title = setOf(InputTranslation(mockWPARow.language!!, mockWPARow.title)),
                        description = setOf(InputTranslation(mockWPARow.language!!, mockWPARow.description)),
                        startPeriod = mockWPARow.startPeriod,
                        endPeriod = mockWPARow.endPeriod,
                        deliverables = listOf(
                            WorkPackageActivityDeliverable(
                                deliverableNumber = mockWPDRow.deliverableNumber,
                                description = setOf(
                                    InputTranslation(EN, mockWPDRow.description)
                                ),
                                period = mockWPDRow.startPeriod
                            )
                        ),
                        partnerIds = setOf(5)
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
                        title = setOf(InputTranslation(mockWPORow.language!!, mockWPORow.title)),
                        description = setOf(InputTranslation(mockWPORow.language!!, mockWPORow.description))
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
        every { repository.findById(eq(WORK_PACKAGE_ID)) } returns Optional.of(workPackageWithActivities)
        val partnerList = mutableListOf(activity1Partner1)
        every { repositoryActivityPartner.findAllByIdWorkPackageActivityIdWorkPackageId(WORK_PACKAGE_ID) } returns partnerList

        assertThat(persistence.getWorkPackageActivitiesForWorkPackage(WORK_PACKAGE_ID, 1L)).containsExactly(
            activity1_model, activity2_model,
        )
    }

    @Test
    fun `work package historical activities are correctly mapped without translations`() {
        val timestamp: Timestamp = Timestamp.valueOf(LocalDateTime.now())
        every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, "A") } returns timestamp
        every {
            repositoryActivity.findAllActivitiesByWorkPackageIdAsOfTimestamp(
                WORK_PACKAGE_ID,
                timestamp
            )
        } returns listOf(
            WorkPackageActivityRowImpl(null, WORK_PACKAGE_ID, 1, 1, 2, null, null)
        )
        every {
            repositoryActivity.findAllDeliverablesByWorkPackageIdAndActivityIdAsOfTimestamp(
                WORK_PACKAGE_ID,
                1,
                timestamp
            )
        } returns emptyList()
        every {
            repositoryActivityPartner.findAllByWorkPackageIdAndActivityNumberAsOfTimestamp(
                WORK_PACKAGE_ID,
                1,
                timestamp
            )
        } returns listOf(
            WorkPackageActivityPartnerRowImpl(
                WORK_PACKAGE_ID,
                1,
                207L
            ),
            WorkPackageActivityPartnerRowImpl(
                WORK_PACKAGE_ID,
                1,
                208L
            ),
        )

        assertThat(
            persistence.getWorkPackageActivitiesForWorkPackage(
                WORK_PACKAGE_ID,
                PROJECT_ID,
                "A"
            )
        ).containsExactly(
            WorkPackageActivity(
                workPackageId = WORK_PACKAGE_ID,
                activityNumber = 1,
                startPeriod = 1,
                endPeriod = 2,
                deliverables = emptyList(),
                partnerIds = setOf(207L, 208L),
            )
        )
    }

    @Test
    fun updateWorkPackageActivities() {
        val slot = slot<List<WorkPackageActivityEntity>>()
        every { repository.existsById(WORK_PACKAGE_ID) } returns true
        every { repositoryActivity.findAllByActivityIdWorkPackageId(WORK_PACKAGE_ID) } returns mutableListOf()
        every { repositoryActivity.deleteAll(any()) } returns Unit
        every { repositoryActivity.saveAll(capture(slot)) } returnsArgument 0
        every { repositoryActivity.saveAll(capture(slot)) } returnsArgument 0

        every { repositoryActivityPartner.deleteAllByIdWorkPackageActivityIdWorkPackageId(WORK_PACKAGE_ID) } returns Unit
        every { repositoryActivityPartner.saveAll(emptyList()) } returns emptyList()

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

        val activities = persistence.updateWorkPackageActivities(WORK_PACKAGE_ID, toBeSaved)

        assertThat(activities).containsExactly(*toBeSaved.mapIndexed { index, it ->
            it.copy(activityNumber = index.plus(1),
                deliverables = it.deliverables.mapIndexed { i, deliverable ->
                    deliverable.copy(deliverableNumber = i.plus(1))
                }
            )
        }.toTypedArray())
    }

    @Test
    fun updateWorkPackageOutputs() {
        val slot = slot<List<WorkPackageOutputEntity>>()
        every { repository.existsById(WORK_PACKAGE_ID) } returns true
        every { repositoryOutput.findAllByOutputIdWorkPackageId(WORK_PACKAGE_ID) } returns mutableListOf()
        every { repositoryOutput.deleteAll(any()) } returns Unit
        every { repositoryOutput.saveAll(capture(slot)) } returnsArgument 0

        every { outputIndicatorRepository.findById(INDICATOR_ID) } returns Optional.of(indicatorOutput)

        val toBeSaved = listOf(
            output,
            WorkPackageOutput(
                workPackageId = 1L,
                periodNumber = 7,
                programmeOutputIndicatorId = null,
            )
        )

        val updatedOutputs = persistence.updateWorkPackageOutputs(WORK_PACKAGE_ID, toBeSaved)

        assertThat(updatedOutputs).containsExactly(
            *toBeSaved.mapIndexed { index, output ->
                output.copy(
                    outputNumber = index.plus(1),
                    programmeOutputIndicatorIdentifier = if (index == 0) indicatorOutput.identifier else null
                )
            }.toTypedArray()
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

    data class WorkPackageActivityRowImpl(
        override val language: SystemLanguage?,
        override val workPackageId: Long,
        override val activityNumber: Int,
        override val startPeriod: Int?,
        override val endPeriod: Int?,
        override val title: String?,
        override val description: String?
    ) : WorkPackageActivityRow

    data class WorkPackageActivityPartnerRowImpl(
        override val workPackageId: Long,
        override val activityNumber: Int,
        override val projectPartnerId: Long
    ) : WorkPackageActivityPartnerRow

}
