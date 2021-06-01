package io.cloudflight.jems.server.plugin.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.validation.constraints.NotNull

@Entity(name = "plugin_status")
data class PluginStatusEntity(
    @Id
    @field:NotNull
    val pluginKey: String,

    @Column
    @field:NotNull
    var enabled: Boolean = true
)
