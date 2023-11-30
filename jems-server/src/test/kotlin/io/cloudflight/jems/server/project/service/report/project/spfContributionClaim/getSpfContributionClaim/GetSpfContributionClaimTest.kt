package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.getSpfContributionClaim

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistenceProvider
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class GetSpfContributionClaimTest: UnitTest() {


    companion object {
        const val projectId = 3L
        const val partnerId = 1L
        const val reportId = 21L

        val spfContributionClaims = listOf(
            ProjectReportSpfContributionClaim(
                id = 1,
                reportId = reportId,
                programmeFund = ERDF_FUND,
                idFromApplicationForm = null,
                sourceOfContribution = null,
                legalStatus = null,
                amountInAf = 500.00.toScaledBigDecimal(),
                previouslyReported = BigDecimal.valueOf(100.00),
                currentlyReported = BigDecimal.valueOf(20.00)
            ),
            ProjectReportSpfContributionClaim(
                id = 2,
                reportId = reportId,
                programmeFund = null,
                idFromApplicationForm = 1L,
                sourceOfContribution = "Contribution source one",
                legalStatus = ProjectPartnerContributionStatus.Private,
                amountInAf = BigDecimal.valueOf(500.00),
                previouslyReported = BigDecimal.valueOf(50.00),
                currentlyReported = BigDecimal.valueOf(9.00)
            )
        )
    }

    @MockK
    lateinit var projectReportSpfContributionClaimPersistenceProvider: ProjectReportSpfContributionClaimPersistenceProvider

    @InjectMockKs
    lateinit var service: GetSpfContributionClaim


    @Test
    fun getContributionClaims() {
        every { projectReportSpfContributionClaimPersistenceProvider.getSpfContributionClaimsFor(reportId) } returns spfContributionClaims

        assertThat(service.getContributionClaims(projectId ,reportId)).isEqualTo(
            listOf(
                ProjectReportSpfContributionClaim(
                    id = 1,
                    reportId = reportId,
                    programmeFund = ERDF_FUND,
                    idFromApplicationForm = null,
                    sourceOfContribution = null,
                    legalStatus = null,
                    amountInAf = 500.00.toScaledBigDecimal(),
                    previouslyReported = BigDecimal.valueOf(100.00),
                    currentlyReported = BigDecimal.valueOf(20.00),
                    totalReportedSoFar = BigDecimal.valueOf(120.00),

                ),
                ProjectReportSpfContributionClaim(
                    id = 2,
                    reportId = reportId,
                    programmeFund = null,
                    idFromApplicationForm = 1L,
                    sourceOfContribution = "Contribution source one",
                    legalStatus = ProjectPartnerContributionStatus.Private,
                    amountInAf = BigDecimal.valueOf(500.00),
                    previouslyReported = BigDecimal.valueOf(50.00),
                    currentlyReported = BigDecimal.valueOf(9.00),
                    totalReportedSoFar = BigDecimal.valueOf(59.00),

                )
            )
        )
    }
}
