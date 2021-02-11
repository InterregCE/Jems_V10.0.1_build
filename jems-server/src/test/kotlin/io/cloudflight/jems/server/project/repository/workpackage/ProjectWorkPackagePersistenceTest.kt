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
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputId
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
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.project
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityDeliverableTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivityTranslatedValue
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
import org.springframework.data.domain.Sort
import java.util.Optional

@ExtendWith(MockKExtension::class)
class ProjectWorkPackagePersistenceTest {

    companion object {
        private const val WORK_PACKAGE_ID = 1L

        private val activityId1 = WorkPackageActivityId(workPackageId = WORK_PACKAGE_ID, activityNumber = 1)
        private val activityId2 = WorkPackageActivityId(workPackageId = WORK_PACKAGE_ID, activityNumber = 2)

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
        private val deliverableId3_activityId2 = WorkPackageActivityDeliverableId(activityId = activityId2, deliverableNumber = 3)

        val workPackageWithActivities = WorkPackageEntity(
            id = WORK_PACKAGE_ID,
            project = project,
            activities = listOf(
                WorkPackageActivityEntity(
                    activityId = activityId2,
                    startPeriod = 4,
                    endPeriod = 6
                ),
                WorkPackageActivityEntity(
                    activityId = activityId1,
                    startPeriod = 1,
                    endPeriod = 3,
                    translatedValues = setOf(
                        WorkPackageActivityTranslationEntity(translationId = trIdAct(activityId1, SK), title = "sk_title", description = ""),
                        WorkPackageActivityTranslationEntity(translationId = trIdAct(activityId1, CS), title = null, description = "cs_desc"),
                        WorkPackageActivityTranslationEntity(translationId = trIdAct(activityId1, EN), title = " ", description = " ")
                    ),
                    deliverables = listOf(
                        WorkPackageActivityDeliverableEntity(
                            deliverableId = deliverableId2_activityId1,
                            startPeriod = 2
                        ),
                        WorkPackageActivityDeliverableEntity(
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
                    )
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

    @RelaxedMockK
    lateinit var investmentRepository: WorkPackageInvestmentRepository

    @RelaxedMockK
    lateinit var indicatorOutputRepository: IndicatorOutputRepository

    @RelaxedMockK
    lateinit var projectPeriodRepository: ProjectPeriodRepository

    @InjectMockKs
    private lateinit var persistence: WorkPackagePersistenceProvider

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
            WorkPackageActivity(
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
            ),
            WorkPackageActivity(
                startPeriod = 4,
                endPeriod = 6
            )
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
        val predictedResult = workPackageWithActivities.copy(workPackageOutputs = listOf(predictedWorkPackageOutput))
        every { repository.save(any<WorkPackageEntity>()) } returns predictedResult

        val result = persistence.updateWorkPackageOutputs(1, listOf(workPackageOutput))

        assertThat(result).isEqualTo(predictedResult.workPackageOutputs.toWorkPackageOutputList())
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
