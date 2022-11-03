package io.cloudflight.jems.server.project.repository.contracting.management.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.minio.GenericProjectFileRepository
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class ProjectContractingFilePersistenceProviderTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 389L
        private const val USER_ID = 270L

        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private const val BUCKET = "bucket_buck_buck"

        private fun fileCreate(name: String = "new_file.txt", type: ProjectPartnerReportFileType) = ProjectReportFileCreate(
            projectId = 6666L,
            partnerId = null,
            name = name,
            path = "our/indexed/path/",
            type = type,
            size = 45L,
            content = mockk(),
            userId = USER_ID,
        )

        private fun file(id: Long, name: String = "file.txt", filePathFull: String = "path/to/file.txt") = ReportProjectFileEntity(
            id = id,
            projectId = PROJECT_ID,
            partnerId = null,
            path = "",
            minioBucket = BUCKET,
            minioLocation = filePathFull,
            name = name,
            type = ProjectPartnerReportFileType.Contract,
            size = 45L,
            user = mockk(),
            uploaded = LAST_WEEK,
            description = "dummy description",
        )

    }

    @MockK
    lateinit var reportFileRepository: ProjectReportFileRepository

    @MockK
    lateinit var minioStorage: MinioStorage

    @MockK
    lateinit var genericFileRepository: GenericProjectFileRepository

    @InjectMockKs
    lateinit var persistence: ProjectContractingFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportFileRepository)
        clearMocks(minioStorage)
        clearMocks(genericFileRepository)
    }

    @Test
    fun uploadFile() {
        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.Contract)

        val metadataMock = mockk<ProjectReportFileMetadata>()
        every { genericFileRepository.persistProjectFile(fileCreate, "our/indexed/path/new_file.txt") } returns metadataMock

        assertThat(persistence.uploadFile(file = fileCreate)).isEqualTo(metadataMock)
    }

    @Test
    fun downloadFile() {
        val filePathFull = "sample/path/to/file.txt"
        every { reportFileRepository.findByProjectIdAndId(PROJECT_ID, fileId = 19L) } returns
            file(id = 17L, name = "file.txt", filePathFull = filePathFull)
        every { minioStorage.getFile(BUCKET, filePathFull) } returns ByteArray(5)

        assertThat(persistence.downloadFile(PROJECT_ID, fileId = 19L))
            .usingRecursiveComparison()
            .isEqualTo(Pair("file.txt", ByteArray(5)))
    }

    @Test
    fun existsFile() {
        every { reportFileRepository.existsByProjectIdAndId(PROJECT_ID, 20L) } returns true
        assertThat(persistence.existsFile(PROJECT_ID, fileId = 20L)).isTrue
    }

    @Test
    fun deleteFile() {
        val file = mockk<ReportProjectFileEntity>()
        every { file.minioBucket } returns BUCKET
        every { file.minioLocation } returns "location"
        every { reportFileRepository.findByProjectIdAndId(PROJECT_ID, fileId = 15L) } returns file

        every { genericFileRepository.delete(any()) } answers { }

        persistence.deleteFile(PROJECT_ID, fileId = 15L)

        verify(exactly = 1) { genericFileRepository.delete(file) }
    }

    @Test
    fun `deleteFile - not existing`() {
        every { reportFileRepository.findByProjectIdAndId(PROJECT_ID, fileId = -1L) } returns null

        persistence.deleteFile(PROJECT_ID, fileId = -1L)

        verify(exactly = 0) { genericFileRepository.delete(any()) }
    }

    @Test
    fun `download file by partner id`() {
        val filePathFull = "sample/path/to/file.txt"
        every { reportFileRepository.findByPartnerIdAndId(partnerId = 1L, fileId = 19L) } returns
            file(id = 19, name = "file.txt", filePathFull = filePathFull)
        every { minioStorage.getFile(BUCKET, filePathFull) } returns ByteArray(5)

        assertThat(persistence.downloadFileByPartnerId(partnerId = 1L, fileId = 19L))
            .usingRecursiveComparison()
            .isEqualTo(Pair("file.txt", ByteArray(5)))
    }

    @Test
    fun `delete file by partner id`() {
        val file = mockk<ReportProjectFileEntity>()
        every { file.minioBucket } returns BUCKET
        every { file.minioLocation } returns "location"
        every { reportFileRepository.findByPartnerIdAndId(partnerId = 1L, fileId = 15L) } returns file

        every { genericFileRepository.delete(any()) } answers { }

        persistence.deleteFileByPartnerId(1L, fileId = 15L)

        verify(exactly = 1) { genericFileRepository.delete(file) }
    }

}
