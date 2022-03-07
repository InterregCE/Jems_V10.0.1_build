package io.cloudflight.jems.server.project.repository.report.workPlan

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableTranslEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageActivityTranslEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageOutputTranslEntity
import io.cloudflight.jems.server.project.entity.report.workPlan.ProjectPartnerReportWorkPackageTranslEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.ProjectReportPersistenceProvider
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.partner.workPlan.ProjectReportWorkPlanPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.Optional

class ProjectReportWorkPlanPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 362L

        private fun wp(id: Long, report: ProjectPartnerReportEntity) = ProjectPartnerReportWorkPackageEntity(
            id = id,
            reportEntity = report,
            number = id.toInt(),
            workPackageId = null,
            translatedValues = mutableSetOf()
        ).apply {
            translatedValues.add(
                ProjectPartnerReportWorkPackageTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    description = "[$id] description",
                )
            )
        }

        private fun activity(id: Long, wp: ProjectPartnerReportWorkPackageEntity) = ProjectPartnerReportWorkPackageActivityEntity(
            id = id,
            workPackageEntity = wp,
            number = id.toInt(),
            activityId = null,
            translatedValues = mutableSetOf()
        ).apply {
            translatedValues.add(
                ProjectPartnerReportWorkPackageActivityTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    title = "[$id] title",
                    description = "[$id] progress",
                )
            )
        }

        private fun deliverable(id: Long, activity: ProjectPartnerReportWorkPackageActivityEntity) = ProjectPartnerReportWorkPackageActivityDeliverableEntity(
            id = id,
            activityEntity = activity,
            number = id.toInt(),
            deliverableId = null,
            contribution = true,
            evidence = false,
            translatedValues = mutableSetOf()
        ).apply {
            translatedValues.add(
                ProjectPartnerReportWorkPackageActivityDeliverableTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    title = "[$id] title",
                )
            )
        }

        private fun output(id: Long, wp: ProjectPartnerReportWorkPackageEntity) = ProjectPartnerReportWorkPackageOutputEntity(
            id = id,
            workPackageEntity = wp,
            number = id.toInt(),
            contribution = false,
            evidence = null,
            translatedValues = mutableSetOf()
        ).apply {
            translatedValues.add(
                ProjectPartnerReportWorkPackageOutputTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    title = "[$id] title",
                )
            )
        }

        private fun expectedWorkPlan(wpId: Long, activityId: Long, deliverableId: Long, outputId: Long) =
            ProjectPartnerReportWorkPackage(
                id = wpId,
                number = wpId.toInt(),
                description = setOf(InputTranslation(SystemLanguage.EN, "[$wpId] description")),
                activities = listOf(
                    ProjectPartnerReportWorkPackageActivity(
                        id = activityId,
                        number = activityId.toInt(),
                        title = setOf(InputTranslation(SystemLanguage.EN, "[$activityId] title")),
                        progress = setOf(InputTranslation(SystemLanguage.EN, "[$activityId] progress")),
                        deliverables = listOf(
                            ProjectPartnerReportWorkPackageActivityDeliverable(
                                id = deliverableId,
                                number = deliverableId.toInt(),
                                title = setOf(InputTranslation(SystemLanguage.EN, "[$deliverableId] title")),
                                contribution = true,
                                evidence = false,
                            )
                        ),
                    )
                ),
                outputs = listOf(
                    ProjectPartnerReportWorkPackageOutput(
                        id = outputId,
                        number = outputId.toInt(),
                        title = setOf(InputTranslation(SystemLanguage.EN, "[$outputId] title")),
                        contribution = false,
                        evidence = null,
                    )
                )
            )

    }

    @MockK
    lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var workPlanRepository: ProjectPartnerReportWorkPackageRepository

    @MockK
    lateinit var workPlanActivityRepository: ProjectPartnerReportWorkPackageActivityRepository

    @MockK
    lateinit var workPlanActivityDeliverableRepository: ProjectPartnerReportWorkPackageActivityDeliverableRepository

    @MockK
    lateinit var workPlanOutputRepository: ProjectPartnerReportWorkPackageOutputRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportWorkPlanPersistenceProvider

    @Test
    fun getPartnerReportWorkPlanById() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { partnerReportRepository.findByIdAndPartnerId(id = 14L, partnerId = PARTNER_ID) } returns report

        val workPackage = wp(id = 15L, report = report)
        val activity = activity(id = 18L, wp = workPackage)
        val deliverable = deliverable(id = 27L, activity = activity)
        val output = output(id = 35L, wp = workPackage)

        every { workPlanActivityRepository.findAllByWorkPackageEntityReportEntityOrderByNumber(report) } returns
            mutableListOf(activity)
        every { workPlanActivityDeliverableRepository.findAllByActivityEntityWorkPackageEntityReportEntityOrderByNumber(report) } returns
            mutableListOf(deliverable)
        every { workPlanOutputRepository.findAllByWorkPackageEntityReportEntityOrderByNumber(report) } returns
            mutableListOf(output)
        every { workPlanRepository.findAllByReportEntityOrderByNumber(report) } returns
            mutableListOf(workPackage)

        assertThat(persistence.getPartnerReportWorkPlanById(partnerId = PARTNER_ID, reportId = 14L))
            .containsExactly(expectedWorkPlan(wpId = 15L, activityId = 18L, deliverableId = 27L, outputId = 35L))
    }

    @Test
    fun updatePartnerReportWorkPackage() {
        val wp = wp(id = 45L, report = mockk())
        every { workPlanRepository.findById(45L) } returns Optional.of(wp)

        val translations = setOf(
            InputTranslation(SystemLanguage.EN, "language already present"),
            InputTranslation(SystemLanguage.SK, "new language"),
            InputTranslation(SystemLanguage.DE, null),
        )

        persistence.updatePartnerReportWorkPackage(workPackageId = 45L, translations)

        assertThat(wp.translatedValues.map { Pair(it.language(), it.description) }).containsExactly(
            Pair(SystemLanguage.EN, "language already present"),
            Pair(SystemLanguage.SK, "new language"),
            Pair(SystemLanguage.DE, null),
        )
    }

    @Test
    fun updatePartnerReportWorkPackageActivity() {
        val activity = activity(id = 99L, wp = mockk())
        every { workPlanActivityRepository.findById(99L) } returns Optional.of(activity)

        val translations = setOf(
            InputTranslation(SystemLanguage.EN, "language already present"),
            InputTranslation(SystemLanguage.SK, "new language"),
            InputTranslation(SystemLanguage.DE, null),
        )

        persistence.updatePartnerReportWorkPackageActivity(activityId = 99L, translations)

        assertThat(activity.translatedValues.map { Pair(it.language(), it.description) }).containsExactly(
            Pair(SystemLanguage.EN, "language already present"),
            Pair(SystemLanguage.SK, "new language"),
            Pair(SystemLanguage.DE, null),
        )
    }

    @Test
    fun updatePartnerReportWorkPackageDeliverable() {
        val deliverable = deliverable(id = 64L, activity = mockk())
        every { workPlanActivityDeliverableRepository.findById(64L) } returns Optional.of(deliverable)

        persistence.updatePartnerReportWorkPackageDeliverable(
            deliverableId = 64L,
            contribution = false,
            evidence = null,
        )

        assertThat(deliverable.contribution).isFalse
        assertThat(deliverable.evidence).isNull()
    }

    @Test
    fun updatePartnerReportWorkPackageOutput() {
        val output = output(id = 38L, wp = mockk())
        every { workPlanOutputRepository.findById(38L) } returns Optional.of(output)

        persistence.updatePartnerReportWorkPackageOutput(
            outputId = 38L,
            contribution = null,
            evidence = false,
        )

        assertThat(output.contribution).isNull()
        assertThat(output.evidence).isFalse
    }

}
