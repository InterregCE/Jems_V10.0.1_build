package io.cloudflight.jems.server.project.service.report.project.annexes.upload

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.repository.ProjectNotFoundException
import io.cloudflight.jems.server.project.repository.file.ProjectFileTypeNotSupported
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.project.annexes.ProjectReportAnnexesFilePersistence
import io.cloudflight.jems.server.user.repository.user.UserNotFound
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream
import java.util.Collections

class UploadProjectReportAnnexesFileTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 1L
        private const val REPORT_ID = 2L
        private const val USER_ID = 4L
        private const val expectedPath = "Project/000001/Report/ProjectReport/000002/"
        private const val fileName = "attachment.pdf"

        private val currentUser = LocalCurrentUser(
            User(
                USER_ID, "admin@test.net", "test", "test",
                UserRole(1, "Role", emptySet()),
                userStatus = UserStatus.ACTIVE
            ), "", Collections.emptyList()
        )
        private val invalidUser = LocalCurrentUser(
            User(
                -1L, "admin@test.net", "test", "test",
                UserRole(1, "Role", emptySet()),
                userStatus = UserStatus.ACTIVE
            ), "", Collections.emptyList()
        )
        private val content = mockk<InputStream>()
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var userPersistence: UserPersistence

    @MockK
    lateinit var filePersistence: JemsFilePersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectReportFilePersistence: ProjectReportAnnexesFilePersistence

    @InjectMockKs
    lateinit var interactor: UploadProjectReportAnnexesFile

    @Test
    fun `should successfully upload a file to the project report annexes`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID, any()) } returns Unit
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { userPersistence.throwIfNotExists(USER_ID) } returns Unit
        every { filePersistence.existsFile(expectedPath, fileName) } returns false
        val fileToAdd = slot<JemsFileCreate>()
        val mockResult = mockk<JemsFileMetadata>()
        every { projectReportFilePersistence.saveFile(capture(fileToAdd)) } returns mockResult

        val file = ProjectFile(
            stream = content,
            name = fileName,
            size = 5L
        )

        assertThat(interactor.upload(PROJECT_ID, REPORT_ID, file)).isEqualTo(mockResult)
        assertThat(fileToAdd.captured).isEqualTo(
            JemsFileCreate(
                projectId = PROJECT_ID,
                partnerId = null,
                name = fileName,
                path = expectedPath,
                type = JemsFileType.ProjectReport,
                size = 5L,
                content = content,
                userId = USER_ID
            )
        )
    }

    @Test
    fun `should throw ProjectNotFoundException when the project does not exist`() {
        every { projectPersistence.throwIfNotExists(-1L, any()) } throws ProjectNotFoundException()

        val file = mockk<ProjectFile>()
        every { file.name } returns fileName

        assertThrows<ProjectNotFoundException> {
            interactor.upload(-1L, REPORT_ID, file)
        }
    }

    @Test
    fun `should throw UserNotFound when the current user cannot be validated`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID, any()) } returns Unit
        every { securityService.currentUser } returns invalidUser
        every { securityService.getUserIdOrThrow() } returns -1L
        every { userPersistence.throwIfNotExists(-1L) } throws UserNotFound()

        val file = mockk<ProjectFile>()
        every { file.name } returns fileName

        assertThrows<UserNotFound> {
            interactor.upload(PROJECT_ID, REPORT_ID, file)
        }
    }

    @Test
    fun `should throw FileAlreadyExists when the file has already been previously uploaded`() {
        every { projectPersistence.throwIfNotExists(PROJECT_ID, any()) } returns Unit
        every { securityService.currentUser } returns currentUser
        every { securityService.getUserIdOrThrow() } returns USER_ID
        every { userPersistence.throwIfNotExists(any()) } returns Unit
        every { filePersistence.existsFile(expectedPath, fileName) } returns true

        val file = mockk<ProjectFile>()
        every { file.name } returns fileName

        assertThrows<FileAlreadyExists> {
            interactor.upload(PROJECT_ID, REPORT_ID, file)
        }
    }

    @Test
    fun `should throw ProjectFileTypeNotSupported when the file type is not valid`() {
        val file = mockk<ProjectFile>()
        every { file.name } returns "invalid.exe"

        assertThrows<ProjectFileTypeNotSupported> {
            interactor.upload(PROJECT_ID, REPORT_ID, file)
        }
    }
}
