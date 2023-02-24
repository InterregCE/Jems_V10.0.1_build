package io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCoFinancingEntity
import io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing.ReportCumulativeFund
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCertificateCoFinancingPersistenceProvider
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCumulativeFund
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ReportCertificateCoFinancingColumnWithoutFunds
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ReportProjectCertificateCoFinancingRepository
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportCertificateCoFinancingPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 363L

        private val report = mockk<ProjectReportEntity>().also {
            every { it.projectId } returns PROJECT_ID
        }

        private fun coFinEntity() = ReportProjectCertificateCoFinancingEntity(
            reportId = 8L,
            reportEntity = report,

            partnerContributionTotal = BigDecimal.valueOf(900),
            publicContributionTotal = BigDecimal.valueOf(200),
            automaticPublicContributionTotal = BigDecimal.valueOf(300),
            privateContributionTotal = BigDecimal.valueOf(400),
            sumTotal = BigDecimal.valueOf(1000),

            partnerContributionCurrent = BigDecimal.valueOf(50),
            publicContributionCurrent = BigDecimal.valueOf(100),
            automaticPublicContributionCurrent = BigDecimal.valueOf(150),
            privateContributionCurrent = BigDecimal.valueOf(200),
            sumCurrent = BigDecimal.valueOf(250),

            partnerContributionPreviouslyReported = BigDecimal.valueOf(2),
            publicContributionPreviouslyReported = BigDecimal.valueOf(3),
            automaticPublicContributionPreviouslyReported = BigDecimal.valueOf(4),
            privateContributionPreviouslyReported = BigDecimal.valueOf(5),
            sumPreviouslyReported = BigDecimal.valueOf(6),
        )

        private val coFin = ReportCertificateCoFinancing(
            totalsFromAF = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(250L), null to BigDecimal.valueOf(750L)),
                partnerContribution = BigDecimal.valueOf(900),
                publicContribution = BigDecimal.valueOf(200),
                automaticPublicContribution = BigDecimal.valueOf(300),
                privateContribution = BigDecimal.valueOf(400),
                sum = BigDecimal.valueOf(1000),
            ),
            currentlyReported = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(125L), null to BigDecimal.valueOf(375L)),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(100),
                automaticPublicContribution = BigDecimal.valueOf(150),
                privateContribution = BigDecimal.valueOf(200),
                sum = BigDecimal.valueOf(250),
            ),
            previouslyReported = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(50L), null to BigDecimal.valueOf(150L)),
                partnerContribution = BigDecimal.valueOf(2),
                publicContribution = BigDecimal.valueOf(3),
                automaticPublicContribution = BigDecimal.valueOf(4),
                privateContribution = BigDecimal.valueOf(5),
                sum = BigDecimal.valueOf(6),
            ),
            previouslyPaid = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(81L), null to BigDecimal.valueOf(123L)),
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(204),
            ),
        )

        private fun fund() = ProjectReportCoFinancingEntity(
            id = ProjectReportCoFinancingIdEntity(report, 1),
            programmeFund = ProgrammeFundEntity(id = 20L, selected = true, type = ProgrammeFundType.ERDF),
            percentage = BigDecimal.valueOf(25L),
            total = BigDecimal.valueOf(250L),
            current = BigDecimal.valueOf(125L),
            previouslyReported = BigDecimal.valueOf(50L),
            previouslyPaid = BigDecimal.valueOf(81L),
        )

        private fun partnerContribution() = ProjectReportCoFinancingEntity(
            id = ProjectReportCoFinancingIdEntity(report, 2),
            programmeFund = null,
            percentage = BigDecimal.valueOf(75L),
            total = BigDecimal.valueOf(750L),
            current = BigDecimal.valueOf(375L),
            previouslyReported = BigDecimal.valueOf(150L),
            previouslyPaid = BigDecimal.valueOf(123L),
        )

        private val reportsCumulative = ReportCertificateCoFinancingColumnWithoutFunds(
            partnerContribution = BigDecimal.valueOf(24),
            publicContribution = BigDecimal.valueOf(25),
            automaticPublicContribution = BigDecimal.valueOf(26),
            privateContribution = BigDecimal.valueOf(27),
            sum = BigDecimal.valueOf(28),
        )

        private val cumulativeFund = ProjectReportCumulativeFund(
            reportFundId = fund().programmeFund!!.id,
            sum = BigDecimal.valueOf(62)
        )

        private val cumulativePartnerContrib = ProjectReportCumulativeFund(
            reportFundId = null,
            sum = BigDecimal.valueOf(184)
        )

        private val coFinCumulative = ReportCertificateCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(62L), null to BigDecimal.valueOf(184L)),
            partnerContribution = BigDecimal.valueOf(24),
            publicContribution = BigDecimal.valueOf(25),
            automaticPublicContribution = BigDecimal.valueOf(26),
            privateContribution = BigDecimal.valueOf(27),
            sum = BigDecimal.valueOf(28),
        )

        private val coFinNewValues = ReportCertificateCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(12L), null to BigDecimal.valueOf(18L)),
            partnerContribution = BigDecimal.valueOf(50),
            publicContribution = BigDecimal.valueOf(40),
            automaticPublicContribution = BigDecimal.valueOf(30),
            privateContribution = BigDecimal.valueOf(60),
            sum = BigDecimal.valueOf(100),
        )
    }

    @MockK
    private lateinit var certificateCoFinancingRepository: ReportProjectCertificateCoFinancingRepository

    @MockK
    private lateinit var projectReportCoFinancingRepository: ProjectReportCoFinancingRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportCertificateCoFinancingPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(certificateCoFinancingRepository)
        clearMocks(projectReportCoFinancingRepository)
    }

    @Test
    fun getCoFinancing() {
        every { certificateCoFinancingRepository.findFirstByReportEntityProjectIdAndReportEntityId(PROJECT_ID, reportId = 8L) } returns coFinEntity()
        every { projectReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId = 8L) } returns
            listOf(fund(), partnerContribution())
        assertThat(persistence.getCoFinancing(PROJECT_ID, reportId = 8L)).isEqualTo(coFin)
    }

    @Test
    fun getCoFinancingCumulative() {
        every { certificateCoFinancingRepository.findCumulativeForReportIds(reportIds = setOf(9L)) } returns reportsCumulative
        every { projectReportCoFinancingRepository.findCumulativeForReportIds(reportIds = setOf(9L)) } returns
            listOf(cumulativeFund, cumulativePartnerContrib)
        assertThat(persistence.getCoFinancingCumulative(reportIds = setOf(9L))).isEqualTo(coFinCumulative)
    }

    @Test
    fun updateCurrentlyReportedValues() {
        val fund = fund()
        val partnerContrib = partnerContribution()
        val coFinEntity = coFinEntity()

        every { projectReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId = 15L) } returns
            listOf(fund, partnerContrib)
        every { certificateCoFinancingRepository.findFirstByReportEntityProjectIdAndReportEntityId(PROJECT_ID, reportId = 15L) } returns coFinEntity

        persistence.updateCurrentlyReportedValues(PROJECT_ID, reportId = 15L, currentlyReported = coFinNewValues)

        assertThat(fund.current).isEqualByComparingTo(BigDecimal.valueOf(12L))
        assertThat(partnerContrib.current).isEqualByComparingTo(BigDecimal.valueOf(18L))
        assertThat(coFinEntity.partnerContributionCurrent).isEqualByComparingTo(BigDecimal.valueOf(50L))
        assertThat(coFinEntity.publicContributionCurrent).isEqualByComparingTo(BigDecimal.valueOf(40L))
        assertThat(coFinEntity.automaticPublicContributionCurrent).isEqualByComparingTo(BigDecimal.valueOf(30L))
        assertThat(coFinEntity.privateContributionCurrent).isEqualByComparingTo(BigDecimal.valueOf(60L))
        assertThat(coFinEntity.sumCurrent).isEqualByComparingTo(BigDecimal.valueOf(100L))
    }
}
