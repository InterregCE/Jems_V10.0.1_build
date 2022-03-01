package io.cloudflight.jems.server.project.controller.report.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportIdentificationTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.UpdateProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.UpdateProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentificationInteractor
import io.cloudflight.jems.server.project.service.report.partner.identification.updateProjectPartnerReportIdentification.UpdateProjectPartnerReportIdentificationInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class ProjectPartnerReportIdentificationControllerTest {

    companion object {
        private const val PARTNER_ID = 525L
        private const val REPORT_ID = 605L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        private val dummyIdentification = ProjectPartnerReportIdentification(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            period = null,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.BusinessSupportOrganisation,
                    sortNumber = 1,
                    specification = setOf(InputTranslation(EN, "spec EN")),
                    description = setOf(InputTranslation(EN, "desc EN")),
                ),
            ),
        )

        private val expectedDummyIdentification = ProjectPartnerReportIdentificationDTO(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            period = null,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroupDTO(
                    type = ProjectTargetGroupDTO.BusinessSupportOrganisation,
                    sortNumber = 1,
                    specification = setOf(InputTranslation(EN, "spec EN")),
                    description = setOf(InputTranslation(EN, "desc EN")),
                ),
            ),
        )

        private val dummyIdentificationUpdateDto = UpdateProjectPartnerReportIdentificationDTO(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            period = null,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            targetGroups = listOf(
                setOf(InputTranslation(EN, "spec EN")),
            ),
        )

        private val expectedDummyUpdateIdentification = UpdateProjectPartnerReportIdentification(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            period = null,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            targetGroups = listOf(
                setOf(InputTranslation(EN, "spec EN")),
            ),
        )

    }

    @MockK
    lateinit var getIdentification: GetProjectPartnerReportIdentificationInteractor

    @MockK
    lateinit var updateIdentification: UpdateProjectPartnerReportIdentificationInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportIdentificationController

    @Test
    fun getWorkPlan() {
        every { getIdentification.getIdentification(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            dummyIdentification
        assertThat(controller.getIdentification(partnerId = PARTNER_ID, reportId = REPORT_ID))
            .isEqualTo(expectedDummyIdentification)
    }

    @Test
    fun update() {
        val slotData = slot<UpdateProjectPartnerReportIdentification>()
        every { updateIdentification.updateIdentification(
            partnerId = PARTNER_ID,
            reportId = REPORT_ID,
            data = capture(slotData),
        ) } returns dummyIdentification

        assertThat(controller.updateIdentification(
            partnerId = PARTNER_ID,
            reportId = REPORT_ID,
            identification = dummyIdentificationUpdateDto,
        )).isEqualTo(expectedDummyIdentification)

        assertThat(slotData.captured).isEqualTo(expectedDummyUpdateIdentification)
    }

}
