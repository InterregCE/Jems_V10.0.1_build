package io.cloudflight.jems.server.plugin.controller

import io.cloudflight.jems.api.plugin.dto.PluginInfoDTO
import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.SamplePluginTypeOne
import io.cloudflight.jems.server.plugin.SamplePluginTypeTwo
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class PluginControllerTest : UnitTest() {

    @MockK
    lateinit var jemsPluginRegistry: JemsPluginRegistry

    @InjectMockKs
    lateinit var pluginController: PluginController

    @Test
    fun `should return list of plugin's information`() {
        val pluginList = listOf(SamplePluginTypeOne(), SamplePluginTypeTwo())
        every { jemsPluginRegistry.list(JemsPlugin::class) } returns pluginList
        val availablePluginList = pluginController.getAvailablePluginList()
        assertThat(availablePluginList).isEqualTo(pluginList.map {
            PluginInfoDTO(it.getKey(), it.getName(), it.getVersion(), it.getDescription())
        })

    }
}
