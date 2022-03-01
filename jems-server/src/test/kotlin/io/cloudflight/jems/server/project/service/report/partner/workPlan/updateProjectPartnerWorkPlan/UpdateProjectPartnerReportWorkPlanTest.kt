package io.cloudflight.jems.server.project.service.report.partner.workPlan.updateProjectPartnerWorkPlan

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.partner.workPlan.ProjectReportWorkPlanPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class UpdateProjectPartnerReportWorkPlanTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 489L

        private val oldWorkPlan = ProjectPartnerReportWorkPackage(
            id = 45L,
            number = 45,
            description = setOf(InputTranslation(EN, "[45] description")),
            activities = listOf(
                ProjectPartnerReportWorkPackageActivity(
                    id = 99L,
                    number = 99,
                    title = setOf(InputTranslation(EN, "[99] title")),
                    progress = setOf(InputTranslation(EN, "[99] progress")),
                    deliverables = listOf(
                        ProjectPartnerReportWorkPackageActivityDeliverable(
                            id = 87L,
                            number = 87,
                            title = setOf(InputTranslation(EN, "[87] title")),
                            contribution = true,
                            evidence = false,
                        )
                    ),
                )
            ),
            outputs = listOf(
                ProjectPartnerReportWorkPackageOutput(
                    id = 61,
                    number = 61,
                    title = setOf(InputTranslation(EN, "[61] title")),
                    contribution = false,
                    evidence = null,
                )
            )
        )

        private val newWorkPlan = ProjectPartnerReportWorkPackage(
            id = 45L,
            number = 45,
            description = setOf(InputTranslation(EN, "[45] description new")),
            activities = listOf(
                ProjectPartnerReportWorkPackageActivity(
                    id = 99L,
                    number = 99,
                    title = setOf(InputTranslation(EN, "[99] title")),
                    progress = setOf(InputTranslation(EN, "[99] progress new")),
                    deliverables = listOf(
                        ProjectPartnerReportWorkPackageActivityDeliverable(
                            id = 87L,
                            number = 87,
                            title = setOf(InputTranslation(EN, "[87] title")),
                            contribution = false,
                            evidence = false,
                        )
                    ),
                )
            ),
            outputs = listOf(
                ProjectPartnerReportWorkPackageOutput(
                    id = 61,
                    number = 61,
                    title = setOf(InputTranslation(EN, "[61] title")),
                    contribution = null,
                    evidence = true,
                )
            )
        )

        private val updateWorkPlanModel = UpdateProjectPartnerReportWorkPackage(
            id = 45L,
            description = setOf(InputTranslation(EN, "[45] description new")),
            activities = listOf(
                UpdateProjectPartnerReportWorkPackageActivity(
                    id = 99L,
                    progress = setOf(InputTranslation(EN, "[99] progress new")),
                    deliverables = listOf(
                        UpdateProjectPartnerReportWorkPackageActivityDeliverable(
                            id = 87L,
                            contribution = false,
                            evidence = false,
                        )
                    ),
                )
            ),
            outputs = listOf(
                UpdateProjectPartnerReportWorkPackageOutput(
                    id = 61,
                    contribution = null,
                    evidence = true,
                )
            )
        )

        private val updateWorkPlanModelNoChanges = UpdateProjectPartnerReportWorkPackage(
            id = 45L,
            description = setOf(InputTranslation(EN, "[45] description")),
            activities = listOf(
                UpdateProjectPartnerReportWorkPackageActivity(
                    id = 99L,
                    progress = setOf(InputTranslation(EN, "[99] progress")),
                    deliverables = listOf(
                        UpdateProjectPartnerReportWorkPackageActivityDeliverable(
                            id = 87L,
                            contribution = true,
                            evidence = false,
                        )
                    ),
                )
            ),
            outputs = listOf(
                UpdateProjectPartnerReportWorkPackageOutput(
                    id = 61,
                    contribution = false,
                    evidence = null,
                )
            )
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportWpPersistence: ProjectReportWorkPlanPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updateWorkPlan: UpdateProjectPartnerReportWorkPlan

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence)
        clearMocks(reportWpPersistence)
    }

    @Test
    fun update() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 11L) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "4.12.0")
        every { reportWpPersistence.getPartnerReportWorkPlanById(PARTNER_ID, reportId = 11) } returnsMany listOf(
            listOf(oldWorkPlan),
            listOf(newWorkPlan),
        )
        every { reportWpPersistence.updatePartnerReportWorkPackage(any(), any()) } answers {}
        every { reportWpPersistence.updatePartnerReportWorkPackageActivity(any(), any()) } answers {}
        every { reportWpPersistence.updatePartnerReportWorkPackageDeliverable(any(), any(), any()) } answers {}
        every { reportWpPersistence.updatePartnerReportWorkPackageOutput(any(), any(), any()) } answers {}

        assertThat(updateWorkPlan.update(PARTNER_ID, reportId = 11L, listOf(updateWorkPlanModel)))
            .containsExactly(newWorkPlan)

        verify(exactly = 1) { reportWpPersistence
            .updatePartnerReportWorkPackage(45L, setOf(InputTranslation(EN, "[45] description new"))) }
        verify(exactly = 1) { reportWpPersistence
            .updatePartnerReportWorkPackageActivity(99L, setOf(InputTranslation(EN, "[99] progress new"))) }
        verify(exactly = 1) { reportWpPersistence
            .updatePartnerReportWorkPackageDeliverable(87L, false, false) }
        verify(exactly = 1) { reportWpPersistence
            .updatePartnerReportWorkPackageOutput(61L, null, true) }

        verify(exactly = 2) { reportWpPersistence.getPartnerReportWorkPlanById(PARTNER_ID, reportId = 11L) }
    }

    @Test
    fun `update - no changes`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 12L) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "4.12.0")
        every { reportWpPersistence.getPartnerReportWorkPlanById(PARTNER_ID, reportId = 12) } returnsMany listOf(
            listOf(oldWorkPlan),
            listOf(oldWorkPlan),
        )
        every { reportWpPersistence.updatePartnerReportWorkPackage(any(), any()) } answers {}
        every { reportWpPersistence.updatePartnerReportWorkPackageActivity(any(), any()) } answers {}
        every { reportWpPersistence.updatePartnerReportWorkPackageDeliverable(any(), any(), any()) } answers {}
        every { reportWpPersistence.updatePartnerReportWorkPackageOutput(any(), any(), any()) } answers {}

        assertThat(updateWorkPlan.update(PARTNER_ID, reportId = 12L, listOf(updateWorkPlanModelNoChanges)))
            .containsExactly(oldWorkPlan)

        verify(exactly = 0) { reportWpPersistence.updatePartnerReportWorkPackage(any(), any()) }
        verify(exactly = 0) { reportWpPersistence.updatePartnerReportWorkPackageActivity(any(), any()) }
        verify(exactly = 0) { reportWpPersistence.updatePartnerReportWorkPackageDeliverable(any(), any(), any()) }
        verify(exactly = 0) { reportWpPersistence.updatePartnerReportWorkPackageOutput(any(), any(), any()) }

        verify(exactly = 2) { reportWpPersistence.getPartnerReportWorkPlanById(PARTNER_ID, reportId = 12L) }
    }

    @Test
    fun `update - wrong status`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, reportId = 0L) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Submitted, "4.12.0")
        assertThrows<ReportAlreadyClosed> { updateWorkPlan.update(PARTNER_ID, reportId = 0L, emptyList()) }
    }
}
