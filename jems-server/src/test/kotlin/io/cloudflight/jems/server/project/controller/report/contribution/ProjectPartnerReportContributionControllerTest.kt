package io.cloudflight.jems.server.project.controller.report.contribution

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.ProjectPartnerReportContributionRowDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.UpdateProjectPartnerReportContributionCustomDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.UpdateProjectPartnerReportContributionDTO
import io.cloudflight.jems.api.project.dto.report.partner.contribution.UpdateProjectPartnerReportContributionDataDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.controller.report.dummyFile
import io.cloudflight.jems.server.project.controller.report.dummyFileDto
import io.cloudflight.jems.server.project.controller.report.dummyFileExpected
import io.cloudflight.jems.server.project.controller.report.dummyMultipartFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContribution
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionData
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionOverview
import io.cloudflight.jems.server.project.service.report.model.contribution.ProjectPartnerReportContributionRow
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionExisting
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionCustom
import io.cloudflight.jems.server.project.service.report.model.contribution.update.UpdateProjectPartnerReportContributionWrapper
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.contribution.getProjectPartnerReportContribution.GetProjectPartnerReportContributionInteractor
import io.cloudflight.jems.server.project.service.report.partner.contribution.updateProjectPartnerReportContribution.UpdateProjectPartnerReportContributionInteractor
import io.cloudflight.jems.server.project.service.report.partner.contribution.uploadFileToProjectPartnerReportContribution.UploadFileToProjectPartnerReportContributionInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.ZonedDateTime

class ProjectPartnerReportContributionControllerTest : UnitTest() {

    private val PARTNER_ID = 447L
    private val REPORT_ID = 466L
    private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

    private fun Long.toBigDecimal() = BigDecimal.valueOf(this)

    private val dummyContribution = ProjectPartnerReportContribution(
        id = 265,
        sourceOfContribution = "source text",
        legalStatus = ProjectPartnerContributionStatus.Public,
        createdInThisReport = true,
        numbers = ProjectPartnerReportContributionRow(
            amount = BigDecimal.TEN,
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
        ),
        attachment = ProjectReportFileMetadata(510L, "file.txt", UPLOADED),
    )

    private val dummyOverview = ProjectPartnerReportContributionOverview(
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

    private val expectedContribution = ProjectPartnerReportContributionDTO(
        id = 265,
        sourceOfContribution = "source text",
        legalStatus = ProjectPartnerContributionStatusDTO.Public,
        createdInThisReport = true,
        numbers = ProjectPartnerReportContributionRowDTO(
            amount = BigDecimal.TEN,
            previouslyReported = BigDecimal.ZERO,
            currentlyReported = BigDecimal.ONE,
            totalReportedSoFar = BigDecimal.ONE,
        ),
        attachment = ProjectReportFileMetadataDTO(510L, "file.txt", UPLOADED),
    )

    private val expectedOverview = ProjectPartnerReportContributionOverviewDTO(
        publicContribution = ProjectPartnerReportContributionRowDTO(
            amount = 20L.toBigDecimal(),
            previouslyReported = 5L.toBigDecimal(),
            currentlyReported = 4L.toBigDecimal(),
            totalReportedSoFar = 9L.toBigDecimal(),
        ),
        automaticPublicContribution = ProjectPartnerReportContributionRowDTO(
            amount = 100L.toBigDecimal(),
            previouslyReported = 40L.toBigDecimal(),
            currentlyReported = 30L.toBigDecimal(),
            totalReportedSoFar = 70L.toBigDecimal(),
        ),
        privateContribution = ProjectPartnerReportContributionRowDTO(
            amount = 60L.toBigDecimal(),
            previouslyReported = 20L.toBigDecimal(),
            currentlyReported = 12L.toBigDecimal(),
            totalReportedSoFar = 32L.toBigDecimal(),
        ),
        total = ProjectPartnerReportContributionRowDTO(
            amount = 180L.toBigDecimal(),
            previouslyReported = 65L.toBigDecimal(),
            currentlyReported = 46L.toBigDecimal(),
            totalReportedSoFar = 111L.toBigDecimal(),
        ),
    )

    @MockK
    lateinit var getContribution: GetProjectPartnerReportContributionInteractor

    @MockK
    lateinit var updateContribution: UpdateProjectPartnerReportContributionInteractor

    @MockK
    lateinit var uploadFileToContribution: UploadFileToProjectPartnerReportContributionInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportContributionController

    @Test
    fun getContribution() {
        every { getContribution.getContribution(partnerId = PARTNER_ID, reportId = REPORT_ID) } returns
            ProjectPartnerReportContributionData(
                contributions = listOf(dummyContribution),
                overview = dummyOverview,
            )
        with(controller.getContribution(partnerId = PARTNER_ID, reportId = REPORT_ID)) {
            assertThat(contributions).containsExactly(expectedContribution)
            assertThat(overview).isEqualTo(expectedOverview)
        }
    }

    @Test
    fun `update - test input and output mappings`() {
        val toBeChanged = UpdateProjectPartnerReportContributionDataDTO(
            toBeUpdated = setOf(UpdateProjectPartnerReportContributionDTO(id = 45L, currentlyReported = BigDecimal.ONE)),
            toBeDeletedIds = setOf(75L),
            toBeCreated = listOf(UpdateProjectPartnerReportContributionCustomDTO(
                sourceOfContribution = "source name updated",
                legalStatus = ProjectPartnerContributionStatusDTO.Private,
                currentlyReported = BigDecimal.TEN,
            ))
        )
        val slotData = slot<UpdateProjectPartnerReportContributionWrapper>()
        every {
            updateContribution.update(
                partnerId = PARTNER_ID,
                reportId = REPORT_ID,
                data = capture(slotData),
            )
        } returns ProjectPartnerReportContributionData(
            contributions = listOf(dummyContribution),
            overview = dummyOverview,
        )

        with(
            controller.updateContribution(
                partnerId = PARTNER_ID,
                reportId = REPORT_ID,
                contributionData = toBeChanged,
            )
        ) {
            assertThat(contributions).containsExactly(expectedContribution)
            assertThat(overview).isEqualTo(expectedOverview)
        }

        with(slotData.captured) {
            assertThat(toBeUpdated).containsExactly(UpdateProjectPartnerReportContributionExisting(id = 45L, currentlyReported = BigDecimal.ONE))
            assertThat(toBeDeletedIds).containsExactly(75L)
            assertThat(toBeCreated).containsExactly(
                UpdateProjectPartnerReportContributionCustom(
                    sourceOfContribution = "source name updated",
                    legalStatus = ProjectPartnerContributionStatus.Private,
                    currentlyReported = BigDecimal.TEN,
                )
            )
        }
    }

    @Test
    fun uploadFileToContribution() {
        val slotFile = slot<ProjectFile>()
        every { uploadFileToContribution.uploadToContribution(PARTNER_ID, reportId = 35L, 70L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadFileToContribution(PARTNER_ID, 35L, 70L, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

}
