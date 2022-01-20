package io.cloudflight.jems.api.plugin.dto

data class PluginInfoDTO(
    val type: PluginTypeDTO,
    val key: String,
    val name: String,
    val version: String,
    val description: String
)
