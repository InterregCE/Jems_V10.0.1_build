package io.cloudflight.jems.server.project.service.file.download_project_file

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.repository.ProjectNotFoundException
import io.cloudflight.jems.server.project.repository.file.ProjectFileNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.utils.FILE_ID
import io.cloudflight.jems.server.utils.FILE_NAME
import io.cloudflight.jems.server.utils.PROJECT_ID
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.utils.USER_ID
import io.cloudflight.jems.server.utils.currentUser
import io.cloudflight.jems.server.utils.fileByteArray
import io.cloudflight.jems.server.utils.fileMetadata
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

internal class DownloadProjectFileTest : UnitTest() {
    @MockK
    lateinit var filePersistence: ProjectFilePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var downloadProjectFile: DownloadProjectFile

    @Test
    fun `should throw ProjectNotFoundException when project does not exist`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } throws ProjectNotFoundException()
        assertThrows<ProjectNotFoundException> { downloadProjectFile.download(PROJECT_ID, FILE_ID) }
    }

    @Test
    fun `should throw ProjectFileNotFoundException when project file metadata does not exist`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { filePersistence.getFileMetadata(FILE_ID) } throws ProjectFileNotFoundException()
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { securityService.currentUser } returns currentUser

        assertThrows<ProjectFileNotFoundException> { downloadProjectFile.download(PROJECT_ID, FILE_ID) }
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_FILE_DOWNLOADED_FAILED)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "document $FILE_ID download failed from project application $PROJECT_ID by $USER_ID"
        )
    }

    @Test
    fun `should return file metadata and its byte array when there is no problem`() {
        val auditSlot = slot<AuditCandidateEvent>()
        val fileMetadata = fileMetadata(ZonedDateTime.now().plusDays(1))
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { filePersistence.getFileMetadata(FILE_ID) } returns fileMetadata
        every { filePersistence.getFile(PROJECT_ID, FILE_ID, FILE_NAME) } returns fileByteArray
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit
        every { securityService.currentUser } returns currentUser

        val file = downloadProjectFile.download(PROJECT_ID, FILE_ID)
        assertThat(file.first).isEqualTo(fileMetadata)
        assertThat(file.second).isEqualTo(fileByteArray)
        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_FILE_DOWNLOADED_SUCCESSFULLY)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "document $FILE_NAME downloaded from project application $PROJECT_ID by $USER_ID"
        )
    }
}
