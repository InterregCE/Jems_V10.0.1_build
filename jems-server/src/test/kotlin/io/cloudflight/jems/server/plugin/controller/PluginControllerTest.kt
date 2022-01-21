package io.cloudflight.jems.server.plugin.controller

import io.cloudflight.jems.api.plugin.dto.PluginInfoDTO
import io.cloudflight.jems.api.plugin.dto.PluginTypeDTO
import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.ApplicationFormExportSamplePlugin
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.PreConditionCheckSamplePlugin
import io.cloudflight.jems.server.plugin.UnknownPluginTypeException
import io.cloudflight.jems.server.plugin.UnknownSamplePlugin
import io.cloudflight.jems.server.plugin.services.update_plugin_status.UpdatePluginStatusInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class PluginControllerTest : UnitTest() {

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @MockK
    lateinit var updatePluginStatus: UpdatePluginStatusInteractor

    @InjectMockKs
    lateinit var pluginController: PluginController

    @Test
    fun `should return list of plugin's information`() {
        val pluginList = listOf(PreConditionCheckSamplePlugin(), ApplicationFormExportSamplePlugin())
        every { jemsPluginRegistry.list(JemsPlugin::class) } returns pluginList
        val availablePluginList = pluginController.getAvailablePluginList(PluginTypeDTO.ALL)
        assertThat(availablePluginList).isEqualTo(pluginList.map {
            PluginInfoDTO(it.toPluginType(), it.getKey(), it.getName(), it.getVersion(), it.getDescription())
        })
    }

    @Test
    fun `should throw exception when plugin type is not known`() {
        val pluginList = listOf(PreConditionCheckSamplePlugin(), ApplicationFormExportSamplePlugin(), UnknownSamplePlugin())
        every { jemsPluginRegistry.list(JemsPlugin::class) } returns pluginList
        assertThrows<UnknownPluginTypeException> { pluginController.getAvailablePluginList(PluginTypeDTO.ALL)}
    }
}
