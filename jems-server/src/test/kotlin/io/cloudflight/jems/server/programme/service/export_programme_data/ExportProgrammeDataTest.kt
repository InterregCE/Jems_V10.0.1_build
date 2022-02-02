package io.cloudflight.jems.server.programme.service.export_programme_data

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.ProgrammeDataExportPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.services.toDataModel
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class ExportProgrammeDataTest : UnitTest(){

    private val pluginKey = "pluginKey"

    @MockK
    lateinit var pluginRegistry: JemsPluginRegistry

    @InjectMockKs
    lateinit var exportProgrammeData: ExportProgrammeData

    @Test
    fun `should export programme data when there is no problem`(){
        val pluginMockk = mockk<ProgrammeDataExportPlugin>()
        val exportResult = ExportResult("type", "file-name", byteArrayOf())
        every { pluginMockk.export( SystemLanguage.EN.toDataModel(), SystemLanguage.DE.toDataModel())} returns exportResult
        every { pluginRegistry.get(ProgrammeDataExportPlugin::class, pluginKey) } returns pluginMockk
        assertThat(exportProgrammeData.export(pluginKey, SystemLanguage.EN, SystemLanguage.DE)).isEqualTo(
            exportResult
        )
    }
}
