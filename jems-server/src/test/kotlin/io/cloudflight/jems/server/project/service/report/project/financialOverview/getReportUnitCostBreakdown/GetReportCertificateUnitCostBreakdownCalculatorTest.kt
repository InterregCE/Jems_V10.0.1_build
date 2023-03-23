package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSubmissionSummary
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.base.ProjectReportModel
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportUnitCostPersistence
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.project.certificate.ProjectReportCertificatePersistence
import io.cloudflight.jems.server.project.service.report.project.financialOverview.ProjectReportCertificateUnitCostPersistence
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

internal class GetReportCertificateUnitCostBreakdownCalculatorTest : UnitTest() {

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

        private val unitCost_1 = CertificateUnitCostBreakdownLine(
            reportUnitCostId = 1L,
            unitCostId = 101L,
            name = setOf(InputTranslation(SystemLanguage.GA, "name 1 GA")),
            totalEligibleBudget = BigDecimal.valueOf(52),
            previouslyReported = BigDecimal.valueOf(23),
            currentReport = BigDecimal.valueOf(39, 1),
        )

        private val unitCost_2 = CertificateUnitCostBreakdownLine(
            reportUnitCostId = 2L,
            unitCostId = 102L,
            name = setOf(InputTranslation(SystemLanguage.MK, "name 2 MK")),
            totalEligibleBudget = BigDecimal.valueOf(18),
            previouslyReported = BigDecimal.valueOf(7),
            currentReport = BigDecimal.valueOf(11, 1)
        )

        private val expectedDraftBreakdown = CertificateUnitCostBreakdown(
            unitCosts = listOf(
                CertificateUnitCostBreakdownLine(
                    reportUnitCostId = 1L,
                    unitCostId = 101L,
                    name = setOf(InputTranslation(SystemLanguage.GA, "name 1 GA")),
                    totalEligibleBudget = BigDecimal.valueOf(52),
                    previouslyReported = BigDecimal.valueOf(23),
                    currentReport = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(23),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(4423, 2),
                    remainingBudget = BigDecimal.valueOf(29),
                ),
                CertificateUnitCostBreakdownLine(
                    reportUnitCostId = 2L,
                    unitCostId = 102L,
                    name = setOf(InputTranslation(SystemLanguage.MK, "name 2 MK")),
                    totalEligibleBudget = BigDecimal.valueOf(18),
                    previouslyReported = BigDecimal.valueOf(7),
                    currentReport = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.valueOf(7),
                    totalReportedSoFarPercentage = BigDecimal.valueOf(3889, 2),
                    remainingBudget = BigDecimal.valueOf(11),
                ),
            ),
            total = CertificateUnitCostBreakdownLine(
                reportUnitCostId = 0L,
                unitCostId = 0L,
                name = emptySet(),
                totalEligibleBudget = BigDecimal.valueOf(70),
                previouslyReported = BigDecimal.valueOf(30),
                currentReport = BigDecimal.ZERO,
                totalReportedSoFar = BigDecimal.valueOf(30),
                totalReportedSoFarPercentage = BigDecimal.valueOf(4286, 2),
                remainingBudget = BigDecimal.valueOf(40),
            ),
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence
    @MockK
    lateinit var reportCertificateUnitCostPersistence: ProjectReportCertificateUnitCostPersistence
    @MockK
    lateinit var reportCertificatePersistence: ProjectReportCertificatePersistence
    @MockK
    lateinit var reportUnitCostPersistence: ProjectPartnerReportUnitCostPersistence

    @InjectMockKs
    lateinit var calculator: GetReportCertificateUnitCostCalculatorService

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportPersistence)
        clearMocks(reportUnitCostPersistence)
        clearMocks(reportCertificateUnitCostPersistence)
        clearMocks(reportCertificatePersistence)
    }

    @ParameterizedTest(name = "get open (status {0})")
    @EnumSource(value = ProjectReportStatus::class, names = ["Draft"])
    fun getOpen(status: ProjectReportStatus) {
        val reportId = 97658L
        every { reportPersistence.getReportById(projectId = PROJECT_ID, reportId) } returns report(status)
        every { reportCertificateUnitCostPersistence.getUnitCosts(projectId = PROJECT_ID, reportId = reportId) } returns
            listOf(
                unitCost_1,
                unitCost_2
            )
        every { reportCertificatePersistence.listCertificatesOfProjectReport(reportId) } returns certificate()
        every {
            reportUnitCostPersistence.getUnitCostCumulativeAfterControl(
                setOf(1L)
            )
        } returns
            mapOf(Pair(1, BigDecimal.valueOf(39, 1)), Pair(2, BigDecimal.valueOf(11, 1)))

        assertThat(calculator.getSubmittedOrCalculateCurrent(PROJECT_ID, reportId = reportId)).isEqualTo(expectedDraftBreakdown)
    }

}
