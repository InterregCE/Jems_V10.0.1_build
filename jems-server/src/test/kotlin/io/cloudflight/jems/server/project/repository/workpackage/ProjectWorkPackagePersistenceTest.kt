package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.indicator.IndicatorOutput
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorOutputRepository
import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import io.cloudflight.jems.server.project.entity.ProjectPeriodId
import io.cloudflight.jems.server.project.entity.TranslationWorkPackageId
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageTransl
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.output.WorkPackageOutputId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityId
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityTranslationId
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableId
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.deliverable.WorkPackageActivityDeliverableTranslationId
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.repository.description.ProjectPeriodRepository
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.workpackage.investment.WorkPackageInvestmentRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.WorkPackageOutputRepository
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.project
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverableTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.util.Optional

@ExtendWith(MockKExtension::class)
class ProjectWorkPackagePersistenceTest {

    companion object {
        private const val WORK_PACKAGE_ID = 1L
        private const val WORK_PACKAGE_ID_2 = 654L

        private val activityId1 = WorkPackageActivityId(workPackageId = WORK_PACKAGE_ID, activityNumber = 1)
        private val activityId2 = WorkPackageActivityId(workPackageId = WORK_PACKAGE_ID, activityNumber = 2)
        private val outputId1 = WorkPackageOutputId(workPackageId = WORK_PACKAGE_ID, outputNumber = 1)
        private val outputId2 = WorkPackageOutputId(workPackageId = WORK_PACKAGE_ID, outputNumber = 2)

        private fun trIdAct(activityId: WorkPackageActivityId, lang: SystemLanguage) = WorkPackageActivityTranslationId(
            activityId = activityId,
            language = lang
        )

        private fun trIdActDel(deliverableId: WorkPackageActivityDeliverableId, lang: SystemLanguage) = WorkPackageActivityDeliverableTranslationId(
            deliverableId = deliverableId,
            language = lang
        )

        private val deliverableId1_activityId1 = WorkPackageActivityDeliverableId(activityId = activityId1, deliverableNumber = 1)
        private val deliverableId2_activityId1 = WorkPackageActivityDeliverableId(activityId = activityId1, deliverableNumber = 2)

        private val deliverableId1_activityId2 = WorkPackageActivityDeliverableId(activityId = activityId2, deliverableNumber = 1)
        private val deliverableId2_activityId2 = WorkPackageActivityDeliverableId(activityId = activityId2, deliverableNumber = 2)
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
            activityId = activityId2,
            startPeriod = 4,
            endPeriod = 6
        )
        val activity1_model = WorkPackageActivity(
            startPeriod = 4,
            endPeriod = 6
        )
        val activity2 = WorkPackageActivityEntity(
            activityId = activityId1,
            startPeriod = 1,
            endPeriod = 3,
            translatedValues = setOf(
                WorkPackageActivityTranslationEntity(translationId = trIdAct(activityId1, SK), title = "sk_title", description = ""),
                WorkPackageActivityTranslationEntity(translationId = trIdAct(activityId1, CS), title = null, description = "cs_desc"),
                WorkPackageActivityTranslationEntity(translationId = trIdAct(activityId1, EN), title = " ", description = " ")
            ),
            deliverables = listOf(deliverable2_2, deliverable2_1)
        )
        val activity2_model = WorkPackageActivity(
            translatedValues = setOf(
                WorkPackageActivityTranslatedValue(language = SK, title = "sk_title", description = ""),
                WorkPackageActivityTranslatedValue(language = CS, title = null, description = "cs_desc"),
                WorkPackageActivityTranslatedValue(language = EN, title = " ", description = " ")
            ),
            startPeriod = 1,
            endPeriod = 3,
            deliverables = listOf(
                WorkPackageActivityDeliverable(
                    period = 1,
                    translatedValues = setOf(
                        WorkPackageActivityDeliverableTranslatedValue(language = SK, description = "sk_deliverable_desc"),
                        WorkPackageActivityDeliverableTranslatedValue(language = CS, description = ""),
                        WorkPackageActivityDeliverableTranslatedValue(language = EN, description = null)
                    )
                ),
                WorkPackageActivityDeliverable(
                    period = 2
                )
            )
        )

