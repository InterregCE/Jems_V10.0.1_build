package io.cloudflight.jems.server.programme.controller.export

import io.cloudflight.jems.api.programme.dto.export.ProgrammeDataExportMetadataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.EXPORT_LANGUAGE
import io.cloudflight.jems.server.programme.service.INPUT_LANGUAGE
import io.cloudflight.jems.server.programme.service.PLUGIN_KEY
import io.cloudflight.jems.server.programme.service.downloadProgrammeDataExportFile.DownloadProgrammeDataExportFileInteractor
import io.cloudflight.jems.server.programme.service.exportMetaData
import io.cloudflight.jems.server.programme.service.exportProgrammeData.ExportProgrammeDataInteractor
import io.cloudflight.jems.server.programme.service.exportResult
import io.cloudflight.jems.server.programme.service.listProgrammeDataExports.ListProgrammeDataExports
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import java.time.ZonedDateTime


internal class ProgrammeDataExportControllerTest : UnitTest() {

    @MockK
    lateinit var exportProgrammeDataInteractor: ExportProgrammeDataInteractor

    @MockK
    lateinit var listProgrammeDataExports: ListProgrammeDataExports

    @MockK
    lateinit var downloadProgrammeDataExportFile: DownloadProgrammeDataExportFileInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeDataExportController

    @Test
    fun `should execute export programme data interactor`() {

        every { exportProgrammeDataInteractor.export(PLUGIN_KEY, SystemLanguage.EN, SystemLanguage.DE) } returns Unit

        controller.export(PLUGIN_KEY, SystemLanguage.EN, SystemLanguage.DE)

        verify { exportProgrammeDataInteractor.export(PLUGIN_KEY, SystemLanguage.EN, SystemLanguage.DE) }
    }

    @Test
    fun `should return list of programme data export metadata`() {
        val exportStartedAt = ZonedDateTime.now().plusSeconds(1)
        val exportEndedAt = ZonedDateTime.now().plusMinutes(2)
        val exportMetadata = exportMetaData(
            fileName = "fileName", contentType = "contentType",
            exportStartedAt = exportStartedAt, exportEndedAt = exportEndedAt
        )

        every { listProgrammeDataExports.list() } returns listOf(exportMetadata)

        assertThat(controller.list()).containsExactly(
            ProgrammeDataExportMetadataDTO(
                PLUGIN_KEY, "fileName", EXPORT_LANGUAGE, INPUT_LANGUAGE, exportMetadata.requestTime, exportMetadata.getExportationTimeInSeconds(),
                timedOut = false, readyToDownload = true, failed = false
            )
        )
    }

    @Test
    fun `should download exported programme data file`() {
        val exportResult = exportResult()
        every { downloadProgrammeDataExportFile.download(PLUGIN_KEY) } returns exportResult

        controller.export(PLUGIN_KEY, SystemLanguage.EN, SystemLanguage.DE)

        verify { exportProgrammeDataInteractor.export(PLUGIN_KEY, SystemLanguage.EN, SystemLanguage.DE) }
        assertThat(controller.download(PLUGIN_KEY))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(exportResult.content.size.toLong())
                    .header(HttpHeaders.CONTENT_TYPE, exportResult.contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${exportResult.fileName}\"")
                    .body(ByteArrayResource(exportResult.content))
            )
    }
}
