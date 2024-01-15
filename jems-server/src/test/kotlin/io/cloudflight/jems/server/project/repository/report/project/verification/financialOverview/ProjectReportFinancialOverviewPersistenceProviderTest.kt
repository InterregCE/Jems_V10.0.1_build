package io.cloudflight.jems.server.project.repository.report.project.verification.financialOverview

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.repository.fund.toEntity
import io.cloudflight.jems.server.project.entity.report.partner.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportCoFinancingEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.verification.financialOverview.ProjectReportVerificationCertificateContributionOverviewEntity
import io.cloudflight.jems.server.project.entity.report.verification.financialOverview.ProjectReportVerificationCertificateSpfContributionOverviewEntity
import io.cloudflight.jems.server.project.repository.report.partner.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.repository.report.project.ProjectReportCoFinancingRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownSplitLine
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.cloudflight.jems.server.utils.IPA_III_FUND
import io.cloudflight.jems.server.utils.NDCI_FUND
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportFinancialOverviewPersistenceProviderTest: UnitTest() {

    companion object {
        private const val PROJECT_REPORT_ID = 6L
        private const val PARTNER_ID = 1L



        private fun projectPartnerReportEntityMock(): ProjectPartnerReportEntity {
            val projectReportEntity = mockk<ProjectReportEntity>()
            every { projectReportEntity.id } returns PROJECT_REPORT_ID

            val partnerIdentification =  mockk<PartnerReportIdentificationEntity>()
            every { partnerIdentification.partnerNumber } returns 1
            every { partnerIdentification.partnerRole } returns ProjectPartnerRole.LEAD_PARTNER

            val partnerReportEntity = mockk<ProjectPartnerReportEntity>()
            every { partnerReportEntity.id } returns 1L
            every { partnerReportEntity.projectReport } returns projectReportEntity
            every { partnerReportEntity.number } returns 1
            every { partnerReportEntity.partnerId } returns 1L
            every { partnerReportEntity.identification } returns partnerIdentification

            return partnerReportEntity
        }



        val overviews = listOf(
            ProjectReportVerificationCertificateContributionOverviewEntity(
                id = 1L,
                partnerReport = projectPartnerReportEntityMock(),
                programmeFund = null,
                fundValue =  null,
                partnerContribution = 18750.01.toScaledBigDecimal(),
                publicContribution = 11612.22.toScaledBigDecimal(),
                automaticPublicContribution = 5938.24.toScaledBigDecimal(),
                privateContribution = 1199.52.toScaledBigDecimal(),
                total = 124999.96.toScaledBigDecimal(),
            ),
            ProjectReportVerificationCertificateContributionOverviewEntity(
                id = 2L,
                partnerReport = projectPartnerReportEntityMock(),
                programmeFund = ERDF_FUND.toEntity(),
                fundValue =  74999.97.toScaledBigDecimal(),
                partnerContribution = 13235.30.toScaledBigDecimal(),
                publicContribution = 8196.86.toScaledBigDecimal(),
                automaticPublicContribution = 4191.69.toScaledBigDecimal(),
                privateContribution = 846.72.toScaledBigDecimal(),
                total = 88235.27.toScaledBigDecimal(),
            ),
            ProjectReportVerificationCertificateContributionOverviewEntity(
                id = 3L,
                partnerReport = projectPartnerReportEntityMock(),
                programmeFund = NDCI_FUND.toEntity(),
                fundValue =  24999.99.toScaledBigDecimal(),
                partnerContribution = 4411.77.toScaledBigDecimal(),
                publicContribution = 2732.28.toScaledBigDecimal(),
                automaticPublicContribution = 1397.23.toScaledBigDecimal(),
                privateContribution = 282.24.toScaledBigDecimal(),
                total = 29411.76.toScaledBigDecimal(),
            ),
            ProjectReportVerificationCertificateContributionOverviewEntity(
                id = 4L,
                partnerReport = projectPartnerReportEntityMock(),
                programmeFund = IPA_III_FUND.toEntity(),
                fundValue = 6249.99.toScaledBigDecimal(),
                partnerContribution = 1102.94.toScaledBigDecimal(),
                publicContribution = 683.07.toScaledBigDecimal(),
                automaticPublicContribution = 349.30.toScaledBigDecimal(),
                privateContribution = 70.56.toScaledBigDecimal(),
                total = 7352.93.toScaledBigDecimal(),
            ),
        )

        val spfOverviews = listOf(
            ProjectReportVerificationCertificateSpfContributionOverviewEntity(
                id = 1L,
                projectReport = mockk(),
                programmeFund = null,
                fundValue = BigDecimal.valueOf(-999L),
                partnerContribution = BigDecimal.valueOf(15L),
                publicContribution = BigDecimal.valueOf(4L),
                automaticPublicContribution = BigDecimal.valueOf(5L),
                privateContribution = BigDecimal.valueOf(6L),
                total = BigDecimal.valueOf(100L),
            ),
            ProjectReportVerificationCertificateSpfContributionOverviewEntity(
                id = 2L,
                projectReport = mockk(),
                programmeFund = IPA_III_FUND.toEntity(),
                fundValue = BigDecimal.valueOf(45L),
                partnerContribution = BigDecimal.valueOf(30L),
                publicContribution = BigDecimal.valueOf(9L),
                automaticPublicContribution = BigDecimal.valueOf(10L),
                privateContribution = BigDecimal.valueOf(11L),
                total = BigDecimal.valueOf(75L),
            ),
        )


        val expectedFinancialDataBreakDownLine = FinancingSourceBreakdownLine(
            partnerReportId = 1L,
            partnerReportNumber = 1,
            spfLine = false,
            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,
            fundsSorted = listOf(
                Pair(ERDF_FUND, 74999.97.toScaledBigDecimal()),
                Pair(NDCI_FUND, 24999.99.toScaledBigDecimal()),
                Pair(IPA_III_FUND, 6249.99.toScaledBigDecimal()),
            ),
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = listOf(
                FinancingSourceBreakdownSplitLine(
                    fundId = 1L,
                    value = 74999.97.toScaledBigDecimal(),
                    partnerContribution = 13235.30.toScaledBigDecimal(),
                    publicContribution = 8196.86.toScaledBigDecimal(),
                    automaticPublicContribution = 4191.69.toScaledBigDecimal(),
                    privateContribution = 846.72.toScaledBigDecimal(),
                    total = 88235.27.toScaledBigDecimal(),
                ),
                FinancingSourceBreakdownSplitLine(
                    fundId = 5L,
                    value = 24999.99.toScaledBigDecimal(),
                    partnerContribution = 4411.77.toScaledBigDecimal(),
                    publicContribution = 2732.28.toScaledBigDecimal(),
                    automaticPublicContribution = 1397.23.toScaledBigDecimal(),
                    privateContribution = 282.24.toScaledBigDecimal(),
                    total = 29411.76.toScaledBigDecimal(),
                ),
                FinancingSourceBreakdownSplitLine(
                    fundId = 4L,
                    value = 6249.99.toScaledBigDecimal(),
                    partnerContribution = 1102.94.toScaledBigDecimal(),
                    publicContribution = 683.07.toScaledBigDecimal(),
                    automaticPublicContribution = 349.30.toScaledBigDecimal(),
                    privateContribution = 70.56.toScaledBigDecimal(),
                    total = 7352.93.toScaledBigDecimal(),
                ),
            )
        )

        val expectedFinancialDataBreakDownSpfLine = FinancingSourceBreakdownLine(
            partnerReportId = null,
            partnerReportNumber = null,
            spfLine = true,
            partnerId = null,
            partnerRole = null,
            partnerNumber = null,
            fundsSorted = listOf(
                Pair(IPA_III_FUND, BigDecimal.valueOf(45L)),
            ),
            partnerContribution = BigDecimal.valueOf(15L),
            publicContribution = BigDecimal.valueOf(4L),
            automaticPublicContribution = BigDecimal.valueOf(5L),
            privateContribution = BigDecimal.valueOf(6L),
            total = BigDecimal.valueOf(100L),
            split = listOf(
                FinancingSourceBreakdownSplitLine(
                    fundId = 4L,
                    value = BigDecimal.valueOf(45L),
                    partnerContribution = BigDecimal.valueOf(30L),
                    publicContribution = BigDecimal.valueOf(9L),
                    automaticPublicContribution = BigDecimal.valueOf(10L),
                    privateContribution = BigDecimal.valueOf(11L),
                    total = BigDecimal.valueOf(75L),
                ),
            )
        )


        fun reportCoFinancingEntities() =
            listOf(ERDF_FUND, NDCI_FUND, IPA_III_FUND).toEntity().map {
                val projectReportCoFinancingEntity = mockk<ProjectReportCoFinancingEntity>()
                every { projectReportCoFinancingEntity.programmeFund } returns it
                return@map projectReportCoFinancingEntity
            }
    }


    @MockK
    lateinit var partnerReportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var projectReportCoFinancingRepository: ProjectReportCoFinancingRepository

    @MockK
    lateinit var projectReportCoFinancingOverviewRepository: ProjectReportVerificationCertificateCoFinancingOverviewRepository

    @MockK
    lateinit var projectReportCoFinancingSpfOverviewRepository: ProjectReportVerificationCertificateSpfCoFinancingOverviewRepository

    @MockK
    lateinit var projectReportRepository: ProjectReportRepository

    @InjectMockKs
    lateinit var projectReportFinancialOverviewPersistenceProvider: ProjectReportFinancialOverviewPersistenceProvider


    @Test
    fun getOverviewPerFund() {
        every { projectReportCoFinancingOverviewRepository.findAllByPartnerReportProjectReportId(PROJECT_REPORT_ID) } returns overviews
        assertThat(projectReportFinancialOverviewPersistenceProvider.getOverviewPerFund(PROJECT_REPORT_ID))
            .containsExactly(expectedFinancialDataBreakDownLine)
    }

    @Test
    fun `getOverviewSpfPerFund - empty`() {
        every { projectReportCoFinancingSpfOverviewRepository.findAllByProjectReportId(PROJECT_REPORT_ID) } returns emptyList()
        assertThat(projectReportFinancialOverviewPersistenceProvider.getOverviewSpfPerFund(PROJECT_REPORT_ID)).isNull()
    }

    @Test
    fun getOverviewSpfPerFund() {
        every { projectReportCoFinancingSpfOverviewRepository.findAllByProjectReportId(PROJECT_REPORT_ID) } returns spfOverviews
        assertThat(projectReportFinancialOverviewPersistenceProvider.getOverviewSpfPerFund(PROJECT_REPORT_ID))
            .isEqualTo(expectedFinancialDataBreakDownSpfLine)
    }

    @Test
    fun storeOverviewPerFund() {

        val toStore = listOf(
            FinancingSourceBreakdownLine(
            partnerReportId = 1L,
            partnerReportNumber = 1,
            spfLine = false,
            partnerId = PARTNER_ID,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerNumber = 1,
            fundsSorted = listOf(
                Pair(ERDF_FUND, 74999.97.toScaledBigDecimal()),
                Pair(NDCI_FUND, 24999.99.toScaledBigDecimal()),
                Pair(IPA_III_FUND, 6249.99.toScaledBigDecimal()),
            ),
            partnerContribution = 18750.01.toScaledBigDecimal(),
            publicContribution = 11612.22.toScaledBigDecimal(),
            automaticPublicContribution = 5938.24.toScaledBigDecimal(),
            privateContribution = 1199.52.toScaledBigDecimal(),
            total = 124999.96.toScaledBigDecimal(),
            split = listOf(
                FinancingSourceBreakdownSplitLine(
                    fundId = 1L,
                    value = 74999.97.toScaledBigDecimal(),
                    partnerContribution = 13235.30.toScaledBigDecimal(),
                    publicContribution = 8196.86.toScaledBigDecimal(),
                    automaticPublicContribution = 4191.69.toScaledBigDecimal(),
                    privateContribution = 846.72.toScaledBigDecimal(),
                    total = 88235.27.toScaledBigDecimal(),
                ),
                FinancingSourceBreakdownSplitLine(
                    fundId = 5L,
                    value = 24999.99.toScaledBigDecimal(),
                    partnerContribution = 4411.77.toScaledBigDecimal(),
                    publicContribution = 2732.28.toScaledBigDecimal(),
                    automaticPublicContribution = 1397.23.toScaledBigDecimal(),
                    privateContribution = 282.24.toScaledBigDecimal(),
                    total = 29411.76.toScaledBigDecimal(),
                ),
                FinancingSourceBreakdownSplitLine(
                    fundId = 4L,
                    value = 6249.99.toScaledBigDecimal(),
                    partnerContribution = 1102.94.toScaledBigDecimal(),
                    publicContribution = 683.07.toScaledBigDecimal(),
                    automaticPublicContribution = 349.30.toScaledBigDecimal(),
                    privateContribution = 70.56.toScaledBigDecimal(),
                    total = 7352.93.toScaledBigDecimal(),
                ),
            ),
            ),
            FinancingSourceBreakdownLine(
                partnerReportId = null,
                partnerReportNumber = null,
                spfLine = true,
                partnerId = null,
                partnerRole = null,
                partnerNumber = null,
                fundsSorted = mockk(),
                partnerContribution = BigDecimal.valueOf(15L),
                publicContribution = BigDecimal.valueOf(4L),
                automaticPublicContribution = BigDecimal.valueOf(5L),
                privateContribution = BigDecimal.valueOf(6L),
                total = BigDecimal.valueOf(100L),
                split = listOf(
                    FinancingSourceBreakdownSplitLine(
                        fundId = 4L,
                        value = BigDecimal.valueOf(45L),
                        partnerContribution = BigDecimal.valueOf(30L),
                        publicContribution = BigDecimal.valueOf(9L),
                        automaticPublicContribution = BigDecimal.valueOf(10L),
                        privateContribution = BigDecimal.valueOf(11L),
                        total = BigDecimal.valueOf(75L),
                    ),
                ),
            ),
        )

        val partnerReport = projectPartnerReportEntityMock()
        every { partnerReportRepository.findAllByProjectReportId(PROJECT_REPORT_ID) } returns listOf(partnerReport)
        val funds = reportCoFinancingEntities()
        every { projectReportCoFinancingRepository.findAllByIdReportIdOrderByIdFundSortNumber(PROJECT_REPORT_ID) } returns funds
        val projectReport = mockk<ProjectReportEntity>()
        every { projectReportRepository.getById(PROJECT_REPORT_ID) } returns projectReport

        every { projectReportCoFinancingOverviewRepository.deleteAllByPartnerReportProjectReportId(PROJECT_REPORT_ID) } returns Unit
        every { projectReportCoFinancingOverviewRepository.flush() } returns Unit
        val overviewsToStoreSlot = slot<List<ProjectReportVerificationCertificateContributionOverviewEntity>>()
        every { projectReportCoFinancingOverviewRepository.saveAll(capture(overviewsToStoreSlot)) } returnsArgument 0

        every { projectReportCoFinancingSpfOverviewRepository.deleteAllByProjectReportId(PROJECT_REPORT_ID) } returns Unit
        every { projectReportCoFinancingSpfOverviewRepository.flush() } returns Unit
        val overviewsSpfToStore = slot<List<ProjectReportVerificationCertificateSpfContributionOverviewEntity>>()
        every { projectReportCoFinancingSpfOverviewRepository.saveAll(capture(overviewsSpfToStore)) } returnsArgument 0

        projectReportFinancialOverviewPersistenceProvider.storeOverviewPerFund(PROJECT_REPORT_ID , toStore, 757L)

        assertThat(overviewsToStoreSlot.captured.size).isEqualTo(4)
        val lineTotal = overviewsToStoreSlot.captured.first()
        val splitLines = overviewsToStoreSlot.captured.filterNot { it.programmeFund == null && it.fundValue == null }

        assertThat(lineTotal.partnerReport).isEqualTo(partnerReport)
        assertThat(lineTotal.programmeFund).isNull()
        assertThat(lineTotal.fundValue).isNull()
        assertThat(lineTotal.partnerContribution).isEqualTo(BigDecimal.valueOf(18_750_01L, 2))
        assertThat(lineTotal.publicContribution).isEqualTo(BigDecimal.valueOf(11_612_22L, 2))
        assertThat(lineTotal.automaticPublicContribution).isEqualTo(BigDecimal.valueOf(5_938_24L, 2))
        assertThat(lineTotal.privateContribution).isEqualTo(BigDecimal.valueOf(1_199_52L, 2))
        assertThat(lineTotal.total).isEqualTo(BigDecimal.valueOf(124_999_96L, 2))

        assertThat(splitLines.size).isEqualTo(3)

        assertThat(overviewsSpfToStore.captured.size).isEqualTo(2)
        val lineTotalSpf = overviewsSpfToStore.captured.first()
        val splitLineSpf = overviewsSpfToStore.captured.last()

        assertThat(lineTotalSpf.projectReport).isEqualTo(projectReport)
        assertThat(lineTotalSpf.programmeFund).isNull()
        assertThat(lineTotalSpf.fundValue).isNull()
        assertThat(lineTotalSpf.partnerContribution).isEqualTo(BigDecimal.valueOf(15L))
        assertThat(lineTotalSpf.publicContribution).isEqualTo(BigDecimal.valueOf(4L))
        assertThat(lineTotalSpf.automaticPublicContribution).isEqualTo(BigDecimal.valueOf(5L))
        assertThat(lineTotalSpf.privateContribution).isEqualTo(BigDecimal.valueOf(6L))
        assertThat(lineTotalSpf.total).isEqualTo(BigDecimal.valueOf(100L))

        assertThat(splitLineSpf.projectReport).isEqualTo(projectReport)
        assertThat(splitLineSpf.programmeFund).isEqualTo(funds[2].programmeFund)
        assertThat(splitLineSpf.fundValue).isEqualTo(BigDecimal.valueOf(45L))
        assertThat(splitLineSpf.partnerContribution).isEqualTo(BigDecimal.valueOf(30L))
        assertThat(splitLineSpf.publicContribution).isEqualTo(BigDecimal.valueOf(9L))
        assertThat(splitLineSpf.automaticPublicContribution).isEqualTo(BigDecimal.valueOf(10L))
        assertThat(splitLineSpf.privateContribution).isEqualTo(BigDecimal.valueOf(11L))
        assertThat(splitLineSpf.total).isEqualTo(BigDecimal.valueOf(75L))
    }

}
