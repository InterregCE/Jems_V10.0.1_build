package io.cloudflight.jems.server.project.service.report.project.financialOverview

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportLumpSumPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown.GetReportCertificateLumpSumCalculatorService
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

internal class GetReportCertificateLumpSumBreakdownCalculatorTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 597L

        private fun report(status: ProjectReportStatus): ProjectReportModel {
            val report = mockk<ProjectReportModel>()
            every { report.status } returns status
            return report
        }

        private fun certificate(): List<ProjectPartnerReportSubmissionSummary> {
            val certificate = mockk<ProjectPartnerReportSubmissionSummary>()
            every { certificate.id } returns 1L
            return listOf(certificate)
        }


        private val lumpSum_1 = CertificateLumpSumBreakdownLine(
            reportLumpSumId = 1L,
            lumpSumId = 101L,
            name = setOf(InputTranslation(SystemLanguage.ES, "name 1 ES")),
            period = 11,
            orderNr = 1,
            totalEligibleBudget = BigDecimal.valueOf(52),
            previouslyReported = BigDecimal.valueOf(23),
            previouslyPaid = BigDecimal.ZERO,
            currentReport = BigDecimal.valueOf(39, 1),
        )

        private val lumpSum_2 = CertificateLumpSumBreakdownLine(
            reportLumpSumId = 2L,
            lumpSumId = 102L,
            name = setOf(InputTranslation(SystemLanguage.TR, "name 2 TR")),
            period = 12,
            orderNr = 2,
            totalEligibleBudget = BigDecimal.valueOf(18),
            previouslyReported = BigDecimal.valueOf(7),
            previouslyPaid = BigDecimal.ZERO,
            currentReport = BigDecimal.valueOf(11, 1),
        )

        private val lumpSum_ft = CertificateLumpSumBreakdownLine(
            reportLumpSumId = 3L,
            lumpSumId = 103L,
            name = setOf(InputTranslation(SystemLanguage.TR, "name 3 TR")),
            period = 13,
            orderNr = 3,
            totalEligibleBudget = BigDecimal.valueOf(15),
            previouslyReported = BigDecimal.ZERO,
            previouslyPaid = BigDecimal.valueOf(12),
            currentReport = BigDecimal.ZERO,
        )

        private val expectedDraftBreakdown = CertificateLumpSumBreakdown(
            lumpSums = listOf(
                CertificateLumpSumBreakdownLine(
                    reportLumpSumId = 1L,
                    lumpSumId = 101L,
                    name = setOf(InputTranslation(SystemLanguage.ES, "name 1 ES")),
                    period = 11,
                    orderNr = 1,
                    totalEligibleBudget = BigDecimal.valueOf(52),
                    previouslyReported = BigDecimal.valueOf(23),
                    previouslyPaid = BigDecimal.ZERO,
                    currentReport = BigDecimal.valueOf(39, 1),
                    totalReportedSoFar = BigDecimal.valueOf(269, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(5173, 2),
                    remainingBudget = BigDecimal.valueOf(251, 1),
                ),
                CertificateLumpSumBreakdownLine(
                    reportLumpSumId = 2L,
                    lumpSumId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.TR, "name 2 TR")),
                    period = 12,
                    orderNr = 2,
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    previouslyPaid = BigDecimal.ZERO,
                    currentReport = BigDecimal.valueOf(11, 1),
                    totalReportedSoFar = BigDecimal.valueOf(81, 1),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4500, 2),
                    remainingBudget = BigDecimal.valueOf(99, 1),
                ),
                CertificateLumpSumBreakdownLine(
                    reportLumpSumId = 3L,
                    lumpSumId = 103L,
                    name = setOf(InputTranslation(SystemLanguage.TR, "name 3 TR")),
                    period = 13,
                    orderNr = 3,
                    totalEligibleBudget = BigDecimal.valueOf(15),
                    previouslyReported = BigDecimal.ZERO,
                    previouslyPaid = BigDecimal.valueOf(12),
                    currentReport = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.ZERO,
                    totalReportedSoFarPercentage = BigDecimal.valueOf(0, 2),
                    remainingBudget = BigDecimal.valueOf(15),
                ),
            ),
            total = CertificateLumpSumBreakdownLine(
                reportLumpSumId = 0L,
                lumpSumId = 0L,
                name = emptySet(),
                period = null,
                orderNr = 0,
                totalEligibleBudget = BigDecimal.valueOf(85),
                previouslyReported = BigDecimal.valueOf(30),
                previouslyPaid = BigDecimal.valueOf(12),
                currentReport = BigDecimal.valueOf(50, 1),
                totalReportedSoFar = BigDecimal.valueOf(350, 1),
                totalReportedSoFarPercentage = BigDecimal.valueOf(4118, 2),
                remainingBudget = BigDecimal.valueOf(500, 1),
            ),
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportCertificateLumpSumPersistence: ProjectReportCertificateLumpSumPersistence
    @MockK
    lateinit var reportCertificatePersistence: ProjectReportCertificatePersistence
    @MockK
    lateinit var reportExpenditureLumpSumPersistence: ProjectPartnerReportLumpSumPersistence

    @InjectMockKs
    lateinit var calculator: GetReportCertificateLumpSumCalculatorService

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportPersistence)
        clearMocks(reportCertificateLumpSumPersistence)
        clearMocks(reportCertificatePersistence)
        clearMocks(reportExpenditureLumpSumPersistence)
    }

    @ParameterizedTest(name = "get open (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft"])
    fun getOpen(status: ProjectReportStatus) {
        val reportId = 97658L
        every { reportPersistence.getReportById(projectId = PROJECT_ID, reportId) } returns report(status)
        every { reportCertificateLumpSumPersistence.getLumpSums(projectId = PROJECT_ID, reportId = reportId) } returns
            listOf(
                lumpSum_1.copy(currentReport = BigDecimal.ZERO),
                lumpSum_2.copy(currentReport = BigDecimal.ZERO),
                lumpSum_ft
            )
        every { reportCertificatePersistence.listCertificatesOfProjectReport(reportId) } returns certificate()
        every {
            reportExpenditureLumpSumPersistence.getLumpSumCumulativeAfterControl(
                setOf(1L)
            )
        } returns
            mapOf(Pair(1, BigDecimal.valueOf(39, 1)), Pair(2, BigDecimal.valueOf(11, 1)), Pair(3, BigDecimal.ZERO))
        assertThat(calculator.getSubmittedOrCalculateCurrent(PROJECT_ID, reportId = reportId)).isEqualTo(expectedDraftBreakdown)
    }
}
