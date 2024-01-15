package io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundEntity
import io.cloudflight.jems.server.programme.entity.fund.ProgrammeFundTranslationEntity
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import io.cloudflight.jems.server.project.entity.report.project.ProjectReportSpfContributionClaimEntity
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ReportExpenditureCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedByContributionSource
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.SpfPreviouslyReportedContributionRow
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportSpfContributionClaimPersistenceProviderTest: UnitTest() {

    companion object {
        private const val REPORT_ID = 10L
        private const val FUND_ID = 5L

        private val reportEntity = mockk<ProjectReportEntity>()
        private val programmeFundEntity = ProgrammeFundEntity(
            id = FUND_ID,
            selected = true,
            type = ProgrammeFundType.OTHER,
            translatedValues = mutableSetOf(
                ProgrammeFundTranslationEntity(
                    translationId = TranslationId(mockk(), SystemLanguage.EN),
                    abbreviation = "EN abbr",
                    description = "EN desc"
                )
            )
        )

        private fun entity(id: Long = 5L) = ProjectReportSpfContributionClaimEntity(
            id = id,
            reportEntity = reportEntity,
            amountFromAf = BigDecimal.valueOf(80L),
            applicationFormPartnerContributionId = null,
            currentlyReported = BigDecimal.TEN,
            legalStatus = null,
            previouslyReported = BigDecimal.ZERO,
            programmeFund = programmeFundEntity,
            sourceOfContribution = null
        )

        private fun model(reportId: Long = REPORT_ID, id: Long = 5L, currentlyReported: BigDecimal = BigDecimal.TEN) = ProjectReportSpfContributionClaim(
            id = id,
            currentlyReported = currentlyReported,
            legalStatus = null,
            previouslyReported = BigDecimal.ZERO,
            programmeFund = ProgrammeFund(
                id = FUND_ID,
                selected = true,
                type = ProgrammeFundType.OTHER,
                abbreviation = setOf(InputTranslation(SystemLanguage.EN, translation = "EN abbr")),
                description = setOf(InputTranslation(SystemLanguage.EN, translation = "EN desc")),
            ),
            sourceOfContribution = null,
            amountInAf = BigDecimal.valueOf(80L),
            idFromApplicationForm = null,
            reportId = reportId,
        )

        val row1 = SpfPreviouslyReportedContributionRow(
            id = 500L,
            programmeFundId = 50L,
            applicationFormPartnerContributionId = null,
            sourceOfContribution = null,
            legalStatus = null,
            previouslyReportedAmount = BigDecimal.valueOf(15L),
        )
        val row2 = SpfPreviouslyReportedContributionRow(
            id = 501L,
            programmeFundId = null,
            applicationFormPartnerContributionId = 51L,
            sourceOfContribution = null,
            legalStatus = null,
            previouslyReportedAmount = BigDecimal.valueOf(16L),
        )
        val row3 = SpfPreviouslyReportedContributionRow(
            id = 502L,
            programmeFundId = null,
            applicationFormPartnerContributionId = 52L,
            sourceOfContribution = null,
            legalStatus = null,
            previouslyReportedAmount = BigDecimal.valueOf(17L),
        )

        private fun rowEntityFund(fundId: Long, current: BigDecimal) = ProjectReportSpfContributionClaimEntity(
            id = 500L,
            reportEntity = mockk(),
            programmeFund = mockk<ProgrammeFundEntity> { every { id } returns fundId },
            sourceOfContribution = null,
            legalStatus = null,
            applicationFormPartnerContributionId = null,
            amountFromAf = BigDecimal.valueOf(9998L),
            currentlyReported = current,
            previouslyReported = BigDecimal.valueOf(9999L),
        )

        private fun rowPartnerContrib(legalStatus: ProjectPartnerContributionStatus, current: BigDecimal) = ProjectReportSpfContributionClaimEntity(
            id = 500L,
            reportEntity = mockk(),
            programmeFund = null,
            sourceOfContribution = null,
            legalStatus = legalStatus,
            applicationFormPartnerContributionId = null,
            amountFromAf = BigDecimal.valueOf(9998L),
            currentlyReported = current,
            previouslyReported = BigDecimal.valueOf(9999L),
        )

        private fun rowEntityFund(fund: ProgrammeFundEntity, current: BigDecimal) = ProjectReportSpfContributionClaimEntity(
            id = 500L,
            reportEntity = mockk(),
            programmeFund = fund,
            sourceOfContribution = null,
            legalStatus = null,
            applicationFormPartnerContributionId = null,
            amountFromAf = BigDecimal.valueOf(9998L),
            currentlyReported = current,
            previouslyReported = BigDecimal.valueOf(9999L),
        )

    }

    @MockK
    lateinit var spfContributionClaimRepository: ProjectReportSpfContributionClaimRepository

    @InjectMockKs
    lateinit var reportSpfContributionClaimPersistence: ProjectReportSpfContributionClaimPersistenceProvider

    @Test
    fun getSpfContributionClaimsFor() {
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(REPORT_ID)) } returns listOf(entity())
        every { reportEntity.id } returns REPORT_ID

        Assertions.assertThat(
            reportSpfContributionClaimPersistence.getSpfContributionClaimsFor(REPORT_ID)
        ).isEqualTo(listOf(model()))
    }

    @Test
    fun updateContributionClaimReportedAmount() {
        val reportId = 33L
        val claimId = 250L
        val newValue = BigDecimal.valueOf(999L)
        val entity = entity(claimId)
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(reportId)) } returns listOf(entity)
        every { reportEntity.id } returns reportId
        assertThat(entity.currentlyReported).isNotEqualTo(newValue)

        val toUpdate = mapOf(claimId to newValue)
        assertThat(reportSpfContributionClaimPersistence.updateContributionClaimReportedAmount(reportId, toUpdate))
            .containsExactly(
                model(reportId, claimId, currentlyReported = newValue)
            )
        assertThat(entity.currentlyReported).isEqualTo(newValue)
    }

    @Test
    fun getSpfContributionCumulative() {
        val reportIds = setOf(15L, 16L)
        every { spfContributionClaimRepository.getPreviouslyReportedContributionAmount(reportIds) } returns
                listOf(row1, row2, row3)

        assertThat(reportSpfContributionClaimPersistence.getSpfContributionCumulative(reportIds)).isEqualTo(
            SpfPreviouslyReportedByContributionSource(
                finances = mapOf(50L to row1),
                partnerContributions = mapOf(51L to row2, 52L to row3),
            )
        )
    }

    @Test
    fun getPreviouslyReportedSpfContributions() {
        val reportIds = setOf(25L, 26L)
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(reportIds) } returns listOf(
            rowEntityFund(60L, BigDecimal.valueOf(350L)),
            rowEntityFund(61L, BigDecimal.valueOf(6800L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.Private, BigDecimal.valueOf(70L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.Public, BigDecimal.valueOf(80L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.AutomaticPublic, BigDecimal.valueOf(90L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.Private, BigDecimal.valueOf(100L)),
        )

        assertThat(reportSpfContributionClaimPersistence.getPreviouslyReportedSpfContributions(reportIds)).isEqualTo(
            ReportExpenditureCoFinancingColumn(
                funds = mapOf(
                    60L to BigDecimal.valueOf(350L),
                    61L to BigDecimal.valueOf(6800L),
                    null to BigDecimal.valueOf(340L),
                ),
                partnerContribution = BigDecimal.valueOf(340L),
                publicContribution = BigDecimal.valueOf(80L),
                automaticPublicContribution = BigDecimal.valueOf(90L),
                privateContribution = BigDecimal.valueOf(170L),
                sum = BigDecimal.valueOf(7490L),
            )
        )
    }

    @Test
    fun getCurrentSpfContribution() {
        val reportId = 35L
        val fund = ProgrammeFundEntity(id = 29L, selected = true)
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(reportId)) } returns listOf(
            rowEntityFund(fund, BigDecimal.valueOf(350L)),
            rowEntityFund(fund, BigDecimal.valueOf(6800L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.Private, BigDecimal.valueOf(70L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.Public, BigDecimal.valueOf(80L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.AutomaticPublic, BigDecimal.valueOf(90L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.Private, BigDecimal.valueOf(100L)),
        )

        assertThat(reportSpfContributionClaimPersistence.getCurrentSpfContribution(reportId)).isEqualTo(
            ReportCertificateCoFinancingColumn(
                funds = mapOf(
                    29L to BigDecimal.valueOf(7150L),
                    null to BigDecimal.valueOf(340L),
                ),
                partnerContribution = BigDecimal.valueOf(340L),
                publicContribution = BigDecimal.valueOf(80L),
                automaticPublicContribution = BigDecimal.valueOf(90L),
                privateContribution = BigDecimal.valueOf(170L),
                sum = BigDecimal.valueOf(7490L),
            )
        )
    }

    @Test
    fun `getCurrentSpfContribution - empty`() {
        val reportId = 36L
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(reportId)) } returns emptyList()
        assertThat(reportSpfContributionClaimPersistence.getCurrentSpfContribution(reportId)).isEqualTo(
            ReportCertificateCoFinancingColumn(
                funds = emptyMap(),
                partnerContribution = BigDecimal.ZERO,
                publicContribution = BigDecimal.ZERO,
                automaticPublicContribution = BigDecimal.ZERO,
                privateContribution = BigDecimal.ZERO,
                sum = BigDecimal.ZERO,
            )
        )
    }

    @Test
    fun getCurrentSpfContributionSplit() {
        val reportId = 45L
        val fund = ProgrammeFundEntity(id = 29L, selected = true)
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(reportId)) } returns listOf(
            rowEntityFund(fund, BigDecimal.valueOf(350L)),
            rowEntityFund(fund, BigDecimal.valueOf(6800L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.Private, BigDecimal.valueOf(70L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.Public, BigDecimal.valueOf(80L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.AutomaticPublic, BigDecimal.valueOf(90L)),
            rowPartnerContrib(ProjectPartnerContributionStatus.Private, BigDecimal.valueOf(100L)),
        )

        assertThat(reportSpfContributionClaimPersistence.getCurrentSpfContributionSplit(reportId)).isEqualTo(
            FinancingSourceBreakdownLine(
                partnerReportId = null,
                partnerReportNumber = null,
                spfLine = true,
                partnerId = null,
                partnerRole = null,
                partnerNumber = null,
                fundsSorted = listOf(
                    Pair(ProgrammeFund(id = 29L, selected = true), BigDecimal.valueOf(7150L)),
                ),
                partnerContribution = BigDecimal.valueOf(340L),
                publicContribution = BigDecimal.valueOf(80L),
                automaticPublicContribution = BigDecimal.valueOf(90L),
                privateContribution = BigDecimal.valueOf(170L),
                total = BigDecimal.valueOf(7490L),
                split = emptyList(),
            )
        )
    }

    @Test
    fun `getCurrentSpfContributionSplit - empty`() {
        val reportId = 46L
        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(reportId)) } returns emptyList()
        assertThat(reportSpfContributionClaimPersistence.getCurrentSpfContributionSplit(reportId)).isNull()
    }

    @Test
    fun getCurrentSpfContributions() {
        val reportIds = setOf(55L, 56L)
        every { spfContributionClaimRepository.getCurrentPerReport(reportIds) } returns listOf(
            Pair(55L, BigDecimal.valueOf(500L)),
            Pair(56L, BigDecimal.valueOf(600L)),
        )

        assertThat(reportSpfContributionClaimPersistence.getCurrentSpfContributions(reportIds)).containsAllEntriesOf(
            mapOf(
                55L to BigDecimal.valueOf(500L),
                56L to BigDecimal.valueOf(600L),
            )
        )
    }

    @Test
    fun resetSpfContributionClaims() {
        val entity = entity()
        assertThat(entity.currentlyReported).isNotZero()

        every { spfContributionClaimRepository.getAllByReportEntityIdIn(setOf(REPORT_ID)) } returns listOf(entity)

        reportSpfContributionClaimPersistence.resetSpfContributionClaims(REPORT_ID)

        assertThat(entity.currentlyReported).isZero()
    }

}
