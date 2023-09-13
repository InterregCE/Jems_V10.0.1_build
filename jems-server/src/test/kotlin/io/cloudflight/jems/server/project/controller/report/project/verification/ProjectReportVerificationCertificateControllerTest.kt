package io.cloudflight.jems.server.project.controller.report.project.verification

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileTypeDTO
import io.cloudflight.jems.api.common.dto.file.UserSimpleDTO
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.project.controller.report.partner.sizeToString
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.download.DownloadProjectReportVerificationCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.generate.GenerateVerificationCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.list.ListProjectReportVerificationCertificateInteractor
import io.cloudflight.jems.server.project.service.report.project.verification.certificate.updateDescription.UpdateDescriptionProjectReportVerificationCertificateInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.ZonedDateTime

class ProjectReportVerificationCertificateControllerTest {

    companion object {
        const val PROJECT_ID = 54L
        const val REPORT_ID = 39L
        const val FILE_ID = 21L
        private val UPLOAD_DATE = ZonedDateTime.now().minusWeeks(3)

        private val certificateFile = JemsFile(
            id = 97L,
            name = "certificate.pdf",
            type = JemsFileType.VerificationCertificate,
            uploaded = UPLOAD_DATE,
            author = UserSimple(41L, email = "jsma@jems.eu", name = "js", surname = "ma"),
            size = 1024L,
            description = "desc",
            indexedPath = "index/path"
        )

        private val certificateFileDto = JemsFileDTO(
            id = 97L,
            name = "certificate.pdf",
            type = JemsFileTypeDTO.VerificationCertificate,
            uploaded = UPLOAD_DATE,
            author = UserSimpleDTO(41L, email = "jsma@jems.eu", name = "js", surname = "ma"),
            size = 1024L,
            sizeString = (1024L).sizeToString(),
            description = "desc"
        )
    }

    @MockK
    lateinit var listCertificate: ListProjectReportVerificationCertificateInteractor

    @MockK
    lateinit var updateDescriptionCertificate: UpdateDescriptionProjectReportVerificationCertificateInteractor

    @MockK
    lateinit var downloadCertificate: DownloadProjectReportVerificationCertificateInteractor

    @MockK
    lateinit var generateCertificate: GenerateVerificationCertificateInteractor

    @InjectMockKs
    lateinit var controller: ProjectReportVerificationCertificateController

    @BeforeEach
    fun setUp() {
        clearMocks(
            listCertificate,
            updateDescriptionCertificate,
            downloadCertificate,
            generateCertificate,
        )
    }

    @Test
    fun list() {
        every { listCertificate.list(PROJECT_ID, REPORT_ID, Pageable.unpaged()) } returns PageImpl(listOf(certificateFile))

        assertThat(controller.list(PROJECT_ID, REPORT_ID, Pageable.unpaged()).content)
            .containsExactly(certificateFileDto)
        verify { listCertificate.list(PROJECT_ID, REPORT_ID, Pageable.unpaged()) }
    }

    @Test
    fun updateDescription() {
        every { updateDescriptionCertificate.updateDescription(PROJECT_ID, REPORT_ID, FILE_ID, "new desc") } answers { }

        assertDoesNotThrow { controller.updateDescription(PROJECT_ID, REPORT_ID, FILE_ID, "new desc") }
        verify { updateDescriptionCertificate.updateDescription(PROJECT_ID, REPORT_ID, FILE_ID, "new desc") }
    }

    @Test
    fun download() {
        val fileContentArray = ByteArray(5)
        every { downloadCertificate.download(PROJECT_ID, REPORT_ID, FILE_ID) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.download(PROJECT_ID, REPORT_ID, FILE_ID))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fileName.txt\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun generate() {
        val pluginKey = "verification-certificate-export"
        every { generateCertificate.generateCertificate(PROJECT_ID, REPORT_ID, pluginKey) } just runs

        controller.generate(PROJECT_ID, REPORT_ID, pluginKey)
        verify { generateCertificate.generateCertificate(PROJECT_ID, REPORT_ID, pluginKey) }
    }
}
