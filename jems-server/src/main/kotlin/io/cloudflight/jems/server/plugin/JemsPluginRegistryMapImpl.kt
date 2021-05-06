package io.cloudflight.jems.server.plugin

import ch.qos.logback.classic.Logger
import io.cloudflight.jems.plugin.contract.JemsPlugin
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import kotlin.reflect.KClass

@Service
class JemsPluginRegistryMapImpl : JemsPluginRegistry {

    @Autowired(required = false)
    private var availablePlugins: List<JemsPlugin>? = null

    private val registry: MutableMap<String, JemsPlugin> = mutableMapOf()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(JemsPluginRegistryMapImpl::class.java) as Logger
    }

    @PostConstruct
    private fun registerPlugins() {
        if (availablePlugins != null) {
            registerPlugins(availablePlugins!!)
        }
    }

    override fun registerPlugins(plugins: List<JemsPlugin>) {
        plugins.forEach {
            registry[it.getKey()] = it
            logger.info("plugin with key:${it.getKey()} and name:${it.getName()} is registered")
        }
    }

    override fun <T : JemsPlugin> get(type: KClass<T>, key: String): T =
        registry[key]?.let {
            try {
                type.javaObjectType.cast(it)
            } catch (e: Throwable) {
                throw PluginTypeIsNotValidException(key)
            }
        } ?: throw PluginNotFoundException(key)

    override fun <T : JemsPlugin> list(type: KClass<T>): List<T> =
        registry.values.mapNotNull {
            try {
                type.javaObjectType.cast(it)
            } catch (e: Throwable) {
                null
            }
        }.toList()

}
