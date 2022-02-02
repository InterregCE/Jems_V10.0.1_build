package io.cloudflight.jems.server.programme.controller.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.export_programme_data.ExportProgrammeDataInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity


internal class ProgrammeDataExportControllerTest : UnitTest(){

    private val pluginKey = "pluginKey"

    @MockK
    lateinit var exportProgrammeDataInteractor: ExportProgrammeDataInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeDataExportController

    @Test
    fun `should export programme data when there is no problem`(){
        val exportResult = ExportResult("type", "file-name", byteArrayOf())
        every { exportProgrammeDataInteractor.export(pluginKey, SystemLanguage.EN, SystemLanguage.DE)} returns exportResult
            assertThat(controller.export(pluginKey, SystemLanguage.EN, SystemLanguage.DE))
            .isEqualTo( ResponseEntity.ok()
                .contentLength(exportResult.content.size.toLong())
                .header(HttpHeaders.CONTENT_TYPE, exportResult.contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${exportResult.fileName}\"")
                .body(ByteArrayResource(exportResult.content)))
    }
}
