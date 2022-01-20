package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.server.UnitTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class JemsPluginRegistryMapImplTest : UnitTest() {

    @Test
    fun `should register plugins when there is no problem`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val samplePlugin = PreConditionCheckSamplePlugin()
        jemsPluginRegistry.registerPlugins(listOf(samplePlugin))
        assertThat(jemsPluginRegistry.list(JemsPlugin::class)).containsExactly(samplePlugin)
    }

    @Test
    fun `should return plugin for provided key when there is no problem`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val samplePlugin = PreConditionCheckSamplePlugin()
        jemsPluginRegistry.registerPlugins(listOf(samplePlugin))
        assertThat(jemsPluginRegistry.get(PreConditionCheckSamplePlugin::class, PreConditionCheckSamplePluginKey)).isEqualTo(samplePlugin)
    }

    @Test
    fun `should throw exception when plugin type is not valid for provided key`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val secondPlugin = ApplicationFormExportSamplePlugin()
        jemsPluginRegistry.registerPlugins(listOf(secondPlugin))
        assertThrows<PluginTypeIsNotValidException> { jemsPluginRegistry.get(PreConditionCheckSamplePlugin::class, ApplicationFormExportSamplePluginKey) }
    }

    @TestFactory
    fun `should throw exception when plugin key is null or blank`() =
        listOf("", null, "  ").map { pluginKey ->
            DynamicTest.dynamicTest("should throw exception when plugin key is '$pluginKey'") {
                val jemsPluginRegistry = JemsPluginRegistryMapImpl()
                assertThrows<PluginKeyIsNullOrBlankException> {
                    jemsPluginRegistry.get(PreConditionCheckSamplePlugin::class, pluginKey)
                }
            }
        }


    @Test
    fun `should throw exception when plugin is not found for provided key`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val secondPlugin = ApplicationFormExportSamplePlugin()
        jemsPluginRegistry.registerPlugins(listOf(secondPlugin))
        assertThrows<PluginNotFoundException> { jemsPluginRegistry.get(PreConditionCheckSamplePlugin::class, PreConditionCheckSamplePluginKey) }
    }

    @Test
    fun `should filter available plugins based on type`() {
        val jemsPluginRegistry = JemsPluginRegistryMapImpl()
        val samplePluginTypeOne = PreConditionCheckSamplePlugin()
        val secondPluginTypeTwo = ApplicationFormExportSamplePlugin()
        jemsPluginRegistry.registerPlugins(listOf(secondPluginTypeTwo,samplePluginTypeOne))
        assertThat(jemsPluginRegistry.list(PreConditionCheckSamplePlugin::class)).containsExactly(samplePluginTypeOne)
    }
}
