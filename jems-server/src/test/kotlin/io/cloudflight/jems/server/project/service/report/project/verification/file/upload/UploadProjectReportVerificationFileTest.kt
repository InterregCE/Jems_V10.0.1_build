package io.cloudflight.jems.server.project.service.report.project.verification.file.upload

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.VerificationDocument
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
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
import java.io.InputStream

class UploadProjectReportVerificationFileTest : UnitTest() {

    companion object {
        const val PROJECT_ID = 91L
        const val REPORT_ID = 95L
        fun filePath() = VerificationDocument.generatePath(PROJECT_ID, REPORT_ID)
    }

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var projectReportFilePersistence: ProjectReportFilePersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UploadProjectReportVerificationFile

    @BeforeEach
    fun setup() {
        clearMocks(filePersistence, projectReportFilePersistence, securityService)
    }

    @Test
    fun upload() {
        val userId = 97L
        val fileToAdd = slot<JemsFileCreate>()
        val savedFile = mockk<JemsFileMetadata>()

        val file = ProjectFile(
            stream = mockk<InputStream>(),
            name = "new.pdf",
            size = 128L,
        )

        every { filePersistence.existsFile(exactPath = filePath(), fileName = file.name) } returns false

        every { securityService.getUserIdOrThrow() } returns userId
        every { projectReportFilePersistence.addAttachmentToProjectReport(capture(fileToAdd)) } returns savedFile

        assertThat(interactor.upload(PROJECT_ID, REPORT_ID, file)).isEqualTo(savedFile)
        assertThat(fileToAdd.captured).isEqualTo(
            JemsFileCreate(
                projectId = PROJECT_ID,
                partnerId = null,
                name = "new.pdf",
                path = filePath(),
                type = VerificationDocument,
                size = file.size,
                content = file.stream,
                userId = userId,
                defaultDescription = "",
            )
        )
    }

    @Test
    fun `upload - FileAlreadyExists`() {
        val fileName = "duplicate.pdf"
        val file = mockk<ProjectFile> { every { name } returns fileName }

        every { filePersistence.existsFile(exactPath = eq(filePath()), fileName = fileName) } returns true

        assertThrows<FileAlreadyExists> { interactor.upload(projectId = PROJECT_ID, reportId = REPORT_ID, file = file) }
    }

    @Test
    fun `upload - FileTypeNotSupported`() {
        val file = mockk<ProjectFile> { every { name } returns "file.jems" }

        assertThrows<FileTypeNotSupported> { interactor.upload(projectId = PROJECT_ID, reportId = REPORT_ID, file = file) }
    }
}
