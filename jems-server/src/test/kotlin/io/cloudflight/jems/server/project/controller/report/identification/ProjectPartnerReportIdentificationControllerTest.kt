package io.cloudflight.jems.server.project.controller.report.identification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.*
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.*
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportAvailablePeriods.GetProjectPartnerReportAvailablePeriodsInteractor
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
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
class ProjectPartnerReportIdentificationControllerTest {

    companion object {
        private const val PARTNER_ID = 525L
        private const val REPORT_ID = 605L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        private val dummyPeriod = ProjectPartnerReportPeriod(number = 3, periodBudget = BigDecimal.ONE, BigDecimal.TEN, 7, 9)

        private val dummyIdentification = ProjectPartnerReportIdentification(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            spendingDeviations = setOf(InputTranslation(EN, "spending EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.BusinessSupportOrganisation,
                    sortNumber = 1,
                    specification = setOf(InputTranslation(EN, "spec EN")),
                    description = setOf(InputTranslation(EN, "desc EN")),
                ),
            ),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = dummyPeriod,
                currentReport = BigDecimal.valueOf(1),
                previouslyReported = BigDecimal.valueOf(2),
                differenceFromPlan = BigDecimal.valueOf(3),
                differenceFromPlanPercentage = BigDecimal.valueOf(4),
                nextReportForecast = BigDecimal.valueOf(5),
            ),
        )

        private val expectedDummyPeriod = ProjectPartnerReportPeriodDTO(number = 3, periodBudget = BigDecimal.ONE, BigDecimal.TEN, 7, 9)

        private val expectedDummyIdentification = ProjectPartnerReportIdentificationDTO(
            startDate = YESTERDAY,
            endDate = TOMORROW,
            summary = setOf(InputTranslation(EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(EN, "problem EN")),
            spendingDeviations = setOf(InputTranslation(EN, "spending EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroupDTO(
                    type = ProjectTargetGroupDTO.BusinessSupportOrganisation,
                    sortNumber = 1,
                    specification = setOf(InputTranslation(EN, "spec EN")),
                    description = setOf(InputTranslation(EN, "desc EN")),
                ),
            ),
            spendingProfile = ProjectPartnerReportSpendingProfileDTO(
                periodDetail = expectedDummyPeriod,
                currentReport = BigDecimal.valueOf(1),
                previouslyReported = BigDecimal.valueOf(2),
                differenceFromPlan = BigDecimal.valueOf(3),
                differenceFromPlanPercentage = BigDecimal.valueOf(4),
                nextReportForecast = BigDecimal.valueOf(5),
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
            nextReportForecast = BigDecimal.valueOf(8),
            spendingDeviations = setOf(InputTranslation(EN, "spending EN")),
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
            spendingDeviations = setOf(InputTranslation(EN, "spending EN")),
            nextReportForecast = BigDecimal.valueOf(8)
        )

    }

    @MockK
    lateinit var getIdentification: GetProjectPartnerReportIdentificationInteractor

    @MockK
    lateinit var updateIdentification: UpdateProjectPartnerReportIdentificationInteractor

    @MockK
    lateinit var getAvailablePeriods: GetProjectPartnerReportAvailablePeriodsInteractor

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

    @Test
    fun getAvailablePeriods() {
        every { getAvailablePeriods.get(PARTNER_ID, reportId = REPORT_ID) } returns listOf(dummyPeriod)
        assertThat(controller.getAvailablePeriods(PARTNER_ID, reportId = REPORT_ID)).containsExactly(expectedDummyPeriod)
    }

}
