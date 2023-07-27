package io.cloudflight.jems.server.project.service.checklist.export.contracting;

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

class ExportContractingChecklistInstanceTest: UnitTest() {

    companion object {
        private const val CHECKLIST_ID = 101L
        private const val PLUGIN_KEY = "export-plugin"
    }

    @MockK
    lateinit var pluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var exportPlugin: ChecklistExportPlugin

    @MockK
    lateinit var checklistInstancePersistence: ChecklistInstancePersistence

    @InjectMockKs
    lateinit var exportChecklistInstance: ExportContractingChecklistInstance

    @Test
    fun export() {
        every { checklistInstancePersistence.existsByIdAndRelatedToId(id = CHECKLIST_ID, relatedToId = 1L) } returns true
        val exportResult = ExportResult("pdf", "filename", byteArrayOf())
        every { pluginRegistry.get(ChecklistExportPlugin::class, PLUGIN_KEY) } returns exportPlugin
        every { exportPlugin.export(1L, CHECKLIST_ID, SystemLanguageData.EN) } returns exportResult

        val result = exportChecklistInstance.export(projectId = 1L, checklistId = CHECKLIST_ID, exportLanguage = SystemLanguage.EN, pluginKey = PLUGIN_KEY)
        assertThat(result).isEqualTo(exportResult)
    }

    @Test
    fun exportNotFound() {
        every { checklistInstancePersistence.existsByIdAndRelatedToId(id = CHECKLIST_ID, relatedToId = 1L) } returns false
        assertThrows<ExportChecklistInstanceNotFoundException> { exportChecklistInstance.export(1L, CHECKLIST_ID, SystemLanguage.EN) }
    }

}
