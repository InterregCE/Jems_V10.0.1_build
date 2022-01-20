package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.plugin.contract.JemsPlugin
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
interface JemsPluginRegistry {

    fun registerPlugins(plugins: List<JemsPlugin>)

    fun <T : JemsPlugin> get(type: KClass<T>, key: String?): T

    fun <T : JemsPlugin> list(type: KClass<T>): List<T>

}
