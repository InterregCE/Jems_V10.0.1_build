package io.cloudflight.jems.server.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.project.dto.file.OutputProjectFile
import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.CircularEconomy
import io.cloudflight.jems.api.programme.dto.strategy.ProgrammeStrategy
import io.cloudflight.jems.server.project.entity.file.FileMetadata
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.file.ProjectFile
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.common.exception.DuplicateFileException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.repository.ProjectFileRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.service.file.FileStorageService
import io.cloudflight.jems.server.project.service.file.FileStorageServiceImpl
import io.cloudflight.jems.server.project.service.file.PROJECT_FILES_BUCKET
import io.cloudflight.jems.server.programme.entity.ProgrammeStrategyEntity
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserRole
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
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

    private val user = User(
        id = 34,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRole(id = 1, name = "ADMIN", permissions = emptySet())
    )

    private val userWithoutRole = OutputUser(
        id = user.id,
        email = user.email,
        name = user.name,
        surname = user.surname
    )

    private val account = UserEntity(
        id = 34,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = UserRoleEntity(id = 1, name = "ADMIN"),
        password = "hash_pass"
    )
    private val dummyCall = CallEntity(
        id = 5,
        creator = account,
        name = "call",
        prioritySpecificObjectives = mutableSetOf(ProgrammeSpecificObjectiveEntity(CircularEconomy, "CE")),
        strategies = mutableSetOf(ProgrammeStrategyEntity(ProgrammeStrategy.SeaBasinStrategyArcticOcean, true)),
        isAdditionalFundAllowed = false,
        funds = mutableSetOf(),
        startDate = ZonedDateTime.now(),
        endDateStep1 = null,
        endDate = ZonedDateTime.now(),
        status = CallStatus.PUBLISHED,
        lengthOfPeriod = 1,
        applicationFormFieldConfigurationEntities = mutableSetOf()
    )
    private val testProject = ProjectEntity(
        id = PROJECT_ID, call = dummyCall, applicant = account, acronym = "test project",
        currentStatus = ProjectStatusHistoryEntity(
            status = ApplicationStatus.DRAFT,
            user = account,
            updated = ZonedDateTime.now()
        ),
    )

    @RelaxedMockK
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
            auditService, minioStorage, projectFileRepository, projectRepository, userRepository, securityService
        )
    }

    @Test
    fun save_duplicate() {
        val fileMetadata = FileMetadata(
            projectId = PROJECT_ID,
            name = "proj-file-1.png",
            size = 0,
            type = ProjectFileType.APPLICANT_FILE
        )
        every {
            projectFileRepository.findFirstByProjectIdAndNameAndType(
                eq(PROJECT_ID),
                eq("proj-file-1.png"),
                eq(ProjectFileType.APPLICANT_FILE)
            )
        } returns Optional.of(dummyProjectFile())

        val exception = assertThrows<DuplicateFileException> {
            fileStorageService.saveFile(
                InputStream.nullInputStream(),
                fileMetadata
            )
        }

        val expected = DuplicateFileException(PROJECT_ID, "proj-file-1.png", TEST_DATE_TIME)
        assertEquals(expected.error, exception.error)

        val auditEvent = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditEvent)) }
        with(auditEvent) {
            assertEquals(testProject.id, PROJECT_ID)
            assertEquals(AuditAction.PROJECT_FILE_UPLOAD_FAILED, captured.action)
            assertEquals("FAILED upload of document proj-file-1.png to project application 612", captured.description)
        }
    }

    @Test
    fun save_projectNotExists() {
        val fileMetadata =
            FileMetadata(projectId = PROJECT_ID, name = "", size = 0, type = ProjectFileType.APPLICANT_FILE)
        every {
            projectFileRepository.findFirstByProjectIdAndNameAndType(
                eq(PROJECT_ID),
                any(),
                eq(ProjectFileType.APPLICANT_FILE)
            )
        } returns Optional.empty()
        every { projectRepository.findById(eq(PROJECT_ID)) } returns Optional.empty()

        assertThrows<ResourceNotFoundException> {
            fileStorageService.saveFile(
                InputStream.nullInputStream(),
                fileMetadata
            )
        }
    }

    @Test
    fun save_userNotExists() {
        val fileMetadata =
            FileMetadata(projectId = PROJECT_ID, name = "", size = 0, type = ProjectFileType.APPLICANT_FILE)
        every {
            projectFileRepository.findFirstByProjectIdAndNameAndType(
                eq(PROJECT_ID),
                any(),
                eq(ProjectFileType.APPLICANT_FILE)
            )
        } returns Optional.empty()
        every { projectRepository.findById(eq(PROJECT_ID)) } returns Optional.of(testProject)
        every { userRepository.findById(any()) } returns Optional.empty()

        assertThrows<ResourceNotFoundException> {
            fileStorageService.saveFile(
                InputStream.nullInputStream(),
                fileMetadata
            )
        }
    }

    @Test
    fun save() {
        val streamToSave = "test".toByteArray().inputStream()
        val fileMetadata = FileMetadata(
            projectId = PROJECT_ID,
            name = "proj-file-1.png",
            size = "test".length.toLong(),
            type = ProjectFileType.APPLICANT_FILE
        )
        every {
            projectFileRepository.findFirstByProjectIdAndNameAndType(
                eq(PROJECT_ID),
                any(),
                eq(ProjectFileType.APPLICANT_FILE)
            )
        } returns Optional.empty()

        val projectFileSlot = slot<ProjectFile>()
        every { projectFileRepository.save(capture(projectFileSlot)) } returnsArgument 0

        val bucketSlot = slot<String>()
        val identifierSlot = slot<String>()
        val sizeSlot = slot<Long>()
        val streamSlot = slot<InputStream>()
        every {
            minioStorage.saveFile(
                capture(bucketSlot),
                capture(identifierSlot),
                capture(sizeSlot),
                capture(streamSlot)
            )
        } answers {}

        every { projectRepository.findById(eq(PROJECT_ID)) } returns Optional.of(testProject)
        every { userRepository.findById(eq(34)) } returns Optional.of(account)

        fileStorageService.saveFile(streamToSave, fileMetadata)

        with(projectFileSlot.captured) {
            assertEquals(0, id)
            assertEquals(PROJECT_FILES_BUCKET, bucket)
            assertEquals("project-$PROJECT_ID/${ProjectFileType.APPLICANT_FILE}/proj-file-1.png", identifier)
            assertEquals("proj-file-1.png", name)
            assertEquals(PROJECT_ID, project.id)
            assertEquals(34, author.id)
            assertEquals(null, description)
            assertEquals("test".length.toLong(), size)
        }
        assertEquals(PROJECT_FILES_BUCKET, bucketSlot.captured)
        assertEquals("project-$PROJECT_ID/${ProjectFileType.APPLICANT_FILE}/proj-file-1.png", identifierSlot.captured)
        assertEquals("test".length.toLong(), sizeSlot.captured)
        assertEquals(streamToSave, streamSlot.captured)

        val auditEvent = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditEvent)) }
        with(auditEvent) {
            assertEquals(testProject.id, PROJECT_ID)
            assertEquals(AuditAction.PROJECT_FILE_UPLOADED_SUCCESSFULLY, captured.action)
            assertEquals("document proj-file-1.png uploaded to project application 612", captured.description)
        }
    }

    @Test
    fun downloadFile_notExisting() {
        every { projectFileRepository.findFirstByProjectIdAndIdAndType(eq(-1), eq(100), ProjectFileType.ASSESSMENT_FILE) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { fileStorageService.downloadFile(-1, 100, ProjectFileType.ASSESSMENT_FILE) }
    }

    @Test
    fun downloadFile() {
        val byteArray = "test-content".toByteArray()
        every { projectFileRepository.findFirstByProjectIdAndIdAndType(eq(PROJECT_ID), eq(10), ProjectFileType.APPLICANT_FILE) } returns Optional.of(
            dummyProjectFile()
        )
        every { minioStorage.getFile(eq(PROJECT_FILES_BUCKET), eq("project-1/proj-file-1.png")) } returns byteArray

        val result = fileStorageService.downloadFile(PROJECT_ID, 10, ProjectFileType.APPLICANT_FILE)

        assertEquals("proj-file-1.png", result.first)
        assertEquals("test-content".length, result.second.size)
    }

    @Test
    fun getFilesForProject_OK() {
        val files = listOf(dummyProjectFile())
        every {
            projectFileRepository.findAllByProjectIdAndType(
                eq(PROJECT_ID),
                eq(ProjectFileType.APPLICANT_FILE),
                UNPAGED
            )
        } returns PageImpl(files)

        val result = fileStorageService.getFilesForProject(PROJECT_ID, ProjectFileType.APPLICANT_FILE, UNPAGED)

        assertEquals(1, result.totalElements)

        assertEquals(dummyOutputProjectFile(), result.get().collect(Collectors.toList()).get(0))
    }

    @Test
    fun getFilesForProject_empty() {
        every {
            projectFileRepository.findAllByProjectIdAndType(
                eq(311),
                eq(ProjectFileType.APPLICANT_FILE),
                UNPAGED
            )
        } returns PageImpl(listOf())

        val result = fileStorageService.getFilesForProject(311, ProjectFileType.APPLICANT_FILE, UNPAGED)

        assertEquals(0, result.totalElements)
    }

    @Test
    fun setDescription_notExisting() {
        every { projectFileRepository.findFirstByProjectIdAndIdAndType(eq(-1), eq(100), ProjectFileType.APPLICANT_FILE) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {
            fileStorageService.setDescription(-1, 100, ProjectFileType.APPLICANT_FILE,null)
        }
    }

    @Test
    fun setDescription_null() {
        val project = dummyProjectFile()
        project.description = "old_description"
        every { projectFileRepository.findFirstByProjectIdAndIdAndType(eq(PROJECT_ID), eq(10), ProjectFileType.APPLICANT_FILE) } returns Optional.of(project)

        val projectFile = slot<ProjectFile>()
        every { projectFileRepository.save(capture(projectFile)) } returnsArgument 0

        val result = fileStorageService.setDescription(PROJECT_ID, 10, ProjectFileType.APPLICANT_FILE, null)

        assertEquals(null, projectFile.captured.description)
        assertEquals(null, result.description)

        val auditEvent = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditEvent)) }
        with(auditEvent) {
            assertEquals(testProject.id, PROJECT_ID)
            assertEquals(AuditAction.PROJECT_FILE_DESCRIPTION_CHANGED, captured.action)
            assertEquals(
                "description of document proj-file-1.png in project application 612 has changed from old_description to null",
                captured.description
            )
        }
    }

    @Test
    fun setDescription_new() {
        val project = dummyProjectFile(ProjectFileType.ASSESSMENT_FILE)
        project.description = null
        every { projectFileRepository.findFirstByProjectIdAndIdAndType(eq(PROJECT_ID), eq(10), ProjectFileType.ASSESSMENT_FILE) } returns Optional.of(project)

        val projectFile = slot<ProjectFile>()
        every { projectFileRepository.save(capture(projectFile)) } returnsArgument 0

        val result = fileStorageService.setDescription(PROJECT_ID, 10, ProjectFileType.ASSESSMENT_FILE, "new_description")

        assertEquals("new_description", projectFile.captured.description)
        assertEquals("new_description", result.description)

        val auditEvent = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditEvent)) }
        with(auditEvent) {
            assertEquals(testProject.id, PROJECT_ID)
            assertEquals(AuditAction.PROJECT_FILE_DESCRIPTION_CHANGED, captured.action)
            assertEquals(
                "description of document proj-file-1.png in project application 612 has changed from null to new_description",
                captured.description
            )
        }
    }

    @Test
    fun deleteFile_notExisting() {
        every { projectFileRepository.findFirstByProjectIdAndIdAndType(eq(-1), eq(100), ProjectFileType.ASSESSMENT_FILE) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {
            fileStorageService.deleteFile(-1, 100, ProjectFileType.ASSESSMENT_FILE)
        }
    }

    @Test
    fun `delete applicant file`() {
        val bucket = slot<String>()
        val identifier = slot<String>()
        val projectFile = slot<ProjectFile>()

        val file = dummyProjectFile().copy(updated = ZonedDateTime.now().plusDays(1))

        every { projectFileRepository.findFirstByProjectIdAndIdAndType(eq(PROJECT_ID), eq(10), ProjectFileType.APPLICANT_FILE) } returns
            Optional.of(file)
        every { minioStorage.deleteFile(capture(bucket), capture(identifier)) } answers {}
        every { projectFileRepository.delete(capture(projectFile)) } answers {}

        fileStorageService.deleteFile(PROJECT_ID, 10, ProjectFileType.APPLICANT_FILE)

        assertEquals(PROJECT_FILES_BUCKET, bucket.captured)
        assertEquals("project-1/proj-file-1.png", identifier.captured)
        assertEquals(file, projectFile.captured)

        val auditEvent = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditEvent)) }
        with(auditEvent) {
            assertEquals(testProject.id, PROJECT_ID)
            assertEquals(AuditAction.PROJECT_FILE_DELETED, captured.action)
            assertEquals("document proj-file-1.png deleted from application 612", captured.description)
        }
    }

    @Test
    fun `delete assessment file`() {
        val bucket = slot<String>()
        val identifier = slot<String>()
        val projectFile = slot<ProjectFile>()

        every { projectFileRepository.findFirstByProjectIdAndIdAndType(eq(PROJECT_ID), eq(10), ProjectFileType.ASSESSMENT_FILE) } returns
            Optional.of(dummyProjectFile(ProjectFileType.ASSESSMENT_FILE))
        every { minioStorage.deleteFile(capture(bucket), capture(identifier)) } answers {}
        every { projectFileRepository.delete(capture(projectFile)) } answers {}

        fileStorageService.deleteFile(PROJECT_ID, 10, ProjectFileType.ASSESSMENT_FILE)

        assertEquals(PROJECT_FILES_BUCKET, bucket.captured)
        assertEquals("project-1/proj-file-1.png", identifier.captured)
        assertEquals(dummyProjectFile(ProjectFileType.ASSESSMENT_FILE), projectFile.captured)

        val auditEvent = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(auditEvent)) }
        with(auditEvent) {
            assertEquals(testProject.id, PROJECT_ID)
            assertEquals(AuditAction.PROJECT_FILE_DELETED, captured.action)
            assertEquals("document proj-file-1.png deleted from application 612", captured.description)
        }
    }

    private fun dummyProjectFile(type: ProjectFileType = ProjectFileType.APPLICANT_FILE): ProjectFile {
        return ProjectFile(
            id = 1,
            bucket = PROJECT_FILES_BUCKET,
            identifier = "project-1/proj-file-1.png",
            name = "proj-file-1.png",
            project = testProject,
            author = account,
            type = type,
            description = "",
            size = 2,
            updated = TEST_DATE_TIME
        )
    }

    private fun dummyOutputProjectFile(): OutputProjectFile {
        return OutputProjectFile(
            id = 1,
            name = "proj-file-1.png",
            author = userWithoutRole,
            type = ProjectFileType.APPLICANT_FILE,
            description = "",
            size = 2,
            updated = TEST_DATE_TIME
        )
    }

}
