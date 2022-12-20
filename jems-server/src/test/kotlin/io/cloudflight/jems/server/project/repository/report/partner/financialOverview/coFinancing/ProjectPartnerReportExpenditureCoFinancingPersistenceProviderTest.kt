package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportCoFinancingRepository
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancing
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectPartnerReportExpenditureCoFinancingPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 363L

        private val report = mockk<ProjectPartnerReportEntity>().also {
            every { it.partnerId } returns PARTNER_ID
        }

        private fun coFinEntity() = ReportProjectPartnerExpenditureCoFinancingEntity(
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

            partnerContributionTotalEligibleAfterControl = BigDecimal.valueOf(51),
            publicContributionTotalEligibleAfterControl = BigDecimal.valueOf(101),
            automaticPublicContributionTotalEligibleAfterControl = BigDecimal.valueOf(151),
            privateContributionTotalEligibleAfterControl = BigDecimal.valueOf(201),
            sumTotalEligibleAfterControl = BigDecimal.valueOf(251),

            partnerContributionPreviouslyReported = BigDecimal.valueOf(2),
            publicContributionPreviouslyReported = BigDecimal.valueOf(3),
            automaticPublicContributionPreviouslyReported = BigDecimal.valueOf(4),
            privateContributionPreviouslyReported = BigDecimal.valueOf(5),
            sumPreviouslyReported = BigDecimal.valueOf(6),
        )

        private val coFin = ReportExpenditureCoFinancing(
            totalsFromAF = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(250L), null to BigDecimal.valueOf(750L)),
                partnerContribution = BigDecimal.valueOf(900),
                publicContribution = BigDecimal.valueOf(200),
                automaticPublicContribution = BigDecimal.valueOf(300),
                privateContribution = BigDecimal.valueOf(400),
                sum = BigDecimal.valueOf(1000),
            ),
            currentlyReported = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(125L), null to BigDecimal.valueOf(375L)),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(100),
                automaticPublicContribution = BigDecimal.valueOf(150),
                privateContribution = BigDecimal.valueOf(200),
                sum = BigDecimal.valueOf(250),
            ),
            totalEligibleAfterControl = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(126L), null to BigDecimal.valueOf(376L)),
                partnerContribution = BigDecimal.valueOf(51),
                publicContribution = BigDecimal.valueOf(101),
                automaticPublicContribution = BigDecimal.valueOf(151),
                privateContribution = BigDecimal.valueOf(201),
                sum = BigDecimal.valueOf(251),
            ),
            previouslyReported = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(50L), null to BigDecimal.valueOf(150L)),
                partnerContribution = BigDecimal.valueOf(2),
                publicContribution = BigDecimal.valueOf(3),
                automaticPublicContribution = BigDecimal.valueOf(4),
                privateContribution = BigDecimal.valueOf(5),
                sum = BigDecimal.valueOf(6),
            ),
            previouslyPaid = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(81L), null to BigDecimal.valueOf(123L)),
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.valueOf(204),
            ),
        )

        private fun fund() = ProjectPartnerReportCoFinancingEntity(
            id = ProjectPartnerReportCoFinancingIdEntity(report, 1),
            programmeFund = ProgrammeFundEntity(id = 20L, selected = true, type = ProgrammeFundType.ERDF),
            percentage = BigDecimal.valueOf(25L),
            total = BigDecimal.valueOf(250L),
            current = BigDecimal.valueOf(125L),
            totalEligibleAfterControl = BigDecimal.valueOf(126L),
            previouslyReported = BigDecimal.valueOf(50L),
            previouslyPaid = BigDecimal.valueOf(81L),
        )

        private fun partnerContribution() = ProjectPartnerReportCoFinancingEntity(
            id = ProjectPartnerReportCoFinancingIdEntity(report, 2),
            programmeFund = null,
            percentage = BigDecimal.valueOf(75L),
            total = BigDecimal.valueOf(750L),
            current = BigDecimal.valueOf(375L),
            totalEligibleAfterControl = BigDecimal.valueOf(376L),
            previouslyReported = BigDecimal.valueOf(150L),
            previouslyPaid = BigDecimal.valueOf(123L),
        )

        private val reportsCumulative = ReportExpenditureCoFinancingColumnWithoutFunds(
            partnerContribution = BigDecimal.valueOf(24),
            publicContribution = BigDecimal.valueOf(25),
            automaticPublicContribution = BigDecimal.valueOf(26),
            privateContribution = BigDecimal.valueOf(27),
            sum = BigDecimal.valueOf(28),
        )

        private val cumulativeFund = ReportCumulativeFund(
            reportFundId = fund().programmeFund!!.id,
            sum = BigDecimal.valueOf(62),
        )

        private val cumulativePartnerContrib = ReportCumulativeFund(
            reportFundId = null,
            sum = BigDecimal.valueOf(184),
        )

        private val coFinCumulative = ReportExpenditureCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(62L), null to BigDecimal.valueOf(184L)),
            partnerContribution = BigDecimal.valueOf(24),
            publicContribution = BigDecimal.valueOf(25),
            automaticPublicContribution = BigDecimal.valueOf(26),
            privateContribution = BigDecimal.valueOf(27),
            sum = BigDecimal.valueOf(28),
        )

        private val coFinNewValues = ReportExpenditureCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(12L), null to BigDecimal.valueOf(18L)),
            partnerContribution = BigDecimal.valueOf(50),
            publicContribution = BigDecimal.valueOf(40),
            automaticPublicContribution = BigDecimal.valueOf(30),
            privateContribution = BigDecimal.valueOf(60),
            sum = BigDecimal.valueOf(100),
        )

        private val coFinAfterControl = ReportExpenditureCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(22L), null to BigDecimal.valueOf(28L)),
            partnerContribution = BigDecimal.valueOf(50),
            publicContribution = BigDecimal.valueOf(10),
            automaticPublicContribution = BigDecimal.valueOf(15),
            privateContribution = BigDecimal.valueOf(25),
            sum = BigDecimal.valueOf(100),
        )
    }

    @MockK
    private lateinit var expenditureCoFinancingRepository: ReportProjectPartnerExpenditureCoFinancingRepository

    @MockK
    private lateinit var partnerReportCoFinancingRepository: ProjectPartnerReportCoFinancingRepository

    @InjectMockKs
    private lateinit var persistence: ProjectPartnerReportExpenditureCoFinancingPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(expenditureCoFinancingRepository)
        clearMocks(partnerReportCoFinancingRepository)
    }

    @Test
    fun getCoFinancing() {
        every { expenditureCoFinancingRepository.findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 8L) } returns coFinEntity()
        every { partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId = 8L) } returns
            listOf(fund(), partnerContribution())
        assertThat(persistence.getCoFinancing(PARTNER_ID, reportId = 8L)).isEqualTo(coFin)
    }

    @Test
    fun getCoFinancingCumulative() {
        every { expenditureCoFinancingRepository.findCumulativeForReportIds(reportIds = setOf(9L)) } returns reportsCumulative
        every { partnerReportCoFinancingRepository.findCumulativeForReportIds(reportIds = setOf(9L)) } returns
            listOf(cumulativeFund, cumulativePartnerContrib)
        assertThat(persistence.getCoFinancingCumulative(reportIds = setOf(9L))).isEqualTo(coFinCumulative)
    }

    @Test
    fun updateCurrentlyReportedValues() {
        val fund = fund()
        val partnerContrib = partnerContribution()
        val coFinEntity = coFinEntity()

        every { partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId = 15L) } returns
            listOf(fund, partnerContrib)
        every { expenditureCoFinancingRepository.findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 15L) } returns coFinEntity

        persistence.updateCurrentlyReportedValues(PARTNER_ID, reportId = 15L, currentlyReported = coFinNewValues)

        assertThat(fund.current).isEqualByComparingTo(BigDecimal.valueOf(12L))
        assertThat(partnerContrib.current).isEqualByComparingTo(BigDecimal.valueOf(18L))
        assertThat(coFinEntity.partnerContributionCurrent).isEqualByComparingTo(BigDecimal.valueOf(50L))
        assertThat(coFinEntity.publicContributionCurrent).isEqualByComparingTo(BigDecimal.valueOf(40L))
        assertThat(coFinEntity.automaticPublicContributionCurrent).isEqualByComparingTo(BigDecimal.valueOf(30L))
        assertThat(coFinEntity.privateContributionCurrent).isEqualByComparingTo(BigDecimal.valueOf(60L))
        assertThat(coFinEntity.sumCurrent).isEqualByComparingTo(BigDecimal.valueOf(100L))
    }

    @Test
    fun updateAfterControlValues() {
        val fund = fund()
        val partnerContrib = partnerContribution()
        val coFinEntity = coFinEntity()

        every { partnerReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(reportId = 15L) } returns
            listOf(fund, partnerContrib)
        every { expenditureCoFinancingRepository.findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 15L) } returns coFinEntity

        persistence.updateAfterControlValues(PARTNER_ID, reportId = 15L, afterControl = coFinAfterControl)

        assertThat(fund.totalEligibleAfterControl).isEqualByComparingTo(BigDecimal.valueOf(22L))
        assertThat(partnerContrib.totalEligibleAfterControl).isEqualByComparingTo(BigDecimal.valueOf(28L))
        assertThat(coFinEntity.partnerContributionTotalEligibleAfterControl).isEqualByComparingTo(BigDecimal.valueOf(50L))
        assertThat(coFinEntity.publicContributionTotalEligibleAfterControl).isEqualByComparingTo(BigDecimal.valueOf(10L))
        assertThat(coFinEntity.automaticPublicContributionTotalEligibleAfterControl).isEqualByComparingTo(BigDecimal.valueOf(15L))
        assertThat(coFinEntity.privateContributionTotalEligibleAfterControl).isEqualByComparingTo(BigDecimal.valueOf(25L))
        assertThat(coFinEntity.sumTotalEligibleAfterControl).isEqualByComparingTo(BigDecimal.valueOf(100L))
    }

    @Test
    fun getReportCurrentSum() {
        val coFin = mockk<ReportProjectPartnerExpenditureCoFinancingEntity>()
        every { coFin.sumCurrent } returns BigDecimal.valueOf(64789, 2)

        every { expenditureCoFinancingRepository
            .findFirstByReportEntityPartnerIdAndReportEntityId(PARTNER_ID, reportId = 15L)
        } returns coFin
        assertThat(persistence.getReportCurrentSum(PARTNER_ID, reportId = 15L)).isEqualTo(BigDecimal.valueOf(64789, 2))
    }

}
