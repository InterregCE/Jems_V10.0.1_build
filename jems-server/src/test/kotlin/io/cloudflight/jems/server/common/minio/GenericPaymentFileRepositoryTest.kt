package io.cloudflight.jems.server.common.minio

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
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
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.time.ZonedDateTime

class GenericPaymentFileRepositoryTest : UnitTest() {

    companion object {
        const val USER_ID = 9672L
        const val PROJECT_ID = 2575L

        private fun file(name: String = "new_file.txt", type: ProjectPartnerReportFileType) = ProjectReportFileCreate(
            projectId = PROJECT_ID,
            partnerId = null,
            name = name,
            path = "/our/indexed/path/",
            type = type,
            size = 45L,
            content = mockk(),
            userId = USER_ID,
        )
    }

    @MockK
    lateinit var reportFileRepository: ProjectReportFileRepository
    @MockK
    lateinit var minioStorage: MinioStorage
    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    private lateinit var repository: GenericPaymentFileRepository

    @BeforeEach
    fun resetMocks() {
        clearMocks(reportFileRepository, minioStorage, userRepository)
    }

    @ParameterizedTest(name = "persistFile (type {0})")
    @EnumSource(value = ProjectPartnerReportFileType::class, names = ["PaymentAttachment", "PaymentAdvancedAttachment"])
    fun persistFile(type: ProjectPartnerReportFileType) {
        every { minioStorage.saveFile(any(), any(), any(), any(), true) } returns Unit

        val slotFileEntity = slot<ReportProjectFileEntity>()
        every { reportFileRepository.save(capture(slotFileEntity)) } returnsArgument 0

        val userEntity = mockk<UserEntity>()
        every { userRepository.getById(USER_ID) } returns userEntity

        val file = file(type = type)
        repository.persistFile(file, "/minio/location")

        verify(exactly = 1) { minioStorage.saveFile("payment", "/minio/location", any(), any(), true) }

        assertThat(slotFileEntity.captured.projectId).isEqualTo(PROJECT_ID)
        assertThat(slotFileEntity.captured.partnerId).isNull()
        assertThat(slotFileEntity.captured.path).isEqualTo("/our/indexed/path/")
        assertThat(slotFileEntity.captured.minioBucket).isEqualTo("payment")
        assertThat(slotFileEntity.captured.minioLocation).isEqualTo("/minio/location")
        assertThat(slotFileEntity.captured.name).isEqualTo("new_file.txt")
        assertThat(slotFileEntity.captured.type).isEqualTo(type)
        assertThat(slotFileEntity.captured.size).isEqualTo(45L)
        assertThat(slotFileEntity.captured.user).isEqualTo(userEntity)
        assertThat(slotFileEntity.captured.description).isEmpty()
    }

    @ParameterizedTest(name = "persistFile (type {0})")
    @EnumSource(value = ProjectPartnerReportFileType::class, names = ["PaymentAttachment", "PaymentAdvancedAttachment"])
    fun setDescription(type: ProjectPartnerReportFileType) {
        val fileId = 85L + type.ordinal
        val file = ReportProjectFileEntity(
            id = fileId,
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
        every { reportFileRepository.findByTypeAndId(type, fileId) } returns file

        repository.setDescription(type, fileId, "new desc")
        assertThat(file.description).isEqualTo("new desc")
    }

    @Test
    fun `setDescription - not existing`() {
        every { reportFileRepository.findByTypeAndId(ProjectPartnerReportFileType.PaymentAttachment, -1L) } returns null
        assertThrows<ResourceNotFoundException> {
            repository.setDescription(ProjectPartnerReportFileType.PaymentAttachment, -1L, "new desc")
        }
    }

    @ParameterizedTest(name = "delete (type {0})")
    @EnumSource(value = ProjectPartnerReportFileType::class, names = ["PaymentAttachment", "PaymentAdvancedAttachment"])
    fun delete(type: ProjectPartnerReportFileType) {
        val fileId = 196L + type.ordinal
        val file = mockk<ReportProjectFileEntity>()
        every { file.id } returns fileId
        every { file.minioBucket } returns "minioBucket"
        every { file.minioLocation } returns "minioLocation"

        every { reportFileRepository.findByTypeAndId(type, fileId) } returns file
        every { minioStorage.deleteFile("minioBucket", "minioLocation") } answers { }
        every { reportFileRepository.delete(file) } answers { }

        repository.delete(type, fileId)

        verify(exactly = 1) { minioStorage.deleteFile("minioBucket", "minioLocation") }
        verify(exactly = 1) { reportFileRepository.delete(file) }
    }

    @ParameterizedTest(name = "delete - not attachment (type {0})")
    @EnumSource(value = ProjectPartnerReportFileType::class, names = ["PaymentAttachment", "PaymentAdvancedAttachment"], mode = EnumSource.Mode.EXCLUDE)
    fun `delete - not attachment`(type: ProjectPartnerReportFileType) {
        val fileId = 196L + type.ordinal
        assertThrows<WrongFileTypeException> { repository.delete(type, fileId) }

        verify(exactly = 0) { minioStorage.deleteFile(any(), any()) }
        verify(exactly = 0) { reportFileRepository.delete(any()) }
    }

}
