package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.repository.indicator.OutputIndicatorRepository
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partneruser.UserPartnerCollaboratorRepository
import io.cloudflight.jems.server.project.repository.projectuser.UserProjectCollaboratorRepository
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityPartnerRepository
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.workpackage.investment.WorkPackageInvestmentRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.WorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil.Companion.project
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Sort
import java.math.BigDecimal
import java.util.Optional

class ProjectWorkPackagePersistenceProviderTest : UnitTest() {

    companion object {
        private const val WORK_PACKAGE_ID = 1L
        private const val INVESTMENT_ID = 54L
        private const val PROJECT_ID = 64L

        private const val activityId1 = 3L
        private const val activityId2 = 2L

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
        val activity2 = WorkPackageActivityEntity(
            id = activityId2,
            workPackage = WorkPackageEntity(id = WORK_PACKAGE_ID, number = 10, project = project),
            activityNumber = 2,
            startPeriod = 1,
            endPeriod = 3,
            translatedValues = mutableSetOf(),
            deliverables = mutableSetOf()
        ).apply {
            deliverables.addAll(
                mutableSetOf(
                    WorkPackageActivityDeliverableEntity(
                        id = 1L,
                        deliverableNumber = 2,
                        startPeriod = 2,
                        workPackageActivity = this
                    ),
                    WorkPackageActivityDeliverableEntity(
                        id = 2L,
                        deliverableNumber = 1,
                        startPeriod = 1,
                        translatedValues = mutableSetOf(),
                        workPackageActivity = this
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
                )
            )
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

        const val INDICATOR_ID = 30L
        val indicatorOutput = OutputIndicatorEntity(
            id = INDICATOR_ID,
            identifier = "ID.30",
            code = "tst",
            programmePriorityPolicyEntity = null,
            resultIndicatorEntity = null,
            translatedValues = mutableSetOf()
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
    lateinit var projectCollaboratorRepository: UserProjectCollaboratorRepository

    @MockK
    lateinit var partnerCollaboratorRepository: UserPartnerCollaboratorRepository


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
            projectCollaboratorRepository,
            partnerCollaboratorRepository
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
                    deliverables = activity2.deliverables
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
        every { activityPartnerMock.id } returns WorkPackageActivityPartnerId(activity1, activityProjectPartnerId)
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
        every { repositoryActivity.flush() } returns Unit
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
        every { repositoryOutput.findAllByOutputIdWorkPackageIdOrderByOutputIdOutputNumber(WORK_PACKAGE_ID) } returns mutableListOf()
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
