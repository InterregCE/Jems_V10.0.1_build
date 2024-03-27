package io.cloudflight.jems.server.project.service.checklist.export.closure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.checklist.ChecklistExportPlugin
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.export.ExportChecklistInstanceNotFoundException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ExportClosureChecklistInstanceTest: UnitTest()  {

    companion object {
        private const val CHECKLIST_ID = 102L
        private const val PLUGIN_KEY = "export-plugin"
        private const val PROJECT_ID = 98L
        private const val REPORT_ID = 99L
    }

    @MockK
    lateinit var pluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var checklistInstancePersistence: ChecklistInstancePersistence

    @MockK
    lateinit var exportPlugin: ChecklistExportPlugin

    @InjectMockKs
    lateinit var exportChecklistInstance: ExportClosureChecklistInstance

    @Test
    fun export() {
        every { checklistInstancePersistence.existsByIdAndRelatedToId(id = CHECKLIST_ID, relatedToId = REPORT_ID) } returns true
        val exportResult = ExportResult("pdf", "filename", byteArrayOf())
        every { pluginRegistry.get(ChecklistExportPlugin::class, PLUGIN_KEY) } returns exportPlugin
        every { exportPlugin.export(PROJECT_ID, CHECKLIST_ID, SystemLanguageData.EN) } returns exportResult

        val result = exportChecklistInstance.export(
            projectId = PROJECT_ID,
            reportId = REPORT_ID,
            checklistId = CHECKLIST_ID,
            exportLanguage = SystemLanguage.EN,
            pluginKey = PLUGIN_KEY
        )
        assertThat(result).isEqualTo(exportResult)
    }

    @Test
    fun exportNotFound() {
        every { checklistInstancePersistence.existsByIdAndRelatedToId(id = CHECKLIST_ID, relatedToId = REPORT_ID) } returns false
        assertThrows<ExportChecklistInstanceNotFoundException> {
            exportChecklistInstance.export(PROJECT_ID, REPORT_ID, CHECKLIST_ID, SystemLanguage.EN)
        }
    }
}
