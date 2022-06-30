package io.cloudflight.jems.server.project.repository.contracting.management.file

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.minio.MinioStorage
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.repository.report.contribution.ProjectReportContributionPersistenceProviderTest
import io.cloudflight.jems.server.project.repository.report.file.ProjectReportFileRepository
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.user.repository.user.UserRepository
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
        )


    }

    @MockK
    lateinit var reportFileRepository: ProjectReportFileRepository

    @MockK
    lateinit var minioStorage: MinioStorage

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var persistence: ProjectContractingFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportFileRepository)
        clearMocks(minioStorage)
    }

    @Test
    fun uploadFile() {
        val filePathMinio = slot<String>()
        val fileEntity = slot<ReportProjectFileEntity>()

        every { minioStorage.saveFile("project-report", capture(filePathMinio), any(), any(), true) } answers { }
        every { userRepository.getById(270) } returns mockk()
        every { reportFileRepository.save(capture(fileEntity)) } returnsArgument 0

        val fileCreate = fileCreate(type = ProjectPartnerReportFileType.Contract)

        assertThat(persistence.uploadFile(file = fileCreate).name)
            .isEqualTo("new_file.txt")

        assertFile(filePathMinio.captured, fileEntity.captured)
        assertThat(fileEntity.captured.type).isEqualTo(ProjectPartnerReportFileType.Contract)
    }

    private fun assertFile(filePathMinio: String, fileEntity: ReportProjectFileEntity) {
        assertThat(filePathMinio).isEqualTo("our/indexed/path/new_file.txt")
        assertThat(fileEntity.partnerId).isEqualTo(null)
        assertThat(fileEntity.path).isEqualTo("our/indexed/path/")
        assertThat(fileEntity.minioBucket).isEqualTo("project-report")
        assertThat(fileEntity.minioLocation).isEqualTo("our/indexed/path/new_file.txt")
        assertThat(fileEntity.name).isEqualTo("new_file.txt")
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

        every { reportFileRepository.delete(any()) } answers { }
        every { minioStorage.deleteFile(BUCKET, "location") } answers { }

        persistence.deleteFile(PROJECT_ID, fileId = 15L)

        verify(exactly = 1) { reportFileRepository.delete(any()) }
        verify(exactly = 1) { minioStorage.deleteFile(BUCKET, "location") }
    }

    @Test
    fun `deleteFile - not existing`() {
        every { reportFileRepository.findByProjectIdAndId(PROJECT_ID, fileId = -1L) } returns null

        persistence.deleteFile(PROJECT_ID, fileId = -1L)

        verify(exactly = 0) { reportFileRepository.delete(any()) }
        verify(exactly = 0) { minioStorage.deleteFile(any(), any()) }
    }

}
