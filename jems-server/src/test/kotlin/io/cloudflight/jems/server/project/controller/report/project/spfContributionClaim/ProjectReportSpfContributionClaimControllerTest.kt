package io.cloudflight.jems.server.project.controller.report.project.spfContributionClaim

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.api.project.dto.report.project.spfContribution.ProjectReportSpfContributionClaimDTO
import io.cloudflight.jems.api.project.dto.report.project.spfContribution.ProjectReportSpfContributionClaimUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.controller.fund.toDto
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaimUpdate
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.getSpfContributionClaim.GetSpfContributionClaimInteractor
import io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.updateSpfContributionClaim.UpdateSpfContributionClaimInteractor
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportSpfContributionClaimControllerTest : UnitTest() {

    companion object {
        const val projectId = 3L
        const val partnerId = 1L
        const val reportId = 21L

        val spfContributionClaimsModels = listOf(
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
                totalReportedSoFar = BigDecimal.valueOf(120.00)

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


        val expectedContributions = listOf(
            ProjectReportSpfContributionClaimDTO(
                id = 1,
                reportId = reportId,
                programmeFund = ERDF_FUND.toDto(),
                sourceOfContribution = null,
                legalStatus = null,
                amountInAf = 500.00.toScaledBigDecimal(),
                previouslyReported = BigDecimal.valueOf(100.00),
                currentlyReported = BigDecimal.valueOf(20.00),
                totalReportedSoFar = BigDecimal.valueOf(120.00),
            ),
            ProjectReportSpfContributionClaimDTO(
                id = 2,
                reportId = reportId,
                programmeFund = null,
                sourceOfContribution = "Contribution source one",
                legalStatus = ProjectPartnerContributionStatusDTO.Private,
                amountInAf = BigDecimal.valueOf(500.00),
                previouslyReported = BigDecimal.valueOf(50.00),
                currentlyReported = BigDecimal.valueOf(9.00),
                totalReportedSoFar = BigDecimal.valueOf(59.00)
            )
        )
    }


    @MockK
    lateinit var projectReportSpfContributionClaim: GetSpfContributionClaimInteractor


    @MockK
    lateinit var updateSpfContributionClaim: UpdateSpfContributionClaimInteractor

    @InjectMockKs
    lateinit var controller: ProjectReportSpfContributionClaimController


    @Test
    fun getContributionClaims() {
        every { projectReportSpfContributionClaim.getContributionClaims(projectId,reportId) } returns spfContributionClaimsModels

        assertThat(controller.getContributionClaims(projectId, reportId)).isEqualTo(expectedContributions)
    }


    @Test
    fun updateContributionClaims() {

        val toUpdate = listOf(
            ProjectReportSpfContributionClaimUpdateDTO(
                id = 1,
                currentlyReported = BigDecimal.valueOf(100.00),
            ),
            ProjectReportSpfContributionClaimUpdateDTO(
                id = 2,
                currentlyReported = BigDecimal.valueOf(90.00),
            )
        )

        every { updateSpfContributionClaim.update(projectId, reportId, any()) } returns listOf(
            spfContributionClaimsModels[0].copy(
                currentlyReported = BigDecimal.valueOf(100.00),
                totalReportedSoFar = BigDecimal.valueOf(220.00)
            ),
            spfContributionClaimsModels[1].copy(
                currentlyReported = BigDecimal.valueOf(90.00),
                totalReportedSoFar = BigDecimal.valueOf(149.00)
            ),
        )

        assertThat(controller.updateContributionClaims(projectId, reportId, toUpdate)).isEqualTo(
            listOf(
                expectedContributions[0].copy(
                    currentlyReported = BigDecimal.valueOf(100.00),
                    totalReportedSoFar = BigDecimal.valueOf(220.00)
                ),
                expectedContributions[1].copy(
                    currentlyReported = BigDecimal.valueOf(90.00),
                    totalReportedSoFar = BigDecimal.valueOf(149.00)
                )
            )
        )
    }


}
