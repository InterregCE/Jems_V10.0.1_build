package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportInvestmentBreakdown

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateInvestmentPersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdownInteractor.GetReportCertificateInvestmentCalculatorService
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal

class GetReportCertificateInvestmentsBreakdownCalculatorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 591L

        private fun report(status: ProjectReportStatus) : ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            every { report.status } returns status
            return report
        }

        private fun certificate(): List<ProjectPartnerReportSubmissionSummary> {
            val certificate = mockk<ProjectPartnerReportSubmissionSummary>()
            every { certificate.id } returns 1L
            return listOf(certificate)
        }

        private val investment_1 = CertificateInvestmentBreakdownLine(
            reportInvestmentId = 1L,
            investmentId = 101L,
            investmentNumber = 11,
            workPackageNumber = 5,
            title = setOf(InputTranslation(SystemLanguage.EN, "title EN 1")),
            totalEligibleBudget = BigDecimal.valueOf(300L),
            previouslyReported = BigDecimal.valueOf(200L),
            currentReport = BigDecimal.valueOf(100L),
            deactivated = false,
            previouslyVerified = BigDecimal.valueOf(200L),
            currentVerified =  BigDecimal.ZERO
        )

        private val investment_2 = CertificateInvestmentBreakdownLine(
            reportInvestmentId = 2L,
            investmentId = 102L,
            investmentNumber = 12,
            workPackageNumber = 5,
            title = setOf(InputTranslation(SystemLanguage.EN, "title EN 2")),
            totalEligibleBudget = BigDecimal.valueOf(60L),
            previouslyReported = BigDecimal.valueOf(40L),
            currentReport = BigDecimal.valueOf(20L),
            deactivated = true,
            previouslyVerified = BigDecimal.valueOf(40L),
            currentVerified =  BigDecimal.ZERO
        )

        private val expectedDraftResult = CertificateInvestmentBreakdown(
            investments = listOf(
                CertificateInvestmentBreakdownLine(
                    reportInvestmentId = 1L,
                    investmentId = 101L,
                    investmentNumber = 11,
                    workPackageNumber = 5,
                    title = setOf(InputTranslation(SystemLanguage.EN, "title EN 1")),
                    totalEligibleBudget = BigDecimal.valueOf(300L),
                    previouslyReported = BigDecimal.valueOf(200L),
                    currentReport = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(200),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(6667, 2),
                    remainingBudget = BigDecimal.valueOf(100),
                    deactivated = false,
                    previouslyVerified = BigDecimal.valueOf(200L),
                    currentVerified =  BigDecimal.ZERO

                ),
                CertificateInvestmentBreakdownLine(
                    reportInvestmentId = 2L,
                    investmentId = 102L,
                    investmentNumber = 12,
                    workPackageNumber = 5,
                    title = setOf(InputTranslation(SystemLanguage.EN, "title EN 2")),
                    totalEligibleBudget = BigDecimal.valueOf(60L),
                    previouslyReported = BigDecimal.valueOf(40L),
                    currentReport = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(40L),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(6667, 2),
                    remainingBudget = BigDecimal.valueOf(20),
                    deactivated = true,
                    previouslyVerified = BigDecimal.valueOf(40L),
                    currentVerified =  BigDecimal.ZERO
                ),
            ),
            total = CertificateInvestmentBreakdownLine(
                reportInvestmentId = 0L,
                investmentId = 0L,
                investmentNumber = 0,
                workPackageNumber = 0,
                title = emptySet(),
                totalEligibleBudget = BigDecimal.valueOf(360L),
                previouslyReported = BigDecimal.valueOf(240L),
                currentReport = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.valueOf(240),
                totalReportedSoFarPercentage = BigDecimal.valueOf(6667, 2),
                remainingBudget = BigDecimal.valueOf(120),
                deactivated = true,
                previouslyVerified =  BigDecimal.valueOf(240L),
                currentVerified =  BigDecimal.ZERO

            )
        )
    }

    @MockK
    lateinit var reportCertificateInvestmentPersistence: ProjectReportCertificateInvestmentPersistence
    @MockK
    lateinit var reportCertificatePersistence: ProjectReportCertificatePersistence
    @MockK
    lateinit var reportInvestmentPersistence: ProjectPartnerReportInvestmentPersistence
    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var interactor: GetReportCertificateInvestmentCalculatorService

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportCertificateInvestmentPersistence)
        clearMocks(reportCertificatePersistence)
        clearMocks(reportInvestmentPersistence)
        clearMocks(reportPersistence)
    }

    @ParameterizedTest(name = "get open (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft"])
    fun getOpen(status: ProjectReportStatus) {
        val reportId = 1L
        every { reportPersistence.getReportById(projectId = PROJECT_ID, reportId) } returns report(status)

        every { reportCertificateInvestmentPersistence.getInvestments(projectId = PROJECT_ID, reportId) } returns
            listOf(
                investment_1,
                investment_2
            )
        every { reportCertificatePersistence.listCertificatesOfProjectReport(reportId) } returns certificate()
        every { reportInvestmentPersistence.getInvestmentsCumulativeAfterControl(setOf(1L)) } returns
        mapOf(Pair(1, BigDecimal.valueOf(39, 1)), Pair(2, BigDecimal.valueOf(11, 1)))

        assertThat(interactor.getSubmittedOrCalculateCurrent(projectId = PROJECT_ID, reportId = reportId)).isEqualTo(expectedDraftResult)
    }
}
