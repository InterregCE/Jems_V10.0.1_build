package io.cloudflight.jems.server.plugin.services.update_plugin_status


interface UpdatePluginStatusInteractor {
    fun enable(pluginKey: String)
    fun disable(pluginKey: String)
}
