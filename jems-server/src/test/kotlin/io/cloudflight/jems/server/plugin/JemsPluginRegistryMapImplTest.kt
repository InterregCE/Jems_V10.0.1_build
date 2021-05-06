package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.server.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class JemsPluginRegistryMapImplTest : UnitTest() {

    @Test
    fun `should register plugins when there is no problem`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val samplePlugin = SamplePluginTypeOne()
        jemsPluginRegistry.registerPlugins(listOf(samplePlugin))
        assertThat(jemsPluginRegistry.list(JemsPlugin::class)).containsExactly(samplePlugin)
    }

    @Test
    fun `should return plugin for provided key when there is no problem`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val samplePlugin = SamplePluginTypeOne()
        jemsPluginRegistry.registerPlugins(listOf(samplePlugin))
        assertThat(jemsPluginRegistry.get(SamplePluginTypeOne::class, samplePluginTypeOneKey)).isEqualTo(samplePlugin)
    }

    @Test
    fun `should throw exception when plugin type is not valid for provided key`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val secondPlugin = SamplePluginTypeTwo()
        jemsPluginRegistry.registerPlugins(listOf(secondPlugin))
        assertThrows<PluginTypeIsNotValidException> { jemsPluginRegistry.get(SamplePluginTypeOne::class, samplePluginTypeTwoKey) }
    }

    @Test
    fun `should throw exception when plugin is not found for provided key`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val secondPlugin = SamplePluginTypeTwo()
        jemsPluginRegistry.registerPlugins(listOf(secondPlugin))
        assertThrows<PluginNotFoundException> { jemsPluginRegistry.get(SamplePluginTypeOne::class, samplePluginTypeOneKey) }
    }

    @Test
    fun `should filter available plugins based on type`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val samplePluginTypeOne = SamplePluginTypeOne()
        val secondPluginTypeTwo = SamplePluginTypeTwo()
        jemsPluginRegistry.registerPlugins(listOf(secondPluginTypeTwo,samplePluginTypeOne))
        assertThat(jemsPluginRegistry.list(SamplePluginTypeOne::class)).containsExactly(samplePluginTypeOne)
    }
}
