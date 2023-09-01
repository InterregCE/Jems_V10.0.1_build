package io.cloudflight.jems.server.project.repository.report.partner.financialOverview.coFinancing

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportCoFinancingIdEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.financialOverview.ReportProjectPartnerExpenditureCoFinancingEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.coFinancing.ProjectReportCumulativeFund
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingCurrentWithReIncluded
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingPrevious
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

            partnerContributionPreviouslyValidated = BigDecimal.valueOf(12),
            publicContributionPreviouslyValidated = BigDecimal.valueOf(13),
            automaticPublicContributionPreviouslyValidated = BigDecimal.valueOf(14),
            privateContributionPreviouslyValidated = BigDecimal.valueOf(15),
            sumPreviouslyValidated = BigDecimal.valueOf(16),

            partnerContributionCurrentParked = BigDecimal.valueOf(50),
            publicContributionCurrentParked = BigDecimal.valueOf(100),
            automaticPublicContributionCurrentParked = BigDecimal.valueOf(150),
            privateContributionCurrentParked = BigDecimal.valueOf(200),
            sumCurrentParked = BigDecimal.valueOf(400),

            partnerContributionCurrentReIncluded = BigDecimal.valueOf(50),
            publicContributionCurrentReIncluded = BigDecimal.valueOf(100),
            automaticPublicContributionCurrentReIncluded = BigDecimal.valueOf(150),
            privateContributionCurrentReIncluded = BigDecimal.valueOf(200),
            sumCurrentReIncluded = BigDecimal.valueOf(400),

            partnerContributionPreviouslyReportedParked = BigDecimal.valueOf(50),
            publicContributionPreviouslyReportedParked = BigDecimal.valueOf(100),
            automaticPublicContributionPreviouslyReportedParked = BigDecimal.valueOf(150),
            privateContributionPreviouslyReportedParked = BigDecimal.valueOf(200),
            sumPreviouslyReportedParked = BigDecimal.valueOf(400),
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
            currentlyReportedReIncluded = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(125L), null to BigDecimal.valueOf(375L)),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(100),
                automaticPublicContribution = BigDecimal.valueOf(150),
                privateContribution = BigDecimal.valueOf(200),
                sum = BigDecimal.valueOf(400),
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
            previouslyValidated = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(71L, 1), null to BigDecimal.valueOf(82L, 1)),
                partnerContribution = BigDecimal.valueOf(12),
                publicContribution = BigDecimal.valueOf(13),
                automaticPublicContribution = BigDecimal.valueOf(14),
                privateContribution = BigDecimal.valueOf(15),
                sum = BigDecimal.valueOf(16),
            ),
            previouslyReportedParked = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(50L), null to BigDecimal.valueOf(150L)),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(100),
                automaticPublicContribution = BigDecimal.valueOf(150),
                privateContribution = BigDecimal.valueOf(200),
                sum = BigDecimal.valueOf(400),
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
            previouslyValidated = BigDecimal.valueOf(71L, 1),
            previouslyPaid = BigDecimal.valueOf(81L),
            currentParked = BigDecimal.valueOf(50L),
            currentReIncluded = BigDecimal.valueOf(125L),
            previouslyReportedParked = BigDecimal.valueOf(50L),
            disabled = true,
        )

        private fun partnerContribution() = ProjectPartnerReportCoFinancingEntity(
            id = ProjectPartnerReportCoFinancingIdEntity(report, 2),
            programmeFund = null,
            percentage = BigDecimal.valueOf(75L),
            total = BigDecimal.valueOf(750L),
            current = BigDecimal.valueOf(375L),
            totalEligibleAfterControl = BigDecimal.valueOf(376L),
            previouslyReported = BigDecimal.valueOf(150L),
            previouslyValidated = BigDecimal.valueOf(82L, 1),
            previouslyPaid = BigDecimal.valueOf(123L),
            currentParked = BigDecimal.valueOf(150L),
            currentReIncluded = BigDecimal.valueOf(375L),
            previouslyReportedParked = BigDecimal.valueOf(150L),
            disabled = true,
        )

        private val reportsCumulative = ReportExpenditureCoFinancingColumnWithoutFunds(
            partnerContribution = BigDecimal.valueOf(24),
            publicContribution = BigDecimal.valueOf(25),
            automaticPublicContribution = BigDecimal.valueOf(26),
            privateContribution = BigDecimal.valueOf(27),
            sum = BigDecimal.valueOf(28),
        )
        private val reportsTotalCumulative = ReportExpenditureCoFinancingColumnWithoutFunds(
            partnerContribution = BigDecimal.valueOf(34),
            publicContribution = BigDecimal.valueOf(35),
            automaticPublicContribution = BigDecimal.valueOf(36),
            privateContribution = BigDecimal.valueOf(37),
            sum = BigDecimal.valueOf(38),
        )

        private val reportsParkedCumulative = ReportExpenditureCoFinancingColumnWithoutFunds(
            partnerContribution = BigDecimal.valueOf(11),
            publicContribution = BigDecimal.valueOf(12),
            automaticPublicContribution = BigDecimal.valueOf(13),
            privateContribution = BigDecimal.valueOf(14),
            sum = BigDecimal.valueOf(15),
        )

        private val cumulativeFund = ReportCumulativeFund(
            reportFundId = fund().programmeFund!!.id,
            currentSum = BigDecimal.valueOf(62),
            currentParkedSum = BigDecimal.valueOf(62)
        )

        private val cumulativePartnerContrib = ReportCumulativeFund(
            reportFundId = null,
            currentSum = BigDecimal.valueOf(184),
            currentParkedSum = BigDecimal.valueOf(184)
        )

        private val cumulativeTotalFund = ProjectReportCumulativeFund(
            reportFundId = fund().programmeFund!!.id,
            sum = BigDecimal.valueOf(625, 1),
        )

        private val cumulativeTotalPartnerContrib = ProjectReportCumulativeFund(
            reportFundId = null,
            sum = BigDecimal.valueOf(1845, 1),
        )

        private val coFinCumulative = ReportExpenditureCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(62L), null to BigDecimal.valueOf(-34L)),
            partnerContribution = BigDecimal.valueOf(-34),
            publicContribution = BigDecimal.valueOf(25),
            automaticPublicContribution = BigDecimal.valueOf(26),
            privateContribution = BigDecimal.valueOf(27),
            sum = BigDecimal.valueOf(28),
        )

        private val coFinParkedCumulative = ReportExpenditureCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(62L), null to BigDecimal.valueOf(-47L)),
            partnerContribution = BigDecimal.valueOf(-47),
            publicContribution = BigDecimal.valueOf(12),
            automaticPublicContribution = BigDecimal.valueOf(13),
            privateContribution = BigDecimal.valueOf(14),
            sum = BigDecimal.valueOf(15),
        )

        private val coFinValidatedCumulative = ReportExpenditureCoFinancingColumn(
            funds = mapOf(20L to BigDecimal.valueOf(625L, 1), null to BigDecimal.valueOf(-245L, 1)),
            partnerContribution = BigDecimal.valueOf(-245L, 1),
            publicContribution = BigDecimal.valueOf(35),
            automaticPublicContribution = BigDecimal.valueOf(36),
            privateContribution = BigDecimal.valueOf(37),
            sum = BigDecimal.valueOf(38),
        )

        private val coFinNewValues = ExpenditureCoFinancingCurrentWithReIncluded(
            current = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(12L), null to BigDecimal.valueOf(18L)),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(40),
                automaticPublicContribution = BigDecimal.valueOf(30),
                privateContribution = BigDecimal.valueOf(60),
                sum = BigDecimal.valueOf(100),
            ),
            currentReIncluded = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(12L), null to BigDecimal.valueOf(18L)),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(40),
                automaticPublicContribution = BigDecimal.valueOf(30),
                privateContribution = BigDecimal.valueOf(60),
                sum = BigDecimal.valueOf(100),
            )
        )

        private val coFinAfterControl = ExpenditureCoFinancingCurrent(
            current = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(22L), null to BigDecimal.valueOf(28L)),
                partnerContribution = BigDecimal.valueOf(50),
                publicContribution = BigDecimal.valueOf(10),
                automaticPublicContribution = BigDecimal.valueOf(15),
                privateContribution = BigDecimal.valueOf(25),
                sum = BigDecimal.valueOf(100),
            ),
            currentParked = ReportExpenditureCoFinancingColumn(
                funds = mapOf(20L to BigDecimal.valueOf(10L), null to BigDecimal.valueOf(20L)),
                partnerContribution = BigDecimal.valueOf(15),
                publicContribution = BigDecimal.valueOf(10),
                automaticPublicContribution = BigDecimal.valueOf(30),
                privateContribution = BigDecimal.valueOf(25),
                sum = BigDecimal.valueOf(80),
            )
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
        every { partnerReportCoFinancingRepository.findCumulativeForReportIds(reportIds = setOf(9L)) } returns
                listOf(cumulativeFund, cumulativePartnerContrib)
        every { partnerReportCoFinancingRepository.findCumulativeTotalsForReportIds(reportIds = setOf(14L)) } returns
                listOf(cumulativeTotalFund, cumulativeTotalPartnerContrib)

        every { expenditureCoFinancingRepository.findCumulativeForReportIds(reportIds = setOf(9L)) } returns reportsCumulative
        every { expenditureCoFinancingRepository.findCumulativeParkedForReportIds(reportIds = setOf(9L)) } returns reportsParkedCumulative
        every { expenditureCoFinancingRepository.findCumulativeTotalsForReportIds(reportIds = setOf(14L)) } returns reportsTotalCumulative

        assertThat(persistence.getCoFinancingCumulative(setOf(9L), setOf(14L)))
            .isEqualTo(ExpenditureCoFinancingPrevious(coFinCumulative, coFinParkedCumulative, coFinValidatedCumulative))
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

}
