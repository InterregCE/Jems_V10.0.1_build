package io.cloudflight.jems.server.project.controller.contracting.management.file

import io.cloudflight.jems.api.project.dto.contracting.file.ProjectContractingFileSearchRequestDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectPartnerReportFileTypeDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.dto.report.file.UserSimpleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.controller.report.dummyFile
import io.cloudflight.jems.server.project.controller.report.dummyFileDto
import io.cloudflight.jems.server.project.controller.report.dummyFileExpected
import io.cloudflight.jems.server.project.controller.report.dummyMultipartFile
import io.cloudflight.jems.server.project.service.contracting.management.file.deleteContractingFile.DeleteContractingFileInteractor
import io.cloudflight.jems.server.project.service.contracting.management.file.downloadContractingFile.DownloadContractingFileInteractor
import io.cloudflight.jems.server.project.service.contracting.management.file.listContractingFiles.ListContractingFilesInteractor
import io.cloudflight.jems.server.project.service.contracting.management.file.uploadFileToContracting.UploadFileToContractingInteractor
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

    }


    @MockK
    lateinit var uploadToContracting: UploadFileToContractingInteractor

    @MockK
    lateinit var listContractingFiles: ListContractingFilesInteractor

    @MockK
    lateinit var downloadContractingFile: DownloadContractingFileInteractor

    @MockK
    lateinit var deleteContractingFile: DeleteContractingFileInteractor

    @InjectMockKs
    private lateinit var controller: ContractingFileController

    @BeforeEach
    fun reset() {
        clearMocks(uploadToContracting)
        clearMocks(listContractingFiles)
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
    fun downloadAttachment() {
        val fileContentArray = ByteArray(5)
        every { downloadContractingFile.download(PROJECT_ID, fileId = 350L) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.downloadFile(PROJECT_ID, fileId = 350L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fileName.txt\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun deleteAttachment() {
        every { deleteContractingFile.delete(PROJECT_ID, fileId = 302L) } answers { }
        controller.deleteFile(PROJECT_ID, fileId = 302L)
        verify(exactly = 1) { deleteContractingFile.delete(PROJECT_ID, fileId = 302L) }
    }

}
