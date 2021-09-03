package io.cloudflight.jems.server.project.service.file.delete_project_file

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.project.repository.ProjectNotFoundException
import io.cloudflight.jems.server.project.repository.file.ProjectFileNotFoundException
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.projectWithId
import io.cloudflight.jems.server.utils.FILE_ID
import io.cloudflight.jems.server.utils.FILE_NAME
import io.cloudflight.jems.server.utils.PROJECT_ID
import io.cloudflight.jems.server.project.service.file.ProjectFilePersistence
import io.cloudflight.jems.server.utils.USER_ID
import io.cloudflight.jems.server.utils.fileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategoryType
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

internal class DeleteProjectFileTest : UnitTest() {

    @MockK
    lateinit var filePersistence: ProjectFilePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var deleteProjectFile: DeleteProjectFile

    @Test
    fun `should throw ProjectNotFoundException when project does not exist`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } throws ProjectNotFoundException()
        assertThrows<ProjectNotFoundException> { deleteProjectFile.delete(PROJECT_ID, FILE_ID) }
    }

    @Test
    fun `should throw ProjectFileNotFoundException when project file metadata does not exist`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { filePersistence.getFileMetadata(FILE_ID) } throws ProjectFileNotFoundException()
        assertThrows<ProjectFileNotFoundException> { deleteProjectFile.delete(PROJECT_ID, FILE_ID) }
    }

    @Test
    fun `should throw exception when project file cannot be removed`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID)
        every { filePersistence.getFileMetadata(FILE_ID) } returns fileMetadata(ZonedDateTime.now().minusDays(1))
        every { filePersistence.getFileCategoryTypeSet(FILE_ID) } returns setOf(ProjectFileCategoryType.APPLICATION)

        assertThrows<DeletingOldFileFromApplicationCategoryIsNotAllowedException> {
            deleteProjectFile.delete(
                PROJECT_ID,
                FILE_ID
            )
        }
    }

    @Test
    fun `should delete project file when there is no problem`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID)
        every { filePersistence.getFileMetadata(FILE_ID) } returns fileMetadata(ZonedDateTime.now().plusDays(1))
        every { filePersistence.getFileCategoryTypeSet(FILE_ID) } returns setOf(ProjectFileCategoryType.APPLICATION)
        every { filePersistence.deleteFile(PROJECT_ID, FILE_ID, FILE_NAME) } returns Unit
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        deleteProjectFile.delete(PROJECT_ID, FILE_ID)

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_FILE_DELETED)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "document $FILE_NAME deleted from project application $PROJECT_ID by $USER_ID"
        )
    }

    @Test
    fun `should delete assessment file when there is no problem`() {
        val auditSlot = slot<AuditCandidateEvent>()
        every { projectPersistence.throwIfNotExists(PROJECT_ID) } returns Unit
        every { projectPersistence.getProject(PROJECT_ID) } returns projectWithId(PROJECT_ID)
        every { filePersistence.getFileMetadata(FILE_ID) } returns fileMetadata(ZonedDateTime.now().minusDays(10))
        every { filePersistence.getFileCategoryTypeSet(FILE_ID) } returns setOf(ProjectFileCategoryType.ASSESSMENT)
        every { filePersistence.deleteFile(PROJECT_ID, FILE_ID, FILE_NAME) } returns Unit
        every { auditPublisher.publishEvent(capture(auditSlot)) } returns Unit

        deleteProjectFile.delete(PROJECT_ID, FILE_ID)

        assertThat(auditSlot.captured.auditCandidate.action).isEqualTo(AuditAction.PROJECT_FILE_DELETED)
        assertThat(auditSlot.captured.auditCandidate.description).isEqualTo(
            "document $FILE_NAME deleted from project application $PROJECT_ID by $USER_ID"
        )
    }
}
