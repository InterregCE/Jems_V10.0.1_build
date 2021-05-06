package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.plugin.contract.JemsPlugin

const val samplePluginTypeOneKey= "key-1"
const val samplePluginTypeTwoKey= "key-2"

class SamplePluginTypeOne : JemsPlugin {
    override fun getKey(): String =
        samplePluginTypeOneKey

    override fun getDescription(): String =
        "description of type one"

    override fun getName(): String =
        "name-1"

    override fun getVersion(): String =
        "1.0.0"
}

class SamplePluginTypeTwo : JemsPlugin {
    override fun getKey(): String =
        samplePluginTypeTwoKey

    override fun getDescription(): String =
        "description of type two"

    override fun getName(): String =
        "name-2"

    override fun getVersion(): String =
        "1.1.0"
}
