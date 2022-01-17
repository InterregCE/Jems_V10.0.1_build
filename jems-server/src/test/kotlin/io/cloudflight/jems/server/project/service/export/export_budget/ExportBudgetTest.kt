package io.cloudflight.jems.server.project.service.export.export_budget

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.BudgetExportPlugin
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ExportBudgetTest : UnitTest() {
    private val pluginKey = "standard-budget-export-plugin"

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var budgetExportPlugin: BudgetExportPlugin

    @InjectMockKs
    lateinit var exportBudget: ExportBudget

    @Test
    fun `should execute export budget plugin when there is no problem`() {
        val exportResult = ExportResult("csv", "filename", byteArrayOf())
        every {
            jemsPluginRegistry.get(BudgetExportPlugin::class, pluginKey)
        } returns budgetExportPlugin
        every {
            budgetExportPlugin.export(1L, SystemLanguageData.EN, SystemLanguageData.DE)
        } returns exportResult

        Assertions.assertThat(exportBudget.exportDataToCsv(1L, SystemLanguage.EN, SystemLanguage.DE))
            .isEqualTo(exportResult)
    }
}
