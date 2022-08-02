package io.cloudflight.jems.server.project.service.export.export_application_form

import io.cloudflight.jems.api.common.dto.LogoDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ApplicationFormExportPlugin
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.resources.service.get_logos.GetLogosInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class ExportApplicationFormTest : UnitTest() {

    private val pluginKey = "standard-application-form-export-plugin"

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var getLogosInteractor: GetLogosInteractor

    @MockK
    lateinit var applicationFormExportPlugin: ApplicationFormExportPlugin

    @InjectMockKs
    lateinit var exportApplicationForm: ExportApplicationForm

    @Test
    fun `should execute export application form plugin when there is no problem`() {
        val exportResult = ExportResult("pdf", "filename", byteArrayOf())
        val localDateTime = LocalDateTime.now()
        every {
            jemsPluginRegistry.get(ApplicationFormExportPlugin::class, pluginKey)
        } returns applicationFormExportPlugin
        every {
            applicationFormExportPlugin.export(1L, SystemLanguageData.EN, SystemLanguageData.DE, localDateTime)
        } returns exportResult
        every {
            getLogosInteractor.getLogos()
        } returns listOf<LogoDTO>()

        assertThat(exportApplicationForm.export(1L, SystemLanguage.EN, SystemLanguage.DE, localDateTime))
            .isEqualTo(exportResult)
    }
}
