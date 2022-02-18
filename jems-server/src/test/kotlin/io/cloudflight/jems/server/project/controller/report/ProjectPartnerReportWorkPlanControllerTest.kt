package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageActivityDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageActivityDeliverableDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.partner.workPlan.UpdateProjectPartnerReportWorkPackageOutputDTO
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.workPlan.update.UpdateProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.partner.workPlan.getProjectPartnerWorkPlan.GetProjectPartnerReportWorkPlanInteractor
import io.cloudflight.jems.server.project.service.report.partner.workPlan.updateProjectPartnerWorkPlan.UpdateProjectPartnerReportWorkPlanInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProjectPartnerReportWorkPlanControllerTest {

    companion object {
        private const val PARTNER_ID = 447L
        private const val REPORT_ID = 466L

        private val dummyWorkPlan = ProjectPartnerReportWorkPackage(
            id = 754,
            number = 1,
            description = setOf(InputTranslation(SystemLanguage.EN, "WP1")),
            activities = listOf(
                ProjectPartnerReportWorkPackageActivity(
                    id = 755,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "A1.1")),
                    progress = setOf(InputTranslation(SystemLanguage.EN, "custom title")),
                    deliverables = listOf(
                        ProjectPartnerReportWorkPackageActivityDeliverable(
                            id = 756,
                            number = 1,
                            title = setOf(InputTranslation(SystemLanguage.EN, "D1.1.1")),
                            contribution = true,
                            evidence = false,
                        )
                    ),
                ),
            ),
            outputs = listOf(
                ProjectPartnerReportWorkPackageOutput(
                    id = 757,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "O1")),
                    contribution = true,
                    evidence = false,
                )
            ),
        )

        private val dummyWorkPlanDto = ProjectPartnerReportWorkPackageDTO(
            id = 754,
            number = 1,
            description = setOf(InputTranslation(SystemLanguage.EN, "WP1")),
            activities = listOf(
                ProjectPartnerReportWorkPackageActivityDTO(
                    id = 755,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "A1.1")),
                    progress = setOf(InputTranslation(SystemLanguage.EN, "custom title")),
                    deliverables = listOf(
                        ProjectPartnerReportWorkPackageActivityDeliverableDTO(
                            id = 756,
                            number = 1,
                            title = setOf(InputTranslation(SystemLanguage.EN, "D1.1.1")),
                            contribution = true,
                            evidence = false,
                        )
                    ),
                ),
            ),
            outputs = listOf(
                ProjectPartnerReportWorkPackageOutputDTO(
                    id = 757,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "O1")),
                    contribution = true,
                    evidence = false,
                )
            ),
        )

        private val dummyWorkPlanUpdateDto = UpdateProjectPartnerReportWorkPackageDTO(
            id = 754,
            description = setOf(InputTranslation(SystemLanguage.EN, "WP1")),
            activities = listOf(
                UpdateProjectPartnerReportWorkPackageActivityDTO(
                    id = 755,
                    progress = setOf(InputTranslation(SystemLanguage.EN, "custom title")),
                    deliverables = listOf(
                        UpdateProjectPartnerReportWorkPackageActivityDeliverableDTO(
                            id = 756,
                            contribution = true,
                            evidence = false,
                        )
                    ),
                ),
            ),
            outputs = listOf(
                UpdateProjectPartnerReportWorkPackageOutputDTO(
                    id = 757,
                    contribution = true,
                    evidence = false,
                )
            ),
        )

        private val expectedDummyWorkPlanUpdateModel = UpdateProjectPartnerReportWorkPackage(
            id = 754,
            description = setOf(InputTranslation(SystemLanguage.EN, "WP1")),
            activities = listOf(
                UpdateProjectPartnerReportWorkPackageActivity(
                    id = 755,
                    progress = setOf(InputTranslation(SystemLanguage.EN, "custom title")),
                    deliverables = listOf(
                        UpdateProjectPartnerReportWorkPackageActivityDeliverable(
                            id = 756,
                            contribution = true,
                            evidence = false,
                        )
                    ),
                ),
            ),
            outputs = listOf(
                UpdateProjectPartnerReportWorkPackageOutput(
                    id = 757,
                    contribution = true,
                    evidence = false,
                )
            ),
        )

    }

    @MockK
    lateinit var getPartnerReportWorkPlan: GetProjectPartnerReportWorkPlanInteractor

    @MockK
    lateinit var updatePartnerReportWorkPlan: UpdateProjectPartnerReportWorkPlanInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportWorkPlanController

    @Test
    fun getWorkPlan() {
        every { getPartnerReportWorkPlan.getForPartner(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            listOf(dummyWorkPlan)
        assertThat(controller.getWorkPlan(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .containsExactly(dummyWorkPlanDto)
    }

    @Test
    fun update() {
        val slotData = slot<List<UpdateProjectPartnerReportWorkPackage>>()
        every { updatePartnerReportWorkPlan.update(
            partnerId = PARTNER_ID,
            reportId = REPORT_ID,
            workPlan = capture(slotData),
        ) } returns listOf(dummyWorkPlan)

        assertThat(controller.updateWorkPlan(
            partnerId = PARTNER_ID,
            reportId = REPORT_ID,
            workPackages = listOf(dummyWorkPlanUpdateDto),
        )).containsExactly(dummyWorkPlanDto)

        assertThat(slotData.captured).hasSize(1)
        assertThat(slotData.captured.first()).isEqualTo(expectedDummyWorkPlanUpdateModel)
    }
}
