package io.cloudflight.jems.server.plugin

import ch.qos.logback.classic.Logger
import io.cloudflight.jems.plugin.contract.JemsPlugin
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class PluginRegistry {

    @Autowired(required = false)
    private var availablePlugins: List<JemsPlugin>? = null

    @PublishedApi
    internal val registry: MutableMap<String, JemsPlugin> = mutableMapOf()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PluginRegistry::class.java) as Logger
    }

    final inline fun <reified T : JemsPlugin> get(key: String): T =
        registry[key]?.let {
            if (it is T) {
                it
            } else {
                throw PluginTypeIsNotValidException(key)
            }
        } ?: throw PluginNotFoundException(key)

    final inline fun <reified T : JemsPlugin> list(): List<T> =
        registry.values.filterIsInstance<T>().toList()

    @PostConstruct
    private fun registerPlugins() {
        if (availablePlugins != null) {
            for (plugin in availablePlugins!!) {
                registry[plugin.getKey()] = plugin
                logger.info("plugin with key:${plugin.getKey()} and name:${plugin.getName()} is registered")
            }
        }
    }
}