        val output1 = WorkPackageOutputEntity(
            outputId = outputId1,
            period = ProjectPeriodEntity(ProjectPeriodId(project.id, number = 1), start = 1, end = 2),
        )
        val output1_model = WorkPackageOutput(
            outputNumber = 1,
            periodNumber = 1,
        )
        val output2 = WorkPackageOutputEntity(
            outputId = outputId2,
            period = ProjectPeriodEntity(ProjectPeriodId(project.id, number = 2), start = 3, end = 4),
        )
        val output2_model = WorkPackageOutput(
            outputNumber = 2,
            periodNumber = 2,
        )

        val workPackageWithActivities = WorkPackageEntity(
            id = WORK_PACKAGE_ID,
            project = project,
            activities = listOf(activity1, activity2),
            translatedValues = setOf(
                WorkPackageTransl(
                    translationId = TranslationWorkPackageId(WORK_PACKAGE_ID, CS),
                    name = "WP CS name"
                )
            )
        )

        val activity = WorkPackageActivity(
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

        val workPackageOutput = WorkPackageOutput(
            outputNumber = 1,
            translatedValues = setOf(
                WorkPackageOutputTranslatedValue(language = EN, title = "text", description = "test")
            ),
            periodNumber = 3,
            targetValue = "target",
            programmeOutputIndicatorId = 2
        )
        val indicatorOutput = IndicatorOutput(
            id = 2,
            identifier = "t",
            name = "test",
            code = "tst",
            measurementUnit = "x",
            programmePriorityPolicy = null
        )
        val projectPeriodEntity = ProjectPeriodEntity(ProjectPeriodId(WORK_PACKAGE_ID, 3), 1, 12)

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
    lateinit var indicatorOutputRepository: IndicatorOutputRepository

    @RelaxedMockK
    lateinit var projectPeriodRepository: ProjectPeriodRepository

    @InjectMockKs
    private lateinit var persistence: WorkPackagePersistenceProvider

    @Test
    fun `get full-rich work packages`() {
        val emptyWP = WorkPackageEntity(
            id = WORK_PACKAGE_ID_2,
            project = project,
        )
        every { repository.findAllByProjectId(eq(1), Pageable.unpaged()) } returns PageImpl(listOf(
            workPackageWithActivities.copy(activities = emptyList(), outputs = emptyList()),
            emptyWP
        ))
        every { repositoryActivity.findAllByActivityIdWorkPackageIdIn(setOf(WORK_PACKAGE_ID, WORK_PACKAGE_ID_2)) } returns listOf(activity2, activity1)
        every { repositoryOutput.findAllByOutputIdWorkPackageIdIn(setOf(WORK_PACKAGE_ID, WORK_PACKAGE_ID_2)) } returns listOf(output2, output1)

        val result = persistence.getRichWorkPackagesByProjectId(1, Pageable.unpaged())
        assertThat(result.totalElements).isEqualTo(2)
        assertThat(result.content.map { it.id }).containsExactly(WORK_PACKAGE_ID, WORK_PACKAGE_ID_2)
        assertThat(result.content[0].translatedValues).containsExactly(ProjectWorkPackageTranslatedValue(CS, "WP CS name"))
        assertThat(result.content[0].activities).containsExactly(activity2_model, activity1_model)
        assertThat(result.content[0].outputs).containsExactly(output1_model, output2_model)
    }

    @Test
    fun `get work package activities - not-existing work package`() {
        every { repository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getWorkPackageActivitiesForWorkPackage(-1) }
        assertThat(ex.entity).isEqualTo("workPackage")
    }

    @Test
    fun `work package activities and deliverables are correctly mapped and sorted`() {
        every { repository.findById(eq(1)) } returns Optional.of(workPackageWithActivities)
        assertThat(persistence.getWorkPackageActivitiesForWorkPackage(1)).containsExactly(
            activity2_model, activity1_model,
        )
    }

    @Test
    fun updateWorkPackageActivities() {
        val workPackageSlot = slot<WorkPackageEntity>()
        every { repository.findById(WORK_PACKAGE_ID) } returns Optional.of(WorkPackageEntity(
            id = WORK_PACKAGE_ID,
            project = project,
            activities = emptyList()
        ))
        // we do not need to test mapping back to model as that is covered by getWorkPackageActivitiesForWorkPackage
        every { repository.save(capture(workPackageSlot)) } returnsArgument 0

        val toBeSaved = listOf(
            activity,
            WorkPackageActivity(
                startPeriod = 4,
                endPeriod = 6,
                deliverables = listOf(
                    WorkPackageActivityDeliverable(period = 4),
                    WorkPackageActivityDeliverable(period = 5),
                    WorkPackageActivityDeliverable(period = 6)
                )
            )
        )

        persistence.updateWorkPackageActivities(1L, toBeSaved)

        assertThat(workPackageSlot.captured.activities).containsExactly(
            WorkPackageActivityEntity(
                activityId = activityId1,
                translatedValues = setOf(
                    WorkPackageActivityTranslationEntity(translationId = trIdAct(activityId1, EN), title = null, description = "en_desc"),
                    WorkPackageActivityTranslationEntity(translationId = trIdAct(activityId1, CS), title = "", description = null),
                    WorkPackageActivityTranslationEntity(translationId = trIdAct(activityId1, SK), title = "sk_title", description = "sk_desc")
                ),
                startPeriod = 1,
                endPeriod = 3,
                deliverables = listOf(
                    WorkPackageActivityDeliverableEntity(
                        deliverableId = deliverableId1_activityId1,
                        startPeriod = 1,
                        translatedValues = setOf(
                            WorkPackageActivityDeliverableTranslationEntity(translationId = trIdActDel(deliverableId1_activityId1, EN), description = "en_deliv_desc"),
                            WorkPackageActivityDeliverableTranslationEntity(translationId = trIdActDel(deliverableId1_activityId1, CS), description = null)
                        )
                    )
                )
            ),
            WorkPackageActivityEntity(
                activityId = activityId2,
                startPeriod = 4,
                endPeriod = 6,
                deliverables = listOf(
                    WorkPackageActivityDeliverableEntity(deliverableId = deliverableId1_activityId2, startPeriod = 4),
                    WorkPackageActivityDeliverableEntity(deliverableId = deliverableId2_activityId2, startPeriod = 5),
                    WorkPackageActivityDeliverableEntity(deliverableId = deliverableId3_activityId2, startPeriod = 6)
                )
            )
        )
    }

    @Test
    fun `work package outputs are updated`() {
        every { repository.findById(1) } returns Optional.of(workPackageWithActivities)
        every { indicatorOutputRepository.findById(2) } returns Optional.of(indicatorOutput)
        every { projectPeriodRepository.findByIdProjectIdAndIdNumber(WORK_PACKAGE_ID, 3) } returns projectPeriodEntity
        val predictedWorkPackageOutput = WorkPackageOutputEntity(
            outputId = WorkPackageOutputId(WORK_PACKAGE_ID, 1),
            period = projectPeriodEntity,
            programmeOutputIndicator = indicatorOutput,
            targetValue = workPackageOutput.targetValue
        )
        val predictedResult = workPackageWithActivities.copy(outputs = listOf(predictedWorkPackageOutput))
        every { repository.save(any<WorkPackageEntity>()) } returns predictedResult

        val result = persistence.updateWorkPackageOutputs(1, listOf(workPackageOutput))

        assertThat(result).isEqualTo(predictedResult.outputs.toModel())
    }

    @Test
    fun countWorkPackageInvestments() {
        every { investmentRepository.countAllByWorkPackageId(1L) } returns 62
        assertThat(persistence.countWorkPackageInvestments(1L)).isEqualTo(62)
    }

    @Test
    fun `work package investment is added`() {
        every { repository.findById(1) } returns Optional.of(workPackageWithActivities)
        val predictedWorkPackageInvestment = workPackageInvestment.toWorkPackageInvestmentEntity(workPackageWithActivities).copy(
            id = 2
        )
        every { investmentRepository.save(any<WorkPackageInvestmentEntity>()) } returns predictedWorkPackageInvestment
        val sortedInvestments = listOf(predictedWorkPackageInvestment)
        every { investmentRepository.findAllByWorkPackageId(1, Sort.by("id")) } returns PageImpl(sortedInvestments)
        every { investmentRepository.saveAll(sortedInvestments) } returns sortedInvestments

        val result = persistence.addWorkPackageInvestment(1, workPackageInvestment)

        assertThat(result).isEqualTo(2)
        verify { investmentRepository.findAllByWorkPackageId(1, Sort.by("id")) }
        verify { investmentRepository.saveAll(sortedInvestments) }
    }

}
