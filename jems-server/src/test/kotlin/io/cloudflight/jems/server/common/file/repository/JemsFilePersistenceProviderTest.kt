package io.cloudflight.jems.server.common.file.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.minio.MinioStorage
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.user.entity.UserEntity
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.io.FilterInputStream
import java.time.ZonedDateTime

class JemsFilePersistenceProviderTest: UnitTest() {

    companion object {
        private const val PROJECT_ID = 877L
        private const val PARTNER_ID = 365L
        private const val USER_ID = 270L

        private val LAST_WEEK = ZonedDateTime.now().minusWeeks(1)
        private const val BUCKET = "custom_bucket"

        private fun file(id: Long, name: String = "file.txt", filePathFull: String = "path/to/file.txt") = JemsFileMetadataEntity(
            id = id,
            projectId = 6666L,
            partnerId = PARTNER_ID,
            path = "",
            minioBucket = BUCKET,
            minioLocation = filePathFull,
            name = name,
            type = JemsFileType.Activity,
            size = 45L,
            user = mockk(),
            uploaded = LAST_WEEK,
            description = "desc",
        )

        private val dummyFile = JemsFile(
            id = 478L,
            name = "attachment.pdf",
            type = JemsFileType.Contribution,
            uploaded = LAST_WEEK,
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            description = "desc",
            indexedPath = ""
        )

        private val dummyFileMetadataEntity = JemsFileMetadataEntity(
            id = 478L,
            projectId = 4L,
            partnerId = 5L,
            path = "",
            minioBucket = "minioBucket",
            minioLocation = "",
            name = "attachment.pdf",
            type = JemsFileType.Contribution,
            size = 47889L,
            user = UserEntity(id = 45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big", sendNotificationsToEmail = false,
                password = "##", userRole = mockk(), userStatus = mockk()),
            uploaded = LAST_WEEK,
            description = "desc",
        )

    }

    @MockK
    lateinit var projectFileMetadataRepository: JemsFileMetadataRepository

    @MockK
    lateinit var minioStorage: MinioStorage

    @MockK
    lateinit var fileRepository: JemsProjectFileService

