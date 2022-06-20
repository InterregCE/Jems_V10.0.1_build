package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.model.financialOverview.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional.of

internal class GetProjectPartnerReportIdentificationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 520L

        private fun identification(
            periodDetail: ProjectPartnerReportPeriod,
            differenceFromPlan: BigDecimal = BigDecimal.valueOf(99999, 2),
            differenceFromPlanPercentage: BigDecimal = BigDecimal.valueOf(99999, 2),
            current: BigDecimal = BigDecimal.valueOf(99999, 2),
            previously: BigDecimal = BigDecimal.valueOf(99999, 2),
        ) = ProjectPartnerReportIdentification(
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            summary = setOf(InputTranslation(SystemLanguage.EN, "summ")),
            problemsAndDeviations = setOf(InputTranslation(SystemLanguage.EN, "p&d")),
            spendingDeviations = setOf(InputTranslation(SystemLanguage.EN, "sd")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.EducationTrainingCentreAndSchool,
                    sortNumber = 1,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "spec")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "target desc")),
                )
            ),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = periodDetail,
                currentReport = current,
                previouslyReported = previously,
                differenceFromPlan = differenceFromPlan,
                differenceFromPlanPercentage = differenceFromPlanPercentage,
                nextReportForecast = BigDecimal.valueOf(12),
            )
        )

        private val totalLine = ExpenditureCostCategoryBreakdownLine(
            flatRate = null,
            totalEligibleBudget = BigDecimal.valueOf(1969, 2) /* not important */,
            previouslyReported = BigDecimal.valueOf(40),
            currentReport = BigDecimal.valueOf(21),
        )
    }

    @MockK
    lateinit var identificationPersistence: ProjectReportIdentificationPersistence
    @MockK
    lateinit var reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService

    @InjectMockKs
    lateinit var getReportIdentification: GetProjectPartnerReportIdentification

    @Test
    fun getIdentification() {
        val period = ProjectPartnerReportPeriod(number = 3, periodBudget = BigDecimal.valueOf(15), periodBudgetCumulative = BigDecimal.valueOf(75), 7, 9)
        val identification = identification(
            periodDetail = period,
            /* other values from persistence are not filled in */
        )
        every { identificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 225L) } returns of(identification)

        val expenditures = mockk<ExpenditureCostCategoryBreakdown>()
        every { expenditures.total } returns totalLine
        every { reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(partnerId = PARTNER_ID, reportId = 225L) } returns expenditures

        assertThat(getReportIdentification.getIdentification(PARTNER_ID, reportId = 225L)).isEqualTo(
            identification(
                periodDetail = period,
                differenceFromPlan = BigDecimal.valueOf(14),
                differenceFromPlanPercentage = BigDecimal.valueOf(8133, 2),
                current = BigDecimal.valueOf(21),
                previously = BigDecimal.valueOf(40),
            )
        )
    }

    @Test
    fun `getIdentification - period 0 (avoid division)`() {
        val period = ProjectPartnerReportPeriod(number = 5, periodBudget = BigDecimal.ZERO, periodBudgetCumulative = BigDecimal.ZERO, 9, 10)
        val identification = identification(
            differenceFromPlan = BigDecimal.ZERO,
            differenceFromPlanPercentage = BigDecimal.ZERO,
            periodDetail = period,
        )
        every { identificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 228L) } returns of(identification)

        val expenditures = mockk<ExpenditureCostCategoryBreakdown>()
        every { expenditures.total } returns totalLine
        every { reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(partnerId = PARTNER_ID, reportId = 228L) } returns expenditures

        assertThat(getReportIdentification.getIdentification(PARTNER_ID, reportId = 228L)).isEqualTo(
            identification(
                periodDetail = period,
                differenceFromPlan = BigDecimal.ZERO,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                current = BigDecimal.valueOf(21),
                previously = BigDecimal.valueOf(40),
            )
        )
    }

}
