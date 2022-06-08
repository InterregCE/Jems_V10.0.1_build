package io.cloudflight.jems.server.project.service.report.partner.contribution.getProjectPartnerReportContribution

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionData
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionRow
import io.cloudflight.jems.server.project.service.report.model.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.util.UUID

internal class GetProjectPartnerReportContributionTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 515L
        private val UPLOADED = ZonedDateTime.now()

        private val contributionPublic1 = ProjectPartnerReportEntityContribution(
            id = 1L,
            sourceOfContribution = "source public 1",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = 200L,
            historyIdentifier = UUID.randomUUID(),
            createdInThisReport = true,
            amount = 5L.toBigDecimal(),
            previouslyReported = 2L.toBigDecimal(),
            currentlyReported = 1L.toBigDecimal(),
            attachment = ProjectReportFileMetadata(45L, "file_pub_1.txt", UPLOADED),
        )
        private val contributionPublic2 = ProjectPartnerReportEntityContribution(
            id = 2L,
            sourceOfContribution = "source public 2",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = 201L,
            historyIdentifier = UUID.randomUUID(),
            createdInThisReport = false,
            amount = 15L.toBigDecimal(),
            previouslyReported = 3L.toBigDecimal(),
            currentlyReported = 3L.toBigDecimal(),
            attachment = ProjectReportFileMetadata(46L, "file_pub_2.txt", UPLOADED),
        )
        private val contributionPrivate = ProjectPartnerReportEntityContribution(
            id = 3L,
            sourceOfContribution = "source private",
            legalStatus = ProjectPartnerContributionStatus.Private,
            idFromApplicationForm = null,
            historyIdentifier = UUID.randomUUID(),
            createdInThisReport = false,
            amount = 60L.toBigDecimal(),
            previouslyReported = 20L.toBigDecimal(),
            currentlyReported = 12L.toBigDecimal(),
            attachment = ProjectReportFileMetadata(47L, "file_private.txt", UPLOADED),
        )
        private val contributionAutomatic = ProjectPartnerReportEntityContribution(
            id = 4L,
            sourceOfContribution = "source automaticPublic",
            legalStatus = ProjectPartnerContributionStatus.AutomaticPublic,
            idFromApplicationForm = 900L,
            historyIdentifier = UUID.randomUUID(),
            createdInThisReport = false,
            amount = 100L.toBigDecimal(),
            previouslyReported = 40L.toBigDecimal(),
            currentlyReported = 30L.toBigDecimal(),
            attachment = ProjectReportFileMetadata(48L, "file_automatic.txt", UPLOADED),
        )

        private val expectedContribution1 = ProjectPartnerReportContribution(
            id = 1L,
            sourceOfContribution = "source public 1",
            legalStatus = ProjectPartnerContributionStatus.Public,
            createdInThisReport = true,
            numbers = ProjectPartnerReportContributionRow(
                amount = 5L.toBigDecimal(),
                previouslyReported = 2L.toBigDecimal(),
                currentlyReported = 1L.toBigDecimal(),
                totalReportedSoFar = 3L.toBigDecimal(),
            ),
            attachment = ProjectReportFileMetadata(45L, "file_pub_1.txt", UPLOADED),
        )

        private val expectedContribution2 = ProjectPartnerReportContribution(
            id = 2L,
            sourceOfContribution = "source public 2",
            legalStatus = ProjectPartnerContributionStatus.Public,
            createdInThisReport = false,
            numbers = ProjectPartnerReportContributionRow(
                amount = 15L.toBigDecimal(),
                previouslyReported = 3L.toBigDecimal(),
                currentlyReported = 3L.toBigDecimal(),
                totalReportedSoFar = 6L.toBigDecimal(),
            ),
            attachment = ProjectReportFileMetadata(46L, "file_pub_2.txt", UPLOADED),
        )

        private val expectedContribution3 = ProjectPartnerReportContribution(
            id = 3L,
            sourceOfContribution = "source private",
            legalStatus = ProjectPartnerContributionStatus.Private,
            createdInThisReport = false,
            numbers = ProjectPartnerReportContributionRow(
                amount = 60L.toBigDecimal(),
                previouslyReported = 20L.toBigDecimal(),
                currentlyReported = 12L.toBigDecimal(),
                totalReportedSoFar = 32L.toBigDecimal(),
            ),
            attachment = ProjectReportFileMetadata(47L, "file_private.txt", UPLOADED),
        )

        private val expectedContribution4 = ProjectPartnerReportContribution(
            id = 4L,
            sourceOfContribution = "source automaticPublic",
            legalStatus = ProjectPartnerContributionStatus.AutomaticPublic,
            createdInThisReport = false,
            numbers = ProjectPartnerReportContributionRow(
                amount = 100L.toBigDecimal(),
                previouslyReported = 40L.toBigDecimal(),
                currentlyReported = 30L.toBigDecimal(),
                totalReportedSoFar = 70L.toBigDecimal(),
            ),
            attachment = ProjectReportFileMetadata(48L, "file_automatic.txt", UPLOADED),
        )

        private val expectedOverview = ProjectPartnerReportContributionOverview(
            public = ProjectPartnerReportContributionRow(
                amount = 20L.toBigDecimal(),
                previouslyReported = 5L.toBigDecimal(),
                currentlyReported = 4L.toBigDecimal(),
                totalReportedSoFar = 9L.toBigDecimal(),
            ),
            automaticPublic = ProjectPartnerReportContributionRow(
                amount = 100L.toBigDecimal(),
                previouslyReported = 40L.toBigDecimal(),
                currentlyReported = 30L.toBigDecimal(),
                totalReportedSoFar = 70L.toBigDecimal(),
            ),
            private = ProjectPartnerReportContributionRow(
                amount = 60L.toBigDecimal(),
                previouslyReported = 20L.toBigDecimal(),
                currentlyReported = 12L.toBigDecimal(),
                totalReportedSoFar = 32L.toBigDecimal(),
            ),
            total = ProjectPartnerReportContributionRow(
                amount = 180L.toBigDecimal(),
                previouslyReported = 65L.toBigDecimal(),
                currentlyReported = 46L.toBigDecimal(),
                totalReportedSoFar = 111L.toBigDecimal(),
            ),
        )

    }

    @MockK
    lateinit var reportContributionPersistence: ProjectReportContributionPersistence

    @InjectMockKs
    lateinit var getReportContribution: GetProjectPartnerReportContribution

    @Test
    fun getContribution() {
        every { reportContributionPersistence.getPartnerReportContribution(PARTNER_ID, reportId = 74L) } returns
            listOf(contributionPublic1, contributionPublic2, contributionPrivate, contributionAutomatic)

        assertThat(getReportContribution.getContribution(PARTNER_ID, reportId = 74L)).isEqualTo(
            ProjectPartnerReportContributionData(
                contributions = listOf(expectedContribution1, expectedContribution2, expectedContribution3, expectedContribution4),
                overview = expectedOverview,
            )
        )
    }
}
