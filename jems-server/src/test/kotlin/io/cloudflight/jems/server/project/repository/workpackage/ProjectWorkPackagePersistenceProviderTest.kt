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
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageDetailRow
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageRow
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerRow
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityRow
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageDeliverableRow
import io.cloudflight.jems.server.project.entity.workpackage.output.OutputRowWithTranslations
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputRow
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityPartnerRepository
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.workpackage.investment.WorkPackageInvestmentRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.WorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.user.repository.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil.Companion.project
import io.cloudflight.jems.server.utils.partner.activityEntity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Optional

class ProjectWorkPackagePersistenceProviderTest : UnitTest() {

    companion object {
        private const val WORK_PACKAGE_ID = 1L
        private const val WORK_PACKAGE_ID_2 = 654L
        private const val INVESTMENT_ID = 54L
        private const val PROJECT_ID = 64L

        private const val activityId1 = 3L
        private const val activityId2 = 2L

        private val outputId1 = WorkPackageOutputId(workPackageId = WORK_PACKAGE_ID, outputNumber = 1)
        private val outputId2 = WorkPackageOutputId(workPackageId = WORK_PACKAGE_ID, outputNumber = 2)

        private fun trIdAct(activityEntity: WorkPackageActivityEntity, lang: SystemLanguage) =
            TranslationId(
                sourceEntity = activityEntity,
                language = lang
            )

        private fun trIdActDel(deliverableEntity: WorkPackageActivityDeliverableEntity, lang: SystemLanguage) =
            TranslationId(
                sourceEntity = deliverableEntity,
                language = lang
            )

        private val deliverable2_2 = WorkPackageActivityDeliverableEntity(
            id = 1L,
            deliverableNumber = 2,
            startPeriod = 2
        )
        private val deliverable2_1 = WorkPackageActivityDeliverableEntity(
            id = 2L,
            deliverableNumber = 1,
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

        const val activityProjectPartnerId = 3L
        val activityPartnerMock: WorkPackageActivityPartnerEntity = mockk()
        var activity1 = WorkPackageActivityEntity(
            id = activityId1,
            workPackage = WorkPackageEntity(id = WORK_PACKAGE_ID, number = 10, project = project),
            activityNumber = 1,
            startPeriod = 4,
            endPeriod = 6,
            partners = mutableSetOf(activityPartnerMock)
        )
        val activity1Partner1 = WorkPackageActivityPartnerEntity(
            WorkPackageActivityPartnerId(
                activity = activity1,
                projectPartnerId = activityProjectPartnerId
            )
        )
        val activity1_model = WorkPackageActivity(
            id = activityId1,
            workPackageId = 1L,
            workPackageNumber = 10,
            activityNumber = 1,
            startPeriod = 4,
            endPeriod = 6,
            partnerIds = setOf(activity1Partner1.id.projectPartnerId)
        )
        val activity2 = WorkPackageActivityEntity(
            id = activityId2,
            workPackage = WorkPackageEntity(id = WORK_PACKAGE_ID, number = 10, project = project),
            activityNumber = 2,
            startPeriod = 1,
            endPeriod = 3,
            translatedValues = mutableSetOf(),
            deliverables = mutableSetOf(deliverable2_2, deliverable2_1)
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
            id = activityId2,
            workPackageId = 1L,
            workPackageNumber = 10,
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
                    id = deliverable2_1.id,
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
                    id = deliverable2_2.id,
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
            number = 10,
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

    @MockK
    lateinit var investmentRepository: WorkPackageInvestmentRepository

    @MockK
    lateinit var outputIndicatorRepository: OutputIndicatorRepository

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var collaboratorRepository: UserProjectCollaboratorRepository

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
            projectVersionUtils,
            projectRepository,
            collaboratorRepository,
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
        val activityIds = setOf(activityId1, activityId2)
        every { activityPartnerMock.id } returns WorkPackageActivityPartnerId(activity1, activityProjectPartnerId)
        every {
            repositoryActivity.findAllByWorkPackageIdIn(wkPackages)
        } returns listOf(activity1, activity2)
        every {
            repositoryOutput.findAllByOutputIdWorkPackageIdIn(wkPackages)
        } returns listOf(output2, output1)
        every { repositoryActivityPartner.findAllByIdActivityIdIn(activityIds) } returns mutableListOf(
            activity1Partner1
        )

        val result = persistence.getWorkPackagesWithOutputsAndActivitiesByProjectId(1L, null)
        assertThat(result.size).isEqualTo(2)
        assertThat(result.map { it.id }).containsExactly(WORK_PACKAGE_ID, WORK_PACKAGE_ID_2)
        assertThat(result.map { it.workPackageNumber }).containsExactly(workPackageWithActivities.number, 2)
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
        every { mockWPARow.id } returns activityId1
        every { mockWPARow.workPackageId } returns wpId
        every { mockWPARow.activityNumber } returns activityNumber
        every { mockWPARow.language } returns EN
        every { mockWPARow.startPeriod } returns 1
        every { mockWPARow.endPeriod } returns 2
        every { mockWPARow.title } returns "title"
        every { mockWPARow.description } returns "description"
        val mockWPAPRow: WorkPackageActivityPartnerRow = mockk()
        every { mockWPAPRow.activityId } returns activityId1
        every { mockWPAPRow.workPackageId } returns wpId
        every { mockWPAPRow.projectPartnerId } returns 5
        val mockWPDRow: WorkPackageDeliverableRow = mockk()
        every { mockWPDRow.id } returns deliverable2_1.id
        every { mockWPDRow.deliverableNumber } returns 4
        every { mockWPDRow.language } returns EN
        every { mockWPDRow.startPeriod } returns 1
        every { mockWPDRow.description } returns "description"
        every { mockWPDRow.title } returns "title"
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
            repositoryActivity.findAllDeliverablesByActivityIdAsOfTimestamp(
                activityId1,
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
                        id = activityId1,
                        activityNumber = mockWPARow.activityNumber,
                        workPackageId = mockWPARow.workPackageId,
                        title = setOf(InputTranslation(mockWPARow.language!!, mockWPARow.title)),
                        description = setOf(InputTranslation(mockWPARow.language!!, mockWPARow.description)),
                        startPeriod = mockWPARow.startPeriod,
                        endPeriod = mockWPARow.endPeriod,
                        deliverables = listOf(
                            WorkPackageActivityDeliverable(
                                id = deliverable2_1.id,
                                deliverableNumber = mockWPDRow.deliverableNumber,
                                description = setOf(
                                    InputTranslation(EN, mockWPDRow.description)
                                ),
                                title = setOf(
                                    InputTranslation(EN, mockWPDRow.title)
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
    fun `get work package activities for project`() {
        every { repository.findAllByProjectId(eq(1L), any()) } returns listOf(workPackageWithActivities)
        every { repositoryActivity.findAllByWorkPackageIdIn(setOf(workPackageWithActivities.id)) } returns listOf(
            activity1, activity2)

        val wpActivityList = persistence.getWorkPackageActivitiesForProject(1L)
        assertThat(wpActivityList).contains(
            WorkPackageActivitySummary(
                activityId = activityId1,
                workPackageNumber = workPackageWithActivities.number!!,
                activityNumber = activity1.activityNumber
            ),
            WorkPackageActivitySummary(
                activityId = activityId2,
                workPackageNumber = workPackageWithActivities.number!!,
                activityNumber = activity2.activityNumber
            ))
    }

    @Test
    fun `work package activities and deliverables are correctly mapped and sorted`() {
        every { activityPartnerMock.id } returns WorkPackageActivityPartnerId(activity1, activityProjectPartnerId)
        every { repository.findById(eq(WORK_PACKAGE_ID)) } returns Optional.of(workPackageWithActivities)
        val partnerList = mutableListOf(activity1Partner1)
        every { repositoryActivityPartner.findAllByIdActivityIdIn(listOf(WORK_PACKAGE_ID)) } returns partnerList

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
            WorkPackageActivityRowImpl(activityId1, null, WORK_PACKAGE_ID, 10,1, 1, 2, null, null)
        )
        every {
            repositoryActivity.findAllDeliverablesByActivityIdAsOfTimestamp(
                activityId1,
                timestamp
            )
        } returns emptyList()
        every {
            repositoryActivityPartner.findAllByActivityIdAsOfTimestamp(
                activityId1,
                timestamp
            )
        } returns listOf(
            WorkPackageActivityPartnerRowImpl(
                activityId1,
                WORK_PACKAGE_ID,
                207L
            ),
            WorkPackageActivityPartnerRowImpl(
                activityId1,
                WORK_PACKAGE_ID,
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
                id = activityId1,
                workPackageId = WORK_PACKAGE_ID,
                workPackageNumber = 10,
                activityNumber = 1,
                startPeriod = 1,
                endPeriod = 2,
                deliverables = emptyList(),
                partnerIds = setOf(207L, 208L),
            )
        )
    }

    @Test
    fun `update WorkPackage Activities`() {
        val wpNr = 10
        val wp = WorkPackageEntity(
            id = WORK_PACKAGE_ID,
            project = project,
            number = wpNr,
            activities = mutableListOf(
                WorkPackageActivityEntity(
                    id = activityId2,
                    workPackage = WorkPackageEntity(id = WORK_PACKAGE_ID, number = 10, project = project),
                    activityNumber = 2,
                    startPeriod = 1,
                    endPeriod = 3,
                    translatedValues = mutableSetOf(),
                    deliverables = mutableSetOf(deliverable2_2, deliverable2_1)
                ),
                WorkPackageActivityEntity(
                    id = activityId1,
                    workPackage = WorkPackageEntity(id = WORK_PACKAGE_ID, number = 10, project = project),
                    activityNumber = 1,
                    startPeriod = 4,
                    endPeriod = 6,
                    partners = mutableSetOf(activityPartnerMock)
                )
            ),
            translatedValues = mutableSetOf()
        ).apply {
            translatedValues.addAll(
                setOf(WorkPackageTransl(translationId = TranslationId(this, EN), name = "name"))
            )
        }
        every { repository.findById(eq(WORK_PACKAGE_ID)) } returns Optional.of(wp)
        val toBeSaved = listOf(
            WorkPackageActivity(
                id = activityId2,
                workPackageId = wp.id,
                workPackageNumber = wpNr,
                activityNumber = 99,
                startPeriod = 4,
                endPeriod = 6,
                deliverables = listOf(
                    WorkPackageActivityDeliverable(period = 4),
                    WorkPackageActivityDeliverable(period = 6)
                )
            ),
            WorkPackageActivity(
                id = activityId1,
                workPackageId = wp.id,
                workPackageNumber = wpNr,
                startPeriod = 2,
                endPeriod = 3,
                deliverables = emptyList()
            )
        )

        val activities = persistence.updateWorkPackageActivities(WORK_PACKAGE_ID, toBeSaved)

        assertThat(activities).containsExactly(
            WorkPackageActivity(
                id = activityId2,
                workPackageId = wp.id,
                workPackageNumber = wpNr,
                activityNumber = 1,
                startPeriod = 4,
                endPeriod = 6,
                deliverables = listOf(
                    WorkPackageActivityDeliverable(period = 4, deliverableNumber = 1),
                    WorkPackageActivityDeliverable(period = 6, deliverableNumber = 2)
                )
            ),
            WorkPackageActivity(
                id = activityId1,
                workPackageId = wp.id,
                workPackageNumber = wpNr,
                activityNumber = 2,
                startPeriod = 2,
                endPeriod = 3,
                deliverables = emptyList()
            )
        )
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

    @Test
    fun getWorkPackagesWithAllDataByProjectId() {
        every { projectRepository.findById(PROJECT_ID).get().periods } returns emptyList()
        every { repository.findAllByProjectId(PROJECT_ID, Sort.by(Sort.Direction.ASC, "id")) } returns listOf(workPackageWithActivities)
        val workPackageIds = setOf(WORK_PACKAGE_ID)
        every { repositoryActivity.findAllByWorkPackageIdIn(workPackageIds) } returns listOf(activityEntity)
        every { repositoryOutput.findAllByOutputIdWorkPackageIdIn(workPackageIds) } returns emptyList()
        every { investmentRepository.findInvestmentsByProjectId(PROJECT_ID) } returns emptyList()

        val workPackages = persistence.getWorkPackagesWithAllDataByProjectId(PROJECT_ID)
        assertThat(workPackages).containsExactly(
            ProjectWorkPackageFull(
                id = WORK_PACKAGE_ID,
                workPackageNumber = workPackageWithActivities.number!!,
                name = setOf(InputTranslation(CS, "WP CS name")),
                specificObjective = emptySet(),
                objectiveAndAudience = emptySet(),
                activities = listOf(WorkPackageActivity(
                    id = activityEntity.id,
                    workPackageId = WORK_PACKAGE_ID,
                    workPackageNumber = workPackageWithActivities.number!!,
                    activityNumber = activityEntity.activityNumber,
                    title = emptySet(),
                    description = emptySet(),
                    startPeriod = 1,
                    endPeriod = 3,
                    deliverables = emptyList()
                )),
                outputs = emptyList(),
                investments = emptyList()
            )
        )
    }

    @Test
    fun `should return work packages with all the details for the specified version of the project when there is no problem`() {
        val timestamp = Timestamp.valueOf(LocalDateTime.of(2020, 8, 15, 6, 0))
        val version = "1.0"
        every { projectRepository.findPeriodsByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns emptyList()
        every { repository.findWorkPackagesByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns getWorkPackageDetailRows()
        every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, version) } returns timestamp

        val workPackages = persistence.getWorkPackagesWithAllDataByProjectId(PROJECT_ID, version)
        assertThat(workPackages).containsExactly(
            ProjectWorkPackageFull(
                id = WORK_PACKAGE_ID,
                workPackageNumber = workPackageWithActivities.number!!,
                name = setOf(InputTranslation(CS, "WP CS name")),
                specificObjective = emptySet(),
                objectiveAndAudience = emptySet(),
                activities = listOf(WorkPackageActivity(
                    id = activityEntity.id,
                    workPackageId = WORK_PACKAGE_ID,
                    workPackageNumber = workPackageWithActivities.number!!,
                    activityNumber = activityEntity.activityNumber,
                    title = emptySet(),
                    description = emptySet(),
                    startPeriod = 1,
                    endPeriod = 3,
                    deliverables = emptyList()
                )),
                outputs = emptyList(),
                investments = emptyList()
            )
        )
    }

    @ParameterizedTest(name = "getAllOutputsForProjectIdSortedByNumbers - when version is {0}")
    @ValueSource(strings = ["7.1", ""])
    fun getAllOutputsForProjectIdSortedByNumbers(versionString: String) {
        val version: String? = versionString.ifEmpty { null }

        val output_row_1_1 = OutputRowImpl(
            workPackageId = 1L,
            workPackageNumber = 1,
            number = 1,
            title = "Out 1.1",
            language = EN,
            targetValue = BigDecimal.TEN,
            programmeOutputId = 514L,
            programmeResultId = 500L,
        )
        val output_row_1_2 = OutputRowImpl(
            workPackageId = 1L,
            workPackageNumber = 1,
            number = 2,
            title = "Out 1.2",
            language = EN,
            targetValue = BigDecimal.ZERO,
            programmeOutputId = 522L,
            programmeResultId = null,
        )
        val output_row_4_1 = OutputRowImpl(
            workPackageId = 4L,
            workPackageNumber = 4,
            number = 1,
            title = "Out 4.1",
            language = EN,
            targetValue = BigDecimal.ONE,
            programmeOutputId = null,
            programmeResultId = null,
        )

        val timestamp = Timestamp.valueOf(LocalDateTime.now())

        val data = listOf(output_row_1_1, output_row_1_2, output_row_4_1)
        if (version != null) {
            every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, version) } returns timestamp
            every { repositoryOutput.findAllByProjectIdAsOfTimestampOrderedByNumbers(PROJECT_ID, timestamp) } returns data
        } else {
            every { repositoryOutput.findAllByProjectIdOrderedByNumbers(PROJECT_ID) } returns data
        }

        val resultingOutputs = persistence.getAllOutputsForProjectIdSortedByNumbers(PROJECT_ID, version)
        assertThat(resultingOutputs).hasSize(3)

        with(resultingOutputs.get(0)) {
            assertThat(workPackageId).isEqualTo(1L)
            assertThat(workPackageNumber).isEqualTo(1)
            assertThat(outputTitle).containsExactlyInAnyOrder(InputTranslation(EN, "Out 1.1"))
            assertThat(outputNumber).isEqualTo(1)
            assertThat(outputTargetValue).isEqualByComparingTo(BigDecimal.TEN)
            assertThat(programmeOutputId).isEqualTo(514L)
            assertThat(programmeResultId).isEqualTo(500L)
        }
        with(resultingOutputs.get(1)) {
            assertThat(workPackageId).isEqualTo(1L)
            assertThat(workPackageNumber).isEqualTo(1)
            assertThat(outputTitle).containsExactlyInAnyOrder(InputTranslation(EN, "Out 1.2"))
            assertThat(outputNumber).isEqualTo(2)
            assertThat(outputTargetValue).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(programmeOutputId).isEqualTo(522L)
            assertThat(programmeResultId).isNull()
        }
        with(resultingOutputs.get(2)) {
            assertThat(workPackageId).isEqualTo(4L)
            assertThat(workPackageNumber).isEqualTo(4)
            assertThat(outputTitle).containsExactlyInAnyOrder(InputTranslation(EN, "Out 4.1"))
            assertThat(outputNumber).isEqualTo(1)
            assertThat(outputTargetValue).isEqualByComparingTo(BigDecimal.ONE)
            assertThat(programmeOutputId).isNull()
            assertThat(programmeResultId).isNull()
        }
    }

    private fun getWorkPackageDetailRows() : List<WorkPackageDetailRow> {
        return listOf(
            object : WorkPackageDetailRow{
                override val id = WORK_PACKAGE_ID
                override val number = workPackageWithActivities.number!!
                override val name = "WP CS name"
                override val specificObjective: String? = null
                override val objectiveAndAudience: String? = null
                override val language = CS
                override val activityId = activityEntity.id
                override val activityNumber =activityEntity.activityNumber
                override val startPeriod = 1
                override val endPeriod = 3
                override var partnerId: Long? = null
                override val activityTitle: String? = null
                override val activityLanguage: SystemLanguage? = null
                override val activityDescription: String? = null
                override val deliverableId: Long? = null
                override val deliverableNumber: Int? = null
                override val deliverableStartPeriod: Int? = null
                override val deliverableDescription: String? = null
                override val deliverableTitle: String? = null
                override val deliverableLanguage: SystemLanguage? = null
                override val outputNumber: Int? = null
                override val programmeOutputIndicatorId: Long? = null
                override val programmeOutputIndicatorIdentifier: String? = null
                override val targetValue: BigDecimal? = null
                override val outputPeriodNumber: Int? = null
                override val outputTitle: String? = null
                override val outputDescription: String? = null
                override val outputLanguage: SystemLanguage? = null
                override val investmentId: Long? = null
                override val investmentNumber: Int? = null
                override val investmentCountry: String? = null
                override val investmentNutsRegion2: String? = null
                override val investmentNutsRegion3: String? = null
                override val investmentStreet: String? = null
                override val investmentHouseNumber: String? = null
                override val investmentPostalCode: String? = null
                override val investmentCity: String? = null
                override val investmentTitle: String? = null
                override val justificationExplanation: String? = null
                override val justificationTransactionalRelevance: String? = null
                override val justificationBenefits: String? = null
                override val justificationPilot: String? = null
                override val investmentRisk: String? = null
                override val investmentDocumentation: String? = null
                override val ownershipSiteLocation: String? = null
                override val ownershipRetain: String? = null
                override val ownershipMaintenance: String? = null
                override val investmentLanguage: SystemLanguage? = null
                override val programmeOutputIndicatorLanguage: SystemLanguage? = null
                override val programmeOutputIndicatorMeasurementUnit: String? = null
                override val programmeOutputIndicatorName: String? = null
            }
        )
    }

    data class WorkPackageActivityRowImpl(
        override val id: Long,
        override val language: SystemLanguage?,
        override val workPackageId: Long,
        override val workPackageNumber: Int?,
        override val activityNumber: Int,
        override val startPeriod: Int?,
        override val endPeriod: Int?,
        override val title: String?,
        override val description: String?
    ) : WorkPackageActivityRow

    data class WorkPackageActivityPartnerRowImpl(
        override val activityId: Long,
        override val workPackageId: Long,
        override val projectPartnerId: Long
    ) : WorkPackageActivityPartnerRow

    data class OutputRowImpl(
        override val workPackageId: Long,
        override val workPackageNumber: Int,
        override val number: Int,
        override val title: String? = null,
        override val language: SystemLanguage?,
        override val targetValue: BigDecimal,
        override val programmeOutputId: Long?,
        override val programmeResultId: Long?
    ) : OutputRowWithTranslations

}
