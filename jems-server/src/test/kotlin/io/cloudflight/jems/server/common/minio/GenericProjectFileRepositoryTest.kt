package io.cloudflight.jems.server.common.minio

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime
import java.util.Optional

class GenericProjectFileRepositoryTest : UnitTest() {

    companion object {
        const val USER_ID = 9678L
        const val PROJECT_ID = 2475L

        private fun file(name: String = "new_file.txt") = ProjectReportFileCreate(
            projectId = PROJECT_ID,
            partnerId = null,
            name = name,
            path = "/our/indexed/path/",
            type = ProjectPartnerReportFileType.ContractDoc,
            size = 45L,
            content = mockk(),
            userId = USER_ID,
        )

        private fun project(): ProjectEntity {
            val p = mockk<ProjectEntity>()

            every { p.id } returns PROJECT_ID
            every { p.customIdentifier } returns "custom-id"
            every { p.call.name } returns "callName"
            every { p.acronym } returns "acronym"
            every { p.currentStatus.status } returns ApplicationStatus.DRAFT
            every { p.firstSubmission } returns null
            every { p.lastResubmission } returns null
            every { p.priorityPolicy } returns null

            return p
        }
    }

    @MockK
    lateinit var reportFileRepository: ProjectReportFileRepository
    @MockK
    lateinit var minioStorage: MinioStorage
    @MockK
    lateinit var userRepository: UserRepository
    @MockK
    lateinit var projectRepository: ProjectRepository
    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var repository: GenericProjectFileRepository

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportFileRepository, minioStorage, userRepository, projectRepository, auditPublisher)
    }

    @Test
    fun persistProjectFileAndPerformAction() {
        every { minioStorage.saveFile(any(), any(), any(), any(), true) } returns Unit

        val userEntity = mockk<UserEntity>()
        every { userRepository.getById(USER_ID) } returns userEntity

        val slotFileEntity = slot<ReportProjectFileEntity>()
        every { reportFileRepository.save(capture(slotFileEntity)) } returnsArgument 0

        every { projectRepository.getById(PROJECT_ID) } returns project()
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }

        val file = file()
        var additionalStepInvoked = false
        repository.persistProjectFileAndPerformAction(file, "/minio/location", { additionalStepInvoked = true })

        verify(exactly = 1) { minioStorage.saveFile("project-report", "/minio/location", any(), any(), true) }

        assertThat(slotFileEntity.captured.projectId).isEqualTo(PROJECT_ID)
        assertThat(slotFileEntity.captured.partnerId).isNull()
        assertThat(slotFileEntity.captured.path).isEqualTo("/our/indexed/path/")
        assertThat(slotFileEntity.captured.minioBucket).isEqualTo("project-report")
        assertThat(slotFileEntity.captured.minioLocation).isEqualTo("/minio/location")
        assertThat(slotFileEntity.captured.name).isEqualTo("new_file.txt")
        assertThat(slotFileEntity.captured.type).isEqualTo(ProjectPartnerReportFileType.ContractDoc)
        assertThat(slotFileEntity.captured.size).isEqualTo(45L)
        assertThat(slotFileEntity.captured.user).isEqualTo(userEntity)
        assertThat(slotFileEntity.captured.description).isEmpty()

        assertThat(additionalStepInvoked).isTrue()
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PROJECT_FILE_UPLOADED_SUCCESSFULLY,
            project = AuditProject(PROJECT_ID.toString(), "custom-id", "acronym"),
            entityRelatedId = 0L /* because DB mocked */,
            description = "File (of type ContractDoc) \"new_file.txt\" has been uploaded to /minio/location",
        ))
    }

    @Test
    fun setDescription() {
        val file = ReportProjectFileEntity(
            id = 85L,
            projectId = PROJECT_ID,
            partnerId = null,
            path = "",
            minioBucket = "",
            minioLocation = "",
            name = "word.docx",
            type = ProjectPartnerReportFileType.ContractInternal,
            size = 400L,
            user = mockk(),
            uploaded = ZonedDateTime.now(),
            description = "old desc",
        )
        every { reportFileRepository.findById(85L) } returns Optional.of(file)
        every { projectRepository.getById(PROJECT_ID) } returns project()
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }

        repository.setDescription(85L, "new desc")
        assertThat(file.description).isEqualTo("new desc")
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PROJECT_FILE_DESCRIPTION_CHANGED,
            project = AuditProject(PROJECT_ID.toString(), "custom-id", "acronym"),
            entityRelatedId = 85L,
            description = "Description of file \"word.docx\" uploaded to  has changed from \"old desc\" to \"new desc\"",
        ))
    }

    @Test
    fun delete() {
        val file = ReportProjectFileEntity(
            id = 96L,
            projectId = PROJECT_ID,
            partnerId = null,
            path = "",
            minioBucket = "file-bucket",
            minioLocation = "/sample/location",
            name = "powerpoint.pptx",
            type = ProjectPartnerReportFileType.Deliverable,
            size = 324L,
            user = mockk(),
            uploaded = ZonedDateTime.now(),
            description = "",
        )

        every { minioStorage.deleteFile("file-bucket", "/sample/location") } answers { }
        every { reportFileRepository.delete(file) } answers { }
        every { projectRepository.getById(PROJECT_ID) } returns project()
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }

        repository.delete(file)

        verify(exactly = 1) { minioStorage.deleteFile("file-bucket", "/sample/location") }
        verify(exactly = 1) { reportFileRepository.delete(file) }
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PROJECT_FILE_DELETED,
            project = AuditProject(PROJECT_ID.toString(), "custom-id", "acronym"),
            entityRelatedId = 96L,
            description = "/sample/location",
        ))
    }

}
