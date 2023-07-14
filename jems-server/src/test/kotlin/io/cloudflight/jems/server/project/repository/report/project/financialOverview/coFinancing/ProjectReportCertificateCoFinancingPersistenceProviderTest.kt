package io.cloudflight.jems.server.project.repository.report.project.financialOverview.coFinancing

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCoFinancingEntity
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCumulativeFund
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingPrevious
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

            partnerContributionCurrentVerified = BigDecimal.valueOf(105),
            publicContributionCurrentVerified = BigDecimal.valueOf(34),
            automaticPublicContributionCurrentVerified = BigDecimal.valueOf(35),
            privateContributionCurrentVerified = BigDecimal.valueOf(36),
            sumCurrentVerified = BigDecimal.valueOf(125),

            partnerContributionPreviouslyVerified = BigDecimal.valueOf(420),
            publicContributionPreviouslyVerified = BigDecimal.valueOf(130),
            automaticPublicContributionPreviouslyVerified = BigDecimal.valueOf(140),
            privateContributionPreviouslyVerified = BigDecimal.valueOf(150),
            sumPreviouslyVerified = BigDecimal.valueOf(600),
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
            currentVerified = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(17L), null to BigDecimal.valueOf(47L)),
                partnerContribution = BigDecimal.valueOf(105),
                publicContribution = BigDecimal.valueOf(34),
                automaticPublicContribution = BigDecimal.valueOf(35),
                privateContribution = BigDecimal.valueOf(36),
                sum = BigDecimal.valueOf(125),
            ),
            previouslyVerified = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(33L), null to BigDecimal.valueOf(79L)),
                partnerContribution = BigDecimal.valueOf(420),
                publicContribution = BigDecimal.valueOf(130),
                automaticPublicContribution = BigDecimal.valueOf(140),
                privateContribution = BigDecimal.valueOf(150),
                sum = BigDecimal.valueOf(600),
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
            total = BigDecimal.valueOf(250L),
            current = BigDecimal.valueOf(125L),
            previouslyReported = BigDecimal.valueOf(50L),
            currentVerified = BigDecimal.valueOf(17L),
            previouslyVerified = BigDecimal.valueOf(33L),
            previouslyPaid = BigDecimal.valueOf(81L),
        )

        private fun partnerContribution() = ProjectReportCoFinancingEntity(
            id = ProjectReportCoFinancingIdEntity(report, 2),
            programmeFund = null,
            total = BigDecimal.valueOf(750L),
            current = BigDecimal.valueOf(375L),
            previouslyReported = BigDecimal.valueOf(150L),
            currentVerified = BigDecimal.valueOf(47L),
            previouslyVerified = BigDecimal.valueOf(79L),
            previouslyPaid = BigDecimal.valueOf(123L),
        )

        private val cumulativeCurrent = ReportCertificateCoFinancingColumnWithoutFunds(
            partnerContribution = BigDecimal.valueOf(24),
            publicContribution = BigDecimal.valueOf(25),
            automaticPublicContribution = BigDecimal.valueOf(26),
            privateContribution = BigDecimal.valueOf(27),
            sum = BigDecimal.valueOf(28),
        )
        private val cumulativeVerified = ReportCertificateCoFinancingColumnWithoutFunds(
            partnerContribution = BigDecimal.valueOf(34),
            publicContribution = BigDecimal.valueOf(35),
            automaticPublicContribution = BigDecimal.valueOf(36),
            privateContribution = BigDecimal.valueOf(37),
            sum = BigDecimal.valueOf(38),
        )

        private val cumulativeCurrentFund = ProjectReportCumulativeFund(
            reportFundId = fund().programmeFund!!.id,
            sum = BigDecimal.valueOf(62),
        )
        private val cumulativeVerifiedFund = ProjectReportCumulativeFund(
            reportFundId = fund().programmeFund!!.id,
            sum = BigDecimal.valueOf(63),
        )

        private val cumulativeCurrentPartnerContrib = ProjectReportCumulativeFund(
            reportFundId = null,
            sum = BigDecimal.valueOf(184),
        )
        private val cumulativeVerifiedPartnerContrib = ProjectReportCumulativeFund(
            reportFundId = null,
            sum = BigDecimal.valueOf(185),
        )

        private val coFinCumulative = ReportCertificateCoFinancingPrevious(
            previouslyReported = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(62L), null to BigDecimal.valueOf(184L)),
                partnerContribution = BigDecimal.valueOf(24),
                publicContribution = BigDecimal.valueOf(25),
                automaticPublicContribution = BigDecimal.valueOf(26),
                privateContribution = BigDecimal.valueOf(27),
                sum = BigDecimal.valueOf(28),
            ),
            previouslyVerified = ReportCertificateCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(63L), null to BigDecimal.valueOf(185L)),
                partnerContribution = BigDecimal.valueOf(34),
                publicContribution = BigDecimal.valueOf(35),
                automaticPublicContribution = BigDecimal.valueOf(36),
                privateContribution = BigDecimal.valueOf(37),
                sum = BigDecimal.valueOf(38),
            ),
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
        every { certificateCoFinancingRepository.findCumulativeCurrentForReportIds(reportIds = setOf(9L)) } returns cumulativeCurrent
        every { projectReportCoFinancingRepository.findCumulativeCurrentForReportIds(reportIds = setOf(9L)) } returns
            listOf(cumulativeCurrentFund, cumulativeCurrentPartnerContrib)

        every { certificateCoFinancingRepository.findCumulativeVerifiedForReportIds(reportIds = setOf(10L)) } returns cumulativeVerified
        every { projectReportCoFinancingRepository.findCumulativeVerifiedForReportIds(reportIds = setOf(10L)) } returns
            listOf(cumulativeVerifiedFund, cumulativeVerifiedPartnerContrib)

        assertThat(persistence.getCoFinancingCumulative(submittedReportIds = setOf(9L), finalizedReportIds = setOf(10L)))
            .isEqualTo(coFinCumulative)
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
