package io.cloudflight.jems.server.call.controller.translation

import io.cloudflight.jems.api.call.dto.translation.CallTranslationFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.FR
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.NL
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.NO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.translation.CallTranslationFile
import io.cloudflight.jems.server.call.service.translation.deleteTranslationFile.DeleteCallTranslationFileInteractor
import io.cloudflight.jems.server.call.service.translation.downloadTranslationFile.DownloadCallTranslationFileInteractor
import io.cloudflight.jems.server.call.service.translation.getTranslation.GetTranslationInteractor
import io.cloudflight.jems.server.call.service.translation.uploadTranslationFile.UploadCallTranslationFileInteractor
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.controller.report.partner.dummyMultipartFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.ZonedDateTime

class CallTranslationConfigurationControllerTest : UnitTest() {

    companion object {
        private val time = ZonedDateTime.now()
    }

    @MockK private lateinit var getTranslation: GetTranslationInteractor
    @MockK private lateinit var downloadTranslationFile: DownloadCallTranslationFileInteractor
    @MockK private lateinit var uploadTranslationFile: UploadCallTranslationFileInteractor
    @MockK private lateinit var deleteTranslationFile: DeleteCallTranslationFileInteractor

    @InjectMockKs
    private lateinit var controller: CallTranslationConfigurationController

    @Test
    fun getTranslation() {
        every { getTranslation.get(54L) } returns listOf(
            CallTranslationFile(NO, JemsFileMetadata(41L, "file name", time), "default-name")
        )
        assertThat(controller.getTranslation(54L)).containsExactly(
            CallTranslationFileDTO(
                language = NO,
                file = JemsFileMetadataDTO(41L, "file name", time),
                defaultFromProgramme = "default-name",
            ),
        )
    }

    @Test
    fun downloadTranslationFile() {
        val fileContentArray = ByteArray(5)
        every { downloadTranslationFile.download(callId = 22L, fileId = 350L) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.downloadTranslationFile(22L, fileId = 350L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fileName.txt\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun uploadTranslationFile() {
        val slotFile = slot<ProjectFile>()
        every { uploadTranslationFile.upload(57L, FR, capture(slotFile)) } returns
                CallTranslationFile(FR, JemsFileMetadata(id = 90L, "file_name.ext", uploaded = time), "some default name")

        assertThat(controller.uploadTranslationFile(57L, FR, dummyMultipartFile())).isEqualTo(
            CallTranslationFileDTO(FR, JemsFileMetadataDTO(id = 90L, "file_name.ext", uploaded = time), "some default name")
        )
    }

    @Test
    fun deleteTranslationFile() {
        every { deleteTranslationFile.delete(59L, NL) } answers { }
        controller.deleteTranslationFile(59L, NL)
        verify(exactly = 1) { deleteTranslationFile.delete(59L, NL) }
    }

}
