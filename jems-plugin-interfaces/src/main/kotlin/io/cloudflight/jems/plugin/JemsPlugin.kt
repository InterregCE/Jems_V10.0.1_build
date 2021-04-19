package io.cloudflight.jems.plugin

interface JemsPlugin {

    fun getKey(): String =
        javaClass.canonicalName

    fun getName(): String

    fun getVersion(): String

    fun getDescription(): String

}