    @InjectMockKs
    lateinit var persistence: JemsFilePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(minioStorage)
        clearMocks(projectFileMetadataRepository)
        clearMocks(fileRepository)
    }

    @Test
    fun existsFileByLocation() {
        every { projectFileMetadataRepository.findOneByPathAndName(path = "Project/Report/Partner/", name = "test.xlsx") } returns null
        assertThat(persistence.existsFile(exactPath = "Project/Report/Partner/", fileName = "test.xlsx")).isFalse
    }

    @Test
    fun existsFile() {
        every { projectFileMetadataRepository.existsByPartnerIdAndPathPrefixAndId(
            partnerId = PARTNER_ID,
            pathPrefix = "Project/45/Report/21",
            id = 14L,
        ) } returns true
        assertThat(persistence.existsFile(PARTNER_ID, "Project/45/Report/21", fileId = 14L)).isTrue
    }

    @Test
    fun fileIdIfExists() {
        val file = mockk<JemsFileMetadataEntity>()
        every { file.id } returns 887L
        every { projectFileMetadataRepository.findOneByPathAndName("Project/45/Report/21", "existingName") } returns file
        assertThat(persistence.fileIdIfExists("Project/45/Report/21", fileName = "existingName")).isEqualTo(887L)
    }

    @Test
    fun fileIdIfNotExists() {
        every { projectFileMetadataRepository.findOneByPathAndName("path", "notExistingName") } returns null
        assertThat(persistence.fileIdIfExists("path", fileName = "notExistingName")).isNull()
    }

    @Test
    fun existsFileId() {
        every { projectFileMetadataRepository.findOneByPathAndId("CallTransl/45/", id = 21L) } returns mockk()
        every { projectFileMetadataRepository.findOneByPathAndId("CallTransl/45/", id = -1L) } returns null
        assertThat(persistence.existsFile("CallTransl/45/", fileId = 21L)).isTrue()
        assertThat(persistence.existsFile("CallTransl/45/", fileId = -1L)).isFalse()
    }

    @Test
    fun existsFileByProjectIdAndFileIdAndFileTypeIn() {
        every { projectFileMetadataRepository.existsByProjectIdAndIdAndTypeIn(
            projectId = PROJECT_ID,
            fileId = 15L,
            fileTypes = setOf(JemsFileType.ContractInternal),
        ) } returns true
        assertThat(
            persistence.existsFileByProjectIdAndFileIdAndFileTypeIn(PROJECT_ID, fileId = 15L, setOf(JemsFileType.ContractInternal))
        ).isTrue
    }

    @Test
    fun existsFileByPartnerIdAndFileIdAndFileTypeIn() {
        every { projectFileMetadataRepository.existsByPartnerIdAndIdAndTypeIn(
            partnerId = 1L,
            fileId = 15L,
            fileTypes = setOf(JemsFileType.ContractPartnerDoc),
        ) } returns true
        assertThat(
            persistence.existsFileByPartnerIdAndFileIdAndFileTypeIn(1L, fileId = 15L, setOf(JemsFileType.ContractPartnerDoc))
        ).isTrue
    }

    @Test
    fun getFileAuthor() {
        val user = mockk<UserEntity>()
        every { user.id } returns USER_ID
        every { user.email } returns "email 270"
        every { user.name } returns "name 270"
        every { user.surname } returns "surname 270"


        val reportFile =  JemsFileMetadataEntity(
            id = 16L,
            projectId = PROJECT_ID,
            partnerId = null,
            path = "",
            minioBucket = BUCKET,
            minioLocation = "filePathFull",
            name = "name",
            type = JemsFileType.Contract,
            size = 45L,
            user = user,
            uploaded = LAST_WEEK,
            description = "dummy description",
        )
        every { projectFileMetadataRepository.findByPartnerIdAndPathPrefixAndId(
            partnerId = PARTNER_ID,
            pathPrefix = "prefix",
            id = 16L,
        ) } returns reportFile
        assertThat(
            persistence.getFile(PARTNER_ID, "prefix", fileId = 16L)?.author
        ).isEqualTo(UserSimple(USER_ID, "email 270", name = "name 270", "surname 270"))
    }

    @Test
    fun `getFileAuthor - not existing file`() {
        every { projectFileMetadataRepository.findByPartnerIdAndPathPrefixAndId(
            partnerId = PARTNER_ID,
            pathPrefix = "prefix",
            id = 16L,
        ) } returns null
        assertThat(
            persistence.getFile(PARTNER_ID, "prefix", fileId = 16L)?.author
        ).isNull()
    }

    @Test
    fun downloadFile() {
        val filePathFull = "sample/path/to/file.txt"
        every { projectFileMetadataRepository.findByPartnerIdAndId(PARTNER_ID, fileId = 17L) } returns
                file(id = 17L, name = "file.txt", filePathFull = filePathFull)
        every { minioStorage.getFile(BUCKET, filePathFull) } returns ByteArray(5)

        assertThat(persistence.downloadFile(PARTNER_ID, fileId = 17L))
            .usingRecursiveComparison()
            .isEqualTo(Pair("file.txt", ByteArray(5)))
    }

    @Test
    fun `downloadFile - not existing`() {
        every { projectFileMetadataRepository.findByPartnerIdAndId(PARTNER_ID, fileId = -1L) } returns null
        assertThat(persistence.downloadFile(PARTNER_ID, fileId = -1L)).isNull()
        verify(exactly = 0) { minioStorage.getFile(any(), any()) }
    }

    @Test
    fun `downloadFile - by type`() {
        val filePathFull = "sample/path/to/file.txt"
        every {
            projectFileMetadataRepository.findByTypeAndId(
                JemsFileType.PaymentAttachment,
                fileId = 20L
            )
        } returns file(id = 20L, name = "file.txt", filePathFull = filePathFull)
        every { minioStorage.getFile(BUCKET, filePathFull) } returns ByteArray(5)

        assertThat(persistence.downloadFile(JemsFileType.PaymentAttachment, fileId = 20L))
            .usingRecursiveComparison()
            .isEqualTo(Pair("file.txt", ByteArray(5)))
    }

    @Test
    fun downloadFileAsStream() {
        val filePathFull = "sample/path/to/file.txt"
        every {
            projectFileMetadataRepository.findByTypeAndId(JemsFileType.CallTranslation, fileId = 77L)
        } returns file(id = 77L, name = "file.txt", filePathFull = filePathFull)
        val stream = mockk<FilterInputStream>()
        every { minioStorage.getFileAsStream(BUCKET, filePathFull) } returns stream

        assertThat(persistence.downloadFileAsStream(JemsFileType.CallTranslation, fileId = 77L))
            .isEqualTo(Pair("file.txt", stream))
    }

    @Test
    fun `downloadFile - by type - not existing`() {
        every { projectFileMetadataRepository.findByTypeAndId(JemsFileType.PaymentAttachment, fileId = -1L) } returns null
        assertThat(persistence.downloadFile(JemsFileType.PaymentAttachment, fileId = -1L)).isNull()
        verify(exactly = 0) { minioStorage.getFile(any(), any()) }
    }

    @Test
    fun deleteFile() {
        val filePathFull = "sample/path/to/file-to-delete.txt"
        val fileToDelete = file(
            id = 20L,
            name = "file-to-delete.txt",
            filePathFull = filePathFull
        )
        every { projectFileMetadataRepository.findByTypeAndId(JemsFileType.PaymentAttachment, fileId = 20L) } returns fileToDelete
        every { fileRepository.delete(fileToDelete) } answers { }

        persistence.deleteFile(JemsFileType.PaymentAttachment, fileId = 20L)

        verify(exactly = 1) { fileRepository.delete(fileToDelete) }
    }

    @Test
    fun `deleteFile - not existing`() {
        every { projectFileMetadataRepository.findByPartnerIdAndId(PARTNER_ID, fileId = -1L) } returns null
        persistence.deleteFile(PARTNER_ID, fileId = -1L)

        verify(exactly = 0) { fileRepository.delete(any()) }
    }

    @Test
    fun `deleteFile - by type`() {
        val filePathFull = "sample/path/to/file-to-delete.txt"
        val fileToDelete = file(
            id = 21L,
            name = "file-to-delete.txt",
            filePathFull = filePathFull
        )
        every { projectFileMetadataRepository.findByTypeAndId(JemsFileType.PaymentAttachment, fileId = 21L) } returns fileToDelete
        every { fileRepository.delete(fileToDelete) } answers { }

        persistence.deleteFile(JemsFileType.PaymentAttachment, fileId = 21L)

        verify(exactly = 1) { fileRepository.delete(fileToDelete) }
    }

    @Test
    fun `deleteFile - by type - not existing`() {
        every { projectFileMetadataRepository.findByTypeAndId(JemsFileType.PaymentAttachment, fileId = -1L) } returns null
        persistence.deleteFile(JemsFileType.PaymentAttachment, fileId = -1L)

        verify(exactly = 0) { fileRepository.delete(any()) }
    }

    @Test
    fun setDescription() {
        every { fileRepository.setDescription(fileId = 20L, "new desc") } answers { }
        persistence.setDescriptionToFile(fileId = 20L, "new desc")
        verify(exactly = 1) { fileRepository.setDescription(fileId = 20L, "new desc") }
    }

    @Test
    fun listAttachments() {
        val filterSubtypes = setOf(JemsFileType.Activity)
        val filterUserIds = setOf(45L, 46L, 47L)

        every { projectFileMetadataRepository.filterAttachment(
            pageable = Pageable.unpaged(),
            indexPrefix = "indexPrefix",
            filterSubtypes = filterSubtypes,
            filterUserIds = filterUserIds,
        ) } returns PageImpl(listOf(dummyFileMetadataEntity))

        assertThat(persistence.listAttachments(Pageable.unpaged(), "indexPrefix", filterSubtypes, filterUserIds).content)
            .containsExactly(dummyFile)
    }

    @Test
    fun getFileTypeByPartnerId() {
        every { projectFileMetadataRepository.findByPartnerIdAndId(
            partnerId = 5L,
            fileId = 15L,
        ) } returns dummyFileMetadataEntity
        assertThat(persistence.getFileTypeByPartnerId(partnerId = 5L, fileId = 15L)).isEqualTo(JemsFileType.Contribution)
    }
}
