package io.cloudflight.jems.server.project.service.report.project.resultPrinciple.attachment.upload

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.project.file.ProjectReportFilePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class UploadAttachmentToProjectReportResultPrincipleTest {

    @MockK
    private lateinit var filePersistence: ProjectReportFilePersistence

    @MockK
    private lateinit var securityService: SecurityService

    @InjectMockKs
    private lateinit var interactor: UploadAttachmentToProjectReportResultPrinciple

    @BeforeEach
    fun reset() {
        clearMocks(filePersistence, securityService)
    }

    @Test
    fun upload() {
        val projectId = 21L
        val reportId = 22L
        val resultNumber = 23
        val userId = 24L
        val file = ProjectFile(mockk(), name = "project-file.pdf", 100L)
        val persistedFile = JemsFileMetadata(25L, "project-file", ZonedDateTime.now())

        every { securityService.getUserIdOrThrow() } returns userId
        val slot = slot<JemsFileCreate>()
        every { filePersistence.updateProjectResultAttachment(reportId, resultNumber, capture(slot)) } returns persistedFile

        Assertions.assertThat(interactor.upload(projectId, reportId, resultNumber, file)).isEqualTo(persistedFile)
        Assertions.assertThat(slot.captured.projectId).isEqualTo(projectId)
        Assertions.assertThat(slot.captured.partnerId).isNull()
        Assertions.assertThat(slot.captured.type).isEqualTo(JemsFileType.ProjectResult)
        Assertions.assertThat(slot.captured.path).contains(projectId.toString(), reportId.toString(), resultNumber.toString())
        Assertions.assertThat(slot.captured.userId).isEqualTo(userId)
    }
}
