package io.cloudflight.jems.server.project.service.report.partner.file.uploadFileToProjectPartnerReport

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream

class UploadFileToProjectPartnerReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 642L
        private const val USER_ID = 9860L

        private val content = mockk<InputStream>()

        private const val expectedPath = "Project/000642/Report/Partner/000045/PartnerReport/000900/"
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var reportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadFileToProjectPartnerReport

    @Test
    fun uploadToReport() {
        every { reportPersistence.exists(45L, 900L) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(45L) } returns PROJECT_ID
        every { reportFilePersistence.existsFile(expectedPath, "test.xlsx") } returns false
        val fileToAdd = slot<ProjectReportFileCreate>()
        val mockResult = mockk<ProjectReportFileMetadata>()
        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { reportFilePersistence.addAttachmentToPartnerReport(capture(fileToAdd)) } returns mockResult

        val file = ProjectFile(
            stream = content,
            name = "test.xlsx",
            size = 8L,
        )
        assertThat(interactor.uploadToReport(45L, 900L, file)).isEqualTo(mockResult)

        assertThat(fileToAdd.captured).isEqualTo(
            ProjectReportFileCreate(
                projectId = PROJECT_ID,
                partnerId = 45L,
                name = "test.xlsx",
                path = expectedPath,
                type = ProjectPartnerReportFileType.PartnerReport,
                size = 8L,
                content = content,
                userId = USER_ID,
            )
        )
    }

    @Test
    fun `uploadToReport - not existing`() {
        every { reportPersistence.exists(10L, -1L) } returns false
        assertThrows<PartnerReportNotFound> {
            interactor.uploadToReport(10L, -1L, mockk())
        }
    }

    @Test
    fun `uploadToReport - file type invalid`() {
        every { reportPersistence.exists(45L, 901L) } returns true

        val file = mockk<ProjectFile>()
        every { file.name } returns "invalid.exe"

        assertThrows<FileTypeNotSupported> {
            interactor.uploadToReport(45L, 901L, file)
        }
    }

    @Test
    fun `uploadToReport - file already exists`() {
        every { reportPersistence.exists(45L, 902L) } returns true
        every { partnerPersistence.getProjectIdForPartnerId(45L) } returns PROJECT_ID
        every { reportFilePersistence.existsFile(
            exactPath = "Project/000642/Report/Partner/000045/PartnerReport/000902/",
            fileName = "duplicate-file.docx"
        ) } returns true

        val file = mockk<ProjectFile>()
        every { file.name } returns "duplicate-file.docx"

        assertThrows<FileAlreadyExists> {
            interactor.uploadToReport(45L, 902L, file)
        }
    }

}
