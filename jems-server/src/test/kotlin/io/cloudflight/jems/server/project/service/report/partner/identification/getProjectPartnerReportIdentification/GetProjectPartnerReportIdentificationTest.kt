package io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.partner.identification.ProjectReportIdentificationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional.empty
import java.util.Optional.of

internal class GetProjectPartnerReportIdentificationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 520L

        private val emptyIdentification = ProjectPartnerReportIdentification(
            startDate = null,
            endDate = null,
            summary = emptySet(),
            problemsAndDeviations = emptySet(),
            spendingDeviations = emptySet(),
            targetGroups = emptyList(),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = null,
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ZERO,
                differenceFromPlan = BigDecimal.ZERO,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                nextReportForecast = BigDecimal.ZERO,
            )
        )

        private fun report(status: ReportStatus) = ProjectPartnerReport(
            id = 0L,
            reportNumber = 1,
            status = status,
            version = "",
            identification = mockk(),
        )

        private fun identification(
            periodDetail: ProjectPartnerReportPeriod,
            differenceFromPlan: BigDecimal,
            differenceFromPlanPercentage: BigDecimal,
            current: BigDecimal,
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
                previouslyReported = BigDecimal.valueOf(40),
                differenceFromPlan = differenceFromPlan,
                differenceFromPlanPercentage = differenceFromPlanPercentage,
                nextReportForecast = BigDecimal.valueOf(12),
            )
        )
    }

    @MockK
    lateinit var identificationPersistence: ProjectReportIdentificationPersistence
    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var getReportIdentification: GetProjectPartnerReportIdentification

    @Test
    fun `getForPartner - submitted`() {
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = 225L) } returns report(ReportStatus.Submitted)

        val period = ProjectPartnerReportPeriod(number = 3, periodBudget = BigDecimal.valueOf(15), periodBudgetCumulative = BigDecimal.valueOf(75), 7, 9)
        val identification = identification(
            periodDetail = period,
            differenceFromPlan = BigDecimal.ZERO,
            differenceFromPlanPercentage = BigDecimal.ZERO,
            current = BigDecimal.valueOf(20),
        )

        every { identificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 225L) } returns of(identification)

        assertThat(getReportIdentification.getIdentification(PARTNER_ID, reportId = 225L)).isEqualTo(
            identification(
                periodDetail = period,
                differenceFromPlan = BigDecimal.valueOf(15),
                differenceFromPlanPercentage = BigDecimal.valueOf(8000, 2),
                current = BigDecimal.valueOf(20),
            )
        )
    }

    @Test
    fun `getForPartner - not submitted`() {
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = 227L) } returns report(ReportStatus.Draft)

        val period = ProjectPartnerReportPeriod(number = 2, periodBudget = BigDecimal.valueOf(14), periodBudgetCumulative = BigDecimal.valueOf(40), 7, 9)
        val identification = identification(
            periodDetail = period,
            differenceFromPlan = BigDecimal.ZERO,
            differenceFromPlanPercentage = BigDecimal.ZERO,
            current = BigDecimal.valueOf(15),
        )

        every { identificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 227L) } returns of(identification)

        assertThat(getReportIdentification.getIdentification(PARTNER_ID, reportId = 227L)).isEqualTo(
            identification(
                periodDetail = period,
                differenceFromPlan = BigDecimal.valueOf(-1),
                differenceFromPlanPercentage = BigDecimal.valueOf(10250, 2),
                current = BigDecimal.ONE,
            )
        )
    }

    @Test
    fun `getForPartner - not submitted - period 0 (avoid division)`() {
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = 228L) } returns report(ReportStatus.Draft)

        val period = ProjectPartnerReportPeriod(number = 5, periodBudget = BigDecimal.ZERO, periodBudgetCumulative = BigDecimal.ZERO, 9, 10)
        val identification = identification(
            periodDetail = period,
            differenceFromPlan = BigDecimal.ZERO,
            differenceFromPlanPercentage = BigDecimal.ZERO,
            current = BigDecimal.valueOf(15),
        )

        every { identificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 228L) } returns of(identification)

        assertThat(getReportIdentification.getIdentification(PARTNER_ID, reportId = 228L)).isEqualTo(
            identification(
                periodDetail = period,
                differenceFromPlan = BigDecimal.ZERO,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                current = BigDecimal.ONE,
            )
        )
    }

    @Test
    fun `getForPartner - empty - submitted`() {
        every { identificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 230L) } returns empty()
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = 230L) } returns report(ReportStatus.Submitted)
        assertThat(getReportIdentification.getIdentification(PARTNER_ID, reportId = 230L)).isEqualTo(emptyIdentification)
    }

    @Test
    fun `getForPartner - empty - NOT submitted`() {
        every { identificationPersistence.getPartnerReportIdentification(PARTNER_ID, reportId = 232L) } returns empty()
        every { reportPersistence.getPartnerReportById(partnerId = PARTNER_ID, reportId = 232L) } returns report(ReportStatus.Draft)
        assertThat(getReportIdentification.getIdentification(PARTNER_ID, reportId = 232L)).isEqualTo(
            emptyIdentification.copy(spendingProfile = emptyIdentification.spendingProfile.copy(currentReport = BigDecimal.ONE))
        )
    }
}
