package io.cloudflight.jems.server.project.controller.contracting.management.file

import io.cloudflight.jems.api.project.dto.contracting.file.ProjectContractingFileSearchRequestDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectPartnerReportFileTypeDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.dto.report.file.UserSimpleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.controller.contracting.fileManagement.ContractingFileController
import io.cloudflight.jems.server.project.controller.report.dummyFile
import io.cloudflight.jems.server.project.controller.report.dummyFileDto
import io.cloudflight.jems.server.project.controller.report.dummyFileExpected
import io.cloudflight.jems.server.project.controller.report.dummyMultipartFile
import io.cloudflight.jems.server.project.service.contracting.fileManagement.FileNotFound
import io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractFile.DeleteContractFileException
import io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractFile.DeleteContractFileInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteInternalFile.DeleteContractingInternalFileException
import io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteInternalFile.DeleteInternalFileInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.deletePartnerFile.DeleteContractingPartnerFileException
import io.cloudflight.jems.server.project.service.contracting.fileManagement.deletePartnerFile.DeletePartnerFileInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadContractFile.DownloadContractFileException
import io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadContractFile.DownloadContractFileInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadInternalFile.DownloadInternalFileException
import io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadInternalFile.DownloadInternalFileInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.downloadPartnerFile.DownloadPartnerFileInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles.ListContractingFilesInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.listPartnerFiles.ListContractingPartnerFilesInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.setContractFileDescription.SetContractFileDescriptionInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.setInternalFileDescription.SetInternalFileDescriptionInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.setPartnerFileDescription.SetDescriptionToPartnerFileException
import io.cloudflight.jems.server.project.service.contracting.fileManagement.setPartnerFileDescription.SetPartnerFileDescriptionInteractor
import io.cloudflight.jems.server.project.service.contracting.fileManagement.uploadFileToContracting.UploadFileToContractingInteractor
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.ZonedDateTime

class ContractingFileControllerTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 656L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)

        private val contractingFile = ProjectReportFile(
            id = 478L,
            name = "attachment.pdf",
            type = ProjectPartnerReportFileType.Contract,
            uploaded = YESTERDAY,
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            description = "example desc",
        )

        private val contractingFileDto = ProjectReportFileDTO(
            id = 478L,
            name = "attachment.pdf",
            type = ProjectPartnerReportFileTypeDTO.Contract,
            uploaded = YESTERDAY,
            author = UserSimpleDTO(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            sizeString = "46.8\u0020kB",
            description = "example desc",
        )

        private val partnerFile = ProjectReportFile(
            id = 479L,
            name = "partner-attachment.pdf",
            type = ProjectPartnerReportFileType.ContractPartnerDoc,
            uploaded = YESTERDAY,
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            description = "example desc",
        )

        private val partnerFileDto = ProjectReportFileDTO(
            id = 479L,
            name = "partner-attachment.pdf",
            type = ProjectPartnerReportFileTypeDTO.ContractPartnerDoc,
            uploaded = YESTERDAY,
            author = UserSimpleDTO(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            sizeString = "46.8\u0020kB",
            description = "example desc",
        )

    }


    @MockK
    lateinit var uploadToContracting: UploadFileToContractingInteractor

    @MockK
    lateinit var listContractingFiles: ListContractingFilesInteractor

    @MockK
    lateinit var downloadContractFile: DownloadContractFileInteractor

    @MockK
    lateinit var downloadInternalFile: DownloadInternalFileInteractor

    @MockK
    lateinit var deleteContractFile: DeleteContractFileInteractor

    @MockK
    lateinit var deleteInternalFile: DeleteInternalFileInteractor

    @MockK
    lateinit var setContractFileDescriptionInteractor: SetContractFileDescriptionInteractor

    @MockK
    lateinit var setInternalFileDescriptionInteractor: SetInternalFileDescriptionInteractor

    @MockK
    lateinit var listContractingPartnerFiles: ListContractingPartnerFilesInteractor

    @MockK
    lateinit var setPartnerFileDescription: SetPartnerFileDescriptionInteractor

    @MockK
    lateinit var downloadPartnerFile: DownloadPartnerFileInteractor

    @MockK
    lateinit var deletePartnerFile: DeletePartnerFileInteractor


    @InjectMockKs
    private lateinit var controller: ContractingFileController

    @BeforeEach
    fun reset() {
        clearMocks(uploadToContracting)
        clearMocks(listContractingFiles)
        clearMocks(listContractingPartnerFiles)
    }

    @Test
    fun uploadContractFile() {
        val slotFile = slot<ProjectFile>()
        every { uploadToContracting.uploadContract(PROJECT_ID, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadContractFile(PROJECT_ID, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun uploadContractDocumentFile() {
        val slotFile = slot<ProjectFile>()
        every { uploadToContracting.uploadContractDocument(PROJECT_ID, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadContractDocumentFile(PROJECT_ID, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun uploadContractFileForPartner() {
        val slotFile = slot<ProjectFile>()
        every { uploadToContracting.uploadContractPartnerFile(PROJECT_ID, 12L, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadContractFileForPartner(PROJECT_ID, 12L, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun uploadContractInternalFile() {
        val slotFile = slot<ProjectFile>()
        every { uploadToContracting.uploadContractInternalFile(PROJECT_ID, capture(slotFile)) } returns dummyFile
        assertThat(controller.uploadContractInternalFile(PROJECT_ID, dummyMultipartFile())).isEqualTo(dummyFileDto)
        assertThat(slotFile.captured).isEqualTo(dummyFileExpected)
    }

    @Test
    fun `listFiles - nonPartner 0`() {
        listFilesTest(0)
    }

    @Test
    fun `listFiles - nonPartner null`() {
        listFilesTest(null)
    }

    private fun listFilesTest(partnerId: Long?) {
        val searchRequest = slot<ProjectContractingFileSearchRequest>()
        every { listContractingFiles.list(29L, null, Pageable.unpaged(), capture(searchRequest)) } returns
            PageImpl(listOf(contractingFile))

        val searchRequestDto = ProjectContractingFileSearchRequestDTO(
            treeNode = ProjectPartnerReportFileTypeDTO.ContractSupport,
            filterSubtypes = setOf(ProjectPartnerReportFileTypeDTO.Contract),
        )

        assertThat(controller.listFiles(29L, partnerId, Pageable.unpaged(), searchRequestDto).content)
            .containsExactly(contractingFileDto)
        assertThat(searchRequest.captured).isEqualTo(
            ProjectContractingFileSearchRequest(
                treeNode = ProjectPartnerReportFileType.ContractSupport,
                filterSubtypes = setOf(ProjectPartnerReportFileType.Contract),
            )
        )
    }

    @Test
    fun `download contract attachment`() {
        val fileContentArray = ByteArray(5)
        every { downloadContractFile.download(PROJECT_ID, fileId = 350L) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.downloadContractFile(PROJECT_ID, fileId = 350L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fileName.txt\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun `download contract attachment throws exception`() {
        every { downloadContractFile.download(PROJECT_ID, fileId = -1L) } throws DownloadContractFileException(FileNotFound())
        val exception = assertThrows<DownloadContractFileException> { downloadContractFile.download(PROJECT_ID, fileId = -1L) }
        assertThat(exception.i18nMessage.i18nKey).isEqualTo("use.case.download.contract.file.failed")
    }

    @Test
    fun `download internal attachment`() {
        val fileContentArray = ByteArray(5)
        every { downloadInternalFile.download(PROJECT_ID, fileId = 350L) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.downloadInternalFile(PROJECT_ID, fileId = 350L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fileName.txt\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun `download internal attachment throws exception`() {
        every { downloadInternalFile.download(PROJECT_ID, fileId = -1L) } throws DownloadInternalFileException(FileNotFound())
        val exception = assertThrows<DownloadInternalFileException> { downloadInternalFile.download(PROJECT_ID, fileId = -1L) }
        assertThat(exception.i18nMessage.i18nKey).isEqualTo("use.case.download.internal.file.failed")
    }

    @Test
    fun `delete contract attachment`() {
        every { deleteContractFile.delete(PROJECT_ID, fileId = 302L) } answers { }
        controller.deleteContractFile(PROJECT_ID, fileId = 302L)
        verify(exactly = 1) { deleteContractFile.delete(PROJECT_ID, fileId = 302L) }
    }


    @Test
    fun `delete contract attachment throws exception`() {
        every { deleteContractFile.delete(PROJECT_ID, fileId = -1L) } throws DeleteContractFileException(RuntimeException())
        val exception = assertThrows<DeleteContractFileException> { deleteContractFile.delete(PROJECT_ID, fileId = -1L) }
        assertThat(exception.i18nMessage.i18nKey).isEqualTo("use.case.delete.contract.file.failed")
    }
    @Test
    fun `delete internal attachment`() {
        every { deleteInternalFile.delete(PROJECT_ID, fileId = 302L) } answers { }
        controller.deleteInternalFile(PROJECT_ID, fileId = 302L)
        verify(exactly = 1) { deleteInternalFile.delete(PROJECT_ID, fileId = 302L) }
    }

    @Test
    fun `delete internal attachment throws exception`() {
        every { deleteInternalFile.delete(PROJECT_ID, fileId = -1L) } throws DeleteContractingInternalFileException(RuntimeException())
        val exception = assertThrows<DeleteContractingInternalFileException> { deleteInternalFile.delete(PROJECT_ID, fileId = -1L) }
        assertThat(exception.i18nMessage.i18nKey).isEqualTo("use.case.delete.contracting.internal.file.failed")
    }

    @Test
    fun setContractFileDescription() {
        every { setContractFileDescriptionInteractor.setContractFileDescription(
            projectId = 1L,
            fileId = 1L,
            description = "description"
        ) } answers { }
        controller.updateContractFileDescription(1L, 1L, "description")
        verify(exactly = 1) { controller.updateContractFileDescription(1L, 1L, "description") }
    }


    @Test
    fun setInternalFileDescription() {
        every { setInternalFileDescriptionInteractor.setInternalFileDescription(
            projectId = 1L,
            fileId = 1L,
            description = "description"
        ) } answers { }
        controller.updateInternalFileDescription(1L, 1L, "description")
        verify(exactly = 1) { controller.updateInternalFileDescription(1L, 1L, "description") }
    }

    @Test
    fun `set partner file description`() {
        every { setPartnerFileDescription.setPartnerFileDescription(
            partnerId = 1L,
            fileId = 1L,
            description = "description partner"
        ) } answers { }
        controller.updatePartnerFileDescription(1L, 1L, 1L, "description partner")
        verify(exactly = 1) { controller.updatePartnerFileDescription(1L, 1L, 1L, "description partner") }
    }

    @Test
    fun `set partner file description throws exception`() {
        every { setPartnerFileDescription.setPartnerFileDescription(
            partnerId = 1L,
            fileId = 1L,
            description = ""
        ) } throws SetDescriptionToPartnerFileException(FileNotFound())
        val exception = assertThrows<SetDescriptionToPartnerFileException> { setPartnerFileDescription.setPartnerFileDescription(1L, 1L, "") }
        assertThat(exception.i18nMessage.i18nKey).isEqualTo("use.case.set.description.to.partner.file.failed")
    }

    @Test
    fun `list partner specific files`() {
        val searchRequest = slot<ProjectContractingFileSearchRequest>()
        every { listContractingPartnerFiles.listPartner(1L, Pageable.unpaged(), capture(searchRequest)) } returns
            PageImpl(listOf(partnerFile))

        val searchRequestDto = ProjectContractingFileSearchRequestDTO(
            treeNode = ProjectPartnerReportFileTypeDTO.ContractPartnerDoc,
            filterSubtypes = setOf(ProjectPartnerReportFileTypeDTO.ContractPartnerDoc),
        )

        assertThat(controller.listPartnerFiles(29L, 1L, Pageable.unpaged(), searchRequestDto).content)
            .containsExactly(partnerFileDto)
        assertThat(searchRequest.captured).isEqualTo(
            ProjectContractingFileSearchRequest(
                treeNode = ProjectPartnerReportFileType.ContractPartnerDoc,
                filterSubtypes = setOf(ProjectPartnerReportFileType.ContractPartnerDoc),
            )
        )
    }

    @Test
    fun `download partner file`() {
        val fileContentArray = ByteArray(5)
        every { downloadPartnerFile.downloadPartnerFile(1L, fileId = 360L) } returns Pair("partnerFile.txt", fileContentArray)

        assertThat(controller.downloadPartnerFile(PROJECT_ID, 1L, fileId = 360L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"partnerFile.txt\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun `delete partner file`() {
        every { deletePartnerFile.delete(1L, fileId = 303L) } answers { }
        controller.deletePartnerFile(PROJECT_ID, 1L, fileId = 303L)
        verify(exactly = 1) { deletePartnerFile.delete(1L, fileId = 303L) }
    }

    @Test
    fun `delete partner attachment throws exception`() {
        every { deletePartnerFile.delete(1L, fileId = -1L) } throws DeleteContractingPartnerFileException(RuntimeException())
        val exception = assertThrows<DeleteContractingPartnerFileException> { deletePartnerFile.delete(1L, fileId = -1L) }
        assertThat(exception.i18nMessage.i18nKey).isEqualTo("use.case.delete.contracting.partner.file.failed")
    }

}
