package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.dto.FileMetadata
import io.cloudflight.ems.entity.*
import io.cloudflight.ems.exception.DuplicateFileException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.UserRepository
import io.cloudflight.ems.repository.MinioStorage
import io.cloudflight.ems.repository.ProjectFileRepository
import io.cloudflight.ems.repository.ProjectRepository
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Optional
import java.util.stream.Collectors

const val PROJECT_ID = 612L

class FileStorageServiceTest {

    private val UNPAGED = Pageable.unpaged()

    private val TEST_DATE = LocalDate.of(2020, 6, 10)
    private val TEST_DATE_TIME = ZonedDateTime.of(TEST_DATE, LocalTime.of(16, 0), ZoneId.of("Europe/Bratislava"))

    private val user = OutputUser(
        id = 34,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "ADMIN")
    )

    private val account = User(
        id = 34,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRole(id = 1, name = "ADMIN"),
        password = "hash_pass"
    )
    private val testProject = Project(id = PROJECT_ID, submissionDate = TEST_DATE, applicant = account, acronym = "test project")

    @MockK
    lateinit var auditService: AuditService
    @MockK
    lateinit var userRepository: UserRepository
    @MockK
    lateinit var projectRepository: ProjectRepository
    @MockK
    lateinit var projectFileRepository: ProjectFileRepository
    @MockK
    lateinit var securityService: SecurityService
    @MockK
    lateinit var minioStorage: MinioStorage

    lateinit var fileStorageService: FileStorageService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(user, "hash_pass", emptyList())
        fileStorageService = FileStorageServiceImpl(
            auditService, minioStorage, projectFileRepository, projectRepository, userRepository, securityService)
    }

    @Test
    fun save_duplicate() {
        val fileMetadata = FileMetadata(projectId = PROJECT_ID, name = "proj-file-1.png", size = 0)
        every { projectFileRepository.findFirstByProjectIdAndName(eq(PROJECT_ID), eq("proj-file-1.png")) } returns Optional.of(dummyProjectFile())

        val exception = assertThrows<DuplicateFileException> { fileStorageService.saveFile(InputStream.nullInputStream(), fileMetadata) }

        val expected = DuplicateFileException(PROJECT_ID, "proj-file-1.png", TEST_DATE_TIME)
        assertEquals(expected.error, exception.error)
    }

    @Test
    fun save_projectNotExists() {
        val fileMetadata = FileMetadata(projectId = PROJECT_ID, name = "", size = 0)
        every { projectFileRepository.findFirstByProjectIdAndName(eq(PROJECT_ID), any()) } returns Optional.empty()
        every { projectRepository.findById(eq(PROJECT_ID)) } returns Optional.empty()

        assertThrows<ResourceNotFoundException> { fileStorageService.saveFile(InputStream.nullInputStream(), fileMetadata) }
    }

    @Test
    fun save_userNotExists() {
        val fileMetadata = FileMetadata(projectId = PROJECT_ID, name = "", size = 0)
        every { projectFileRepository.findFirstByProjectIdAndName(eq(PROJECT_ID), any()) } returns Optional.empty()
        every { projectRepository.findById(eq(PROJECT_ID)) } returns Optional.of(testProject)
        every { userRepository.findById(any()) } returns Optional.empty()

        assertThrows<ResourceNotFoundException> { fileStorageService.saveFile(InputStream.nullInputStream(), fileMetadata) }
    }

    @Test
    fun save() {
        val streamToSave = "test".toByteArray().inputStream()
        val fileMetadata = FileMetadata(projectId = PROJECT_ID, name = "proj-file-1.png", size = "test".length.toLong())
        every { projectFileRepository.findFirstByProjectIdAndName(eq(PROJECT_ID), any()) } returns Optional.empty()

        val projectFileSlot = slot<ProjectFile>()
        every { projectFileRepository.save(capture(projectFileSlot)) } returnsArgument 0

        val bucketSlot = slot<String>()
        val identifierSlot = slot<String>()
        val sizeSlot = slot<Long>()
        val streamSlot = slot<InputStream>()
        every { minioStorage.saveFile(capture(bucketSlot), capture(identifierSlot), capture(sizeSlot), capture(streamSlot)) } answers {}

        every { projectRepository.findById(eq(PROJECT_ID)) } returns Optional.of(testProject)
        every { userRepository.findById(eq(34)) } returns Optional.of(account)

        fileStorageService.saveFile(streamToSave, fileMetadata)

        with (projectFileSlot.captured) {
            assertEquals(null, id)
            assertEquals(PROJECT_FILES_BUCKET, bucket)
            assertEquals("project-$PROJECT_ID/proj-file-1.png", identifier)
            assertEquals("proj-file-1.png", name)
            assertEquals(PROJECT_ID, project.id)
            assertEquals(34, author.id)
            assertEquals(null, description)
            assertEquals("test".length.toLong(), size)
        }
        assertEquals(PROJECT_FILES_BUCKET, bucketSlot.captured)
        assertEquals("project-$PROJECT_ID/proj-file-1.png", identifierSlot.captured)
        assertEquals("test".length.toLong(), sizeSlot.captured)
        assertEquals(streamToSave, streamSlot.captured)
    }

    @Test
    fun downloadFile_notExisting() {
        every { projectFileRepository.findFirstByProjectIdAndId(eq(-1), eq(100)) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { fileStorageService.downloadFile(-1, 100) }
    }

    @Test
    fun downloadFile() {
        val byteArray = "test-content".toByteArray()
        every { projectFileRepository.findFirstByProjectIdAndId(eq(PROJECT_ID), eq(10)) } returns Optional.of(dummyProjectFile())
        every { minioStorage.getFile(eq(PROJECT_FILES_BUCKET), eq("project-1/proj-file-1.png")) } returns byteArray

        val result = fileStorageService.downloadFile(PROJECT_ID, 10)

        assertEquals("proj-file-1.png", result.first)
        assertEquals("test-content".length, result.second.size)
    }

    @Test
    fun getFilesForProject_OK() {
        val files = listOf(dummyProjectFile())
        every { projectFileRepository.findAllByProjectId(eq(PROJECT_ID), UNPAGED) } returns PageImpl(files)

        val result = fileStorageService.getFilesForProject(PROJECT_ID, UNPAGED)

        assertEquals(1, result.totalElements)

        assertEquals(dummyOutputProjectFile(), result.get().collect(Collectors.toList()).get(0))
    }

    @Test
    fun getFilesForProject_empty() {
        every { projectFileRepository.findAllByProjectId(eq(311), UNPAGED) } returns PageImpl(listOf())

        val result = fileStorageService.getFilesForProject(311, UNPAGED)

        assertEquals(0, result.totalElements)
    }

    @Test
    fun setDescription_notExisting() {
        every { projectFileRepository.findFirstByProjectIdAndId(eq(-1), eq(100)) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {
            fileStorageService.setDescription(-1, 100, null)
        }
    }

    @Test
    fun setDescription_null() {
        val project = dummyProjectFile()
        project.description = "old_description"
        every { projectFileRepository.findFirstByProjectIdAndId(eq(PROJECT_ID), eq(10)) } returns Optional.of(project)

        val projectFile = slot<ProjectFile>()
        every { projectFileRepository.save(capture(projectFile)) } returnsArgument 0

        val result = fileStorageService.setDescription(PROJECT_ID, 10, null)

        assertEquals(null, projectFile.captured.description)
        assertEquals(null, result.description)
    }

    @Test
    fun setDescription_new() {
        val project = dummyProjectFile()
        project.description = null
        every { projectFileRepository.findFirstByProjectIdAndId(eq(PROJECT_ID), eq(10)) } returns Optional.of(project)

        val projectFile = slot<ProjectFile>()
        every { projectFileRepository.save(capture(projectFile)) } returnsArgument 0

        val result = fileStorageService.setDescription(PROJECT_ID, 10, "new_description")

        assertEquals("new_description", projectFile.captured.description)
        assertEquals("new_description", result.description)
    }

    @Test
    fun deleteFile_notExisting() {
        every { projectFileRepository.findFirstByProjectIdAndId(eq(-1), eq(100)) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {
            fileStorageService.deleteFile(-1, 100)
        }
    }

    @Test
    fun deleteFile() {
        val bucket = slot<String>()
        val identifier = slot<String>()
        val projectFile = slot<ProjectFile>()

        every { projectFileRepository.findFirstByProjectIdAndId(eq(PROJECT_ID), eq(10)) } returns Optional.of(dummyProjectFile())
        every { minioStorage.deleteFile(capture(bucket), capture(identifier)) } answers {}
        every { projectFileRepository.delete(capture(projectFile)) } answers {}
        every { auditService.logEvent(any()) } answers {} // doNothing

        fileStorageService.deleteFile(PROJECT_ID, 10)

        assertEquals(PROJECT_FILES_BUCKET, bucket.captured)
        assertEquals("project-1/proj-file-1.png", identifier.captured)
        assertEquals(dummyProjectFile(), projectFile.captured)

        val auditEvent = slot<Audit>()
        verify { auditService.logEvent(capture(auditEvent)) }
        with(auditEvent) {
            assertEquals(testProject.id, PROJECT_ID)
            assertEquals(AuditAction.PROJECT_FILE_DELETE, captured.action)
            assertEquals(34, captured.user?.id)
            assertEquals("admin@admin.dev", captured.user?.email)
            assertEquals("document proj-file-1.png deleted from application 612", captured.description)
        }
    }

    private fun dummyProjectFile(): ProjectFile {
        return ProjectFile(
            id = 1,
            bucket = PROJECT_FILES_BUCKET,
            identifier = "project-1/proj-file-1.png",
            name = "proj-file-1.png",
            project = testProject,
            author = account,
            description = "",
            size = 2,
            updated = TEST_DATE_TIME
        )
    }

    private fun dummyOutputProjectFile(): OutputProjectFile {
        return OutputProjectFile(
            id = 1,
            name = "proj-file-1.png",
            author = user,
            description = "",
            size = 2,
            updated = TEST_DATE_TIME
        )
    }

}
