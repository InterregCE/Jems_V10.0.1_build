package io.cloudflight.jems.server.project.service.report.project.spfContributionClaim.updateSpfContributionClaim

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.notification.inApp.service.model.NotificationType
import io.cloudflight.jems.server.project.repository.report.project.spfContributionClaim.ProjectReportSpfContributionClaimPersistenceProvider
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.project.ProjectReportStatus
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaim
import io.cloudflight.jems.server.project.service.report.model.project.spfContributionClaim.ProjectReportSpfContributionClaimUpdate
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import io.cloudflight.jems.server.toScaledBigDecimal
import io.cloudflight.jems.server.utils.ERDF_FUND
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal

class UpdateSpfContributionClaimTest: UnitTest() {

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

    @MockK
    lateinit var  reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var service: UpdateSpfContributionClaim

    @Test
    fun updateSpfContributionClaim() {

        val spfContributionUpdates = listOf(
            ProjectReportSpfContributionClaimUpdate(
                id = 1,
                currentlyReported = BigDecimal.valueOf(700.00)
            ),
            ProjectReportSpfContributionClaimUpdate(
                id = 2,
                currentlyReported = BigDecimal.valueOf(30.00)
            )
        )



        every { reportPersistence.getReportById(projectId, reportId = reportId).status } returns ProjectReportStatus.Draft
        every { projectReportSpfContributionClaimPersistenceProvider.getSpfContributionClaimsFor(reportId) } returns spfContributionClaims
        val updateSlot = slot<Map<Long, BigDecimal>>()

        every {
            projectReportSpfContributionClaimPersistenceProvider.updateContributionClaimReportedAmount(reportId, capture(updateSlot))
        } returns listOf(
            spfContributionClaims[0].copy(currentlyReported = BigDecimal.valueOf(700.00)),
            spfContributionClaims[1].copy(currentlyReported = BigDecimal.valueOf(30.00))
        )

        assertThat(service.update(projectId, reportId, spfContributionUpdates)).isEqualTo(
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
                    currentlyReported = BigDecimal.valueOf(700.00),
                    totalReportedSoFar = BigDecimal.valueOf(800.00)
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
                    currentlyReported = BigDecimal.valueOf(30.00),
                    totalReportedSoFar = BigDecimal.valueOf(80.00)
                )
            )
        )

        assertThat(updateSlot.captured[1]).isEqualTo(BigDecimal.valueOf(700.00))
        assertThat(updateSlot.captured[2]).isEqualTo(BigDecimal.valueOf(30.00))

    }


    @Test
    fun `updateSpfContributionClaim - wrong contribution sources throws exception`() {

        val spfContributionUpdates = listOf(
            ProjectReportSpfContributionClaimUpdate(
                id = 1,
                currentlyReported = BigDecimal.valueOf(700.00)
            ),
            ProjectReportSpfContributionClaimUpdate(
                id = 3,
                currentlyReported = BigDecimal.valueOf(30.00)
            )
        )

        every { reportPersistence.getReportById(projectId, reportId = reportId).status } returns ProjectReportStatus.Draft
        every { projectReportSpfContributionClaimPersistenceProvider.getSpfContributionClaimsFor(reportId) } returns spfContributionClaims

        assertThrows<ContributionSourcesException> { service.update(projectId, reportId, spfContributionUpdates) }
    }
    @ParameterizedTest(name = "Project report status - {0}")
    @EnumSource(value = ProjectReportStatus::class, names = ["ReOpenSubmittedLast", "VerificationReOpenedLast", "Draft"], mode = EnumSource.Mode.EXCLUDE )
    fun `updateSpfContributionClaim - wrong status throws exception`(status: ProjectReportStatus) {
        every { reportPersistence.getReportById(projectId, reportId = reportId).status } returns status
        assertThrows<ReportStatusNotValidException> { service.update(projectId, reportId, emptyList()) }
    }


}
