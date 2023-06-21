package io.cloudflight.jems.server.project.service.report.project.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.project.report.ProjectReportExportPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.time.LocalDateTime

class ProjectReportExportTest: UnitTest() {

    companion object {
        const val PROJECT_ID = 1L
        const val REPORT_ID = 3L
        private const val REPORT_EXPORT_PLUGIN_KEY = "standard-project-report-export-plugin"

        private val EXPORT_LANGUAGE = SystemLanguage.EN
        private val INPUT_LANGUAGE = SystemLanguage.EN
        private val DATE_TIME_NOW = LocalDateTime.now()
    }

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var projectReportExportPlugin: ProjectReportExportPlugin

    @MockK
    lateinit var getLogosInteractor: GetLogosInteractor

    @InjectMockKs
    lateinit var projectReportExport: ProjectReportExport

    @Test
    fun `Export project report pdf - ok`() {
        val exportResult = ExportResult("pdf", "Report_export_CLF00001_R3.pdf", byteArrayOf())
        every {
            jemsPluginRegistry.get(ProjectReportExportPlugin::class, REPORT_EXPORT_PLUGIN_KEY)
        } returns projectReportExportPlugin

        every {
            projectReportExport.exportReport(
                PROJECT_ID,
                REPORT_ID,
                REPORT_EXPORT_PLUGIN_KEY,
                EXPORT_LANGUAGE,
                INPUT_LANGUAGE,
                DATE_TIME_NOW
            )
        } returns exportResult

        every {
            getLogosInteractor.getLogos()
        } returns listOf()

        Assertions.assertThat(
            projectReportExport.exportReport(
                PROJECT_ID,
                REPORT_ID,
                REPORT_EXPORT_PLUGIN_KEY,
                EXPORT_LANGUAGE,
                INPUT_LANGUAGE,
                DATE_TIME_NOW
            )
        ).isEqualTo(exportResult)
    }

    @Test
    fun `Project report export exception`() {
        val exception = ProjectReportExportException(IOException())
        every { jemsPluginRegistry.get(ProjectReportExportPlugin::class, REPORT_EXPORT_PLUGIN_KEY) } throws exception

        assertThrows<ProjectReportExportException> {
            projectReportExport.exportReport(
                PROJECT_ID,
                REPORT_ID,
                REPORT_EXPORT_PLUGIN_KEY,
                EXPORT_LANGUAGE,
                INPUT_LANGUAGE,
                DATE_TIME_NOW
            )
        }
    }
}
