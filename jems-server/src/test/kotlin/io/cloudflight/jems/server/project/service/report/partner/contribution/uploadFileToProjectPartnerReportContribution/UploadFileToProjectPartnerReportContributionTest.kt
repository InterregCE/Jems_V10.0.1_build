package io.cloudflight.jems.server.project.service.report.partner.contribution.uploadFileToProjectPartnerReportContribution

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectReportContributionPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private const val PARTNER_ID = 3520L
private const val REPORT_ID = 362L
private const val USER_ID = 696L

internal class UploadFileToProjectPartnerReportProcurementTest : UnitTest() {

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence
    @MockK
    lateinit var reportContributionPersistence: ProjectReportContributionPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence
    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadFileToProjectPartnerReportContribution

    @BeforeEach
    fun setup() {
        clearMocks(reportFilePersistence)
        every { securityService.getUserIdOrThrow() } returns USER_ID
    }

    @Test
    fun uploadToContribution() {
        every { reportContributionPersistence.existsByContributionId(PARTNER_ID, REPORT_ID, 25L) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns 700L

        val slotFile = slot<ProjectReportFileCreate>()
        val mockResult = mockk<ProjectReportFileMetadata>()
        every { reportFilePersistence.updatePartnerReportContributionAttachment(25L, capture(slotFile)) } returns mockResult

        val file = ProjectFile(ByteArray(5).inputStream(), "file_name.xlsx", 55L)

        assertThat(interactor.uploadToContribution(PARTNER_ID, REPORT_ID, 25L, file)).isEqualTo(mockResult)

        with(slotFile.captured) {
            assertThat(projectId).isEqualTo(700L)
            assertThat(partnerId).isEqualTo(PARTNER_ID)
            assertThat(name).isEqualTo("file_name.xlsx")
            assertThat(path).isEqualTo("Project/000700/Report/Partner/003520/PartnerReport/000362/Contribution/000025/")
            assertThat(type).isEqualTo(ProjectPartnerReportFileType.Contribution)
            assertThat(size).isEqualTo(55L)
            assertThat(userId).isEqualTo(USER_ID)
        }
    }

    @Test
    fun `uploadToContribution - not exists`() {
        every { reportContributionPersistence.existsByContributionId(PARTNER_ID, REPORT_ID, -1L) } returns false
        assertThrows<ContributionNotFoundException> { interactor.uploadToContribution(PARTNER_ID, REPORT_ID, -1L, mockk()) }
    }

    @Test
    fun `uploadToContribution - file type invalid`() {
        every { reportContributionPersistence.existsByContributionId(PARTNER_ID, REPORT_ID, 30L) } returns true

        val file = mockk<ProjectFile>()
        every { file.name } returns "invalid.exe"

        assertThrows<FileTypeNotSupported> {
            interactor.uploadToContribution(PARTNER_ID, REPORT_ID, 30L, file)
        }
    }

}
