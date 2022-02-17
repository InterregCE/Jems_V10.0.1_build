package io.cloudflight.jems.server.programme.service.exportProgrammeData

import io.cloudflight.jems.plugin.contract.export.ProgrammeDataExportPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.programme.service.EXPORT_LANGUAGE
import io.cloudflight.jems.server.programme.service.INPUT_LANGUAGE
import io.cloudflight.jems.server.programme.service.PLUGIN_KEY
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test


internal class ExportProgrammeDataTest : UnitTest() {

    @MockK
    lateinit var pluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var exportProgrammeDataService: ExportProgrammeDataService

    @InjectMockKs
    lateinit var exportProgrammeData: ExportProgrammeData

    @Test
    fun `should save export programme data metadata and trigger export when there is no problem`() {
        val pluginMockk = mockk<ProgrammeDataExportPlugin>()

        every { pluginMockk.getKey() } returns PLUGIN_KEY
        every { pluginRegistry.get(ProgrammeDataExportPlugin::class, PLUGIN_KEY) } returns pluginMockk
        every {
            exportProgrammeDataService.saveExportFileMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE)
        } returns Unit
        every { exportProgrammeDataService.execute(pluginMockk, EXPORT_LANGUAGE, INPUT_LANGUAGE) } returns Unit

        exportProgrammeData.export(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE)

        verifyOrder {
            exportProgrammeDataService.saveExportFileMetaData(PLUGIN_KEY, EXPORT_LANGUAGE, INPUT_LANGUAGE)
            exportProgrammeDataService.execute(pluginMockk, EXPORT_LANGUAGE, INPUT_LANGUAGE)
        }

    }
}
