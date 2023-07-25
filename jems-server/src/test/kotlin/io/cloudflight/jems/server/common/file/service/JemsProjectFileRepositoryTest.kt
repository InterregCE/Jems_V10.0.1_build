package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.PaymentAdvanceAttachment
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.PaymentAttachment
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime
import java.util.Optional

class JemsProjectFileRepositoryTest : UnitTest() {

    companion object {
        const val USER_ID = 9678L
        const val PROJECT_ID = 2475L

        private fun file(name: String = "new_file.txt", type: JemsFileType) = JemsFileCreate(
            projectId = PROJECT_ID,
            partnerId = null,
            name = name,
            path = "/our/indexed/path/",
            type = type,
            size = 45L,
            content = mockk(),
            userId = USER_ID,
        )

        private fun project(): ProjectEntity {
            val p = mockk<ProjectEntity>()

            every { p.id } returns PROJECT_ID
            every { p.customIdentifier } returns "custom-id"
            every { p.call.id } returns 1L
            every { p.call.name } returns "callName"
            every { p.acronym } returns "acronym"
            every { p.currentStatus.status } returns ApplicationStatus.DRAFT
            every { p.firstSubmission } returns null
            every { p.lastResubmission } returns null
            every { p.priorityPolicy } returns null

            return p
        }

        private fun expectedFile(type: JemsFileType) = JemsFile(
            id = 0L,
            name = "new_file.txt",
            type = type,
            uploaded = ZonedDateTime.now(),
            author = UserSimple(9678L, email = "email", name = "name-user", surname = "surname-user"),
            size = 45L,
            description = "",
            indexedPath = "/our/indexed/path/",
        )
    }

    @MockK
    lateinit var reportFileRepository: JemsFileMetadataRepository
    @MockK
    lateinit var minioStorage: MinioStorage
    @MockK
    lateinit var userRepository: UserRepository
    @MockK
    lateinit var projectRepository: ProjectRepository
    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var service: JemsProjectFileService

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportFileRepository, minioStorage, userRepository, projectRepository, auditPublisher)
    }

    @ParameterizedTest(name = "persistProjectFileAndPerformAction (type {0})")
    @EnumSource(value = JemsFileType::class, names = [
        "PaymentAttachment",
        "PaymentAdvanceAttachment",

        "ProjectReport",
        "ProjectResult",
        "ActivityProjectReport",
        "DeliverableProjectReport",
        "OutputProjectReport",

        "PartnerReport",
        "Activity",
        "Deliverable",
        "Output",
        "Expenditure",
        "ProcurementAttachment",
        "ProcurementGdprAttachment",
        "Contribution",

        "ControlDocument",
        "ControlCertificate",
        "ControlReport",
        "Contract",
        "ContractDoc",
        "ContractPartnerDoc",
        "ContractInternal",

        "SharedFolder"
    ])
    fun persistProjectFileAndPerformAction(type: JemsFileType) {
        val expectedBucket = if (setOf(PaymentAttachment, PaymentAdvanceAttachment).contains(type)) "payment" else "application"

        every { minioStorage.saveFile(any(), any(), any(), any(), true) } returns Unit

        val userEntity = mockk<UserEntity>()
        every { userEntity.id } returns USER_ID
        every { userEntity.email } returns "email"
        every { userEntity.name } returns "name-user"
        every { userEntity.surname } returns "surname-user"
        every { userRepository.getById(USER_ID) } returns userEntity

        val slotFileEntity = slot<JemsFileMetadataEntity>()
        every { reportFileRepository.save(capture(slotFileEntity)) } returnsArgument 0

        every { projectRepository.getById(PROJECT_ID) } returns project()
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }

        val file = file(type = type)
        var additionalStepInvoked = false
        val result = service.persistFileAndPerformAction(file) { additionalStepInvoked = true }
        assertThat(result).isEqualTo(expectedFile(type).copy(uploaded = slotFileEntity.captured.uploaded))

        verify(exactly = 1) { minioStorage.saveFile(expectedBucket, "/our/indexed/path/new_file.txt", any(), any(), true) }

        assertThat(slotFileEntity.captured.projectId).isEqualTo(PROJECT_ID)
        assertThat(slotFileEntity.captured.partnerId).isNull()
        assertThat(slotFileEntity.captured.path).isEqualTo("/our/indexed/path/")
        assertThat(slotFileEntity.captured.minioBucket).isEqualTo(expectedBucket)
        assertThat(slotFileEntity.captured.minioLocation).isEqualTo("/our/indexed/path/new_file.txt")
        assertThat(slotFileEntity.captured.name).isEqualTo("new_file.txt")
        assertThat(slotFileEntity.captured.type).isEqualTo(type)
        assertThat(slotFileEntity.captured.size).isEqualTo(45L)
        assertThat(slotFileEntity.captured.user).isEqualTo(userEntity)
        assertThat(slotFileEntity.captured.description).isEmpty()

        assertTrue(additionalStepInvoked)
        assertThat(auditSlot.captured.auditCandidate).isEqualTo(AuditCandidate(
            action = AuditAction.PROJECT_FILE_UPLOADED_SUCCESSFULLY,
            project = AuditProject(PROJECT_ID.toString(), "custom-id", "acronym"),
            entityRelatedId = 0L /* because DB mocked */,
            description = "File (of type ${type.name}) \"new_file.txt\" has been uploaded to /our/indexed/path/new_file.txt",
        ))
    }

    @ParameterizedTest(name = "persistProjectFileAndPerformAction wrong type (type {0})")
    @EnumSource(value = JemsFileType::class, names = [
        "PaymentAttachment",
        "PaymentAdvanceAttachment",

        "ProjectReport",
        "ProjectResult",
        "ActivityProjectReport",
        "DeliverableProjectReport",
        "OutputProjectReport",

        "PartnerReport",
        "Activity",
        "Deliverable",
        "Output",
        "Expenditure",
        "ProcurementAttachment",
        "ProcurementGdprAttachment",
        "Contribution",

        "ControlDocument",
        "ControlCertificate",
        "ControlReport",
        "Contract",
        "ContractDoc",
        "ContractPartnerDoc",
        "ContractInternal",

        "SharedFolder"
    ], mode = EnumSource.Mode.EXCLUDE)
    fun `persistProjectFileAndPerformAction wrong type`(type: JemsFileType) {
        val file = file(type = type)
        assertThrows<WrongFileTypeException> {
            service.persistFileAndPerformAction(file) { }
        }
    }

    @Test
    fun setDescription() {
        val file = JemsFileMetadataEntity(
            id = 85L,
            projectId = PROJECT_ID,
            partnerId = null,
            path = "",
            minioBucket = "",
            minioLocation = "",
            name = "word.docx",
            type = JemsFileType.ContractInternal,
            size = 400L,
            user = mockk(),
            uploaded = ZonedDateTime.now(),
            description = "old desc",
        )
        every { reportFileRepository.findById(85L) } returns Optional.of(file)
        every { projectRepository.getById(PROJECT_ID) } returns project()
        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers { }

        service.setDescription(85L, "new desc")
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
        val file = JemsFileMetadataEntity(
            id = 96L,
            projectId = PROJECT_ID,
            partnerId = null,
            path = "",
            minioBucket = "file-bucket",
            minioLocation = "/sample/location",
            name = "powerpoint.pptx",
            type = JemsFileType.Deliverable,
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

        service.delete(file)

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
