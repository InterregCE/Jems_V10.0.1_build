package io.cloudflight.jems.api.call.dto

data class PreSubmissionPluginsDTO(
    val pluginKey: String,
    val firstStepPluginKey: String?,
    val callHasTwoSteps: Boolean
)
