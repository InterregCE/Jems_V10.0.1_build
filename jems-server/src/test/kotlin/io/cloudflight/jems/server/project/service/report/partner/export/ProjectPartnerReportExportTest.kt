package io.cloudflight.jems.server.project.service.report.partner.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerReportExportPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.time.LocalDateTime

class ProjectPartnerReportExportTest : UnitTest() {

    companion object {
        const val PARTNER_ID = 2L
        const val PROJECT_ID = 1L
        const val REPORT_ID = 3L
        private const val REPORT_EXPORT_PLUGIN_KEY = "standard-partner-report-export-plugin"
        private const val REPORT_BUDGET_EXPORT_PLUGIN_KEY = "standard-partner-report-export-budget-plugin"

        private val EXPORT_LANGUAGE = SystemLanguage.EN
        private val INPUT_LANGUAGE = SystemLanguage.EN
        private val DATE_TIME_NOW = LocalDateTime.now()
    }

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var partnerReportExportPlugin: PartnerReportExportPlugin

    @MockK
    lateinit var getLogosInteractor: GetLogosInteractor

    @InjectMockKs
    lateinit var projectPartnerReportExport: ProjectPartnerReportExport

    @Test
    fun `Export partner report pdf - ok`() {
        val exportResult = ExportResult("pdf", "Report_export_CLF00001_PP2_R3.pdf", byteArrayOf())
        every {
            jemsPluginRegistry.get(PartnerReportExportPlugin::class, REPORT_EXPORT_PLUGIN_KEY)
        } returns partnerReportExportPlugin

        every {
            projectPartnerReportExport.exportReport(
                PARTNER_ID,
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

        assertThat(
            projectPartnerReportExport.exportReport(
                PARTNER_ID,
                REPORT_ID,
                REPORT_EXPORT_PLUGIN_KEY,
                EXPORT_LANGUAGE,
                INPUT_LANGUAGE,
                DATE_TIME_NOW
            )
        ).isEqualTo(exportResult)
    }


    @Test
    fun `Export partner report spreadsheet - ok`() {
        val exportResult = ExportResult("xlsx", "Report_export_CLF00001_PP2_R3.xlsx", byteArrayOf())
        every {
            jemsPluginRegistry.get(PartnerReportExportPlugin::class, REPORT_BUDGET_EXPORT_PLUGIN_KEY)
        } returns partnerReportExportPlugin

        every {
            projectPartnerReportExport.exportReport(
                PARTNER_ID,
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

        assertThat(
            projectPartnerReportExport.exportReport(
                PARTNER_ID,
                REPORT_ID,
                REPORT_EXPORT_PLUGIN_KEY,
                EXPORT_LANGUAGE,
                INPUT_LANGUAGE,
                DATE_TIME_NOW
            )
        ).isEqualTo(exportResult)
    }


    @Test
    fun `Partner report export exception`() {
        val exception = ProjectPartnerReportExportException(IOException())
        every { jemsPluginRegistry.get(PartnerReportExportPlugin::class, REPORT_EXPORT_PLUGIN_KEY) } throws exception

        assertThrows<ProjectPartnerReportExportException> {
            projectPartnerReportExport.exportReport(
                PARTNER_ID,
                REPORT_ID,
                REPORT_EXPORT_PLUGIN_KEY,
                EXPORT_LANGUAGE,
                INPUT_LANGUAGE,
                DATE_TIME_NOW
            )
        }
    }
}