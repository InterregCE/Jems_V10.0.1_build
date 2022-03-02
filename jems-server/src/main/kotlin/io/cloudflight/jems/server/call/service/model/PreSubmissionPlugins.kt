package io.cloudflight.jems.server.call.service.model

data class PreSubmissionPlugins (
    val pluginKey: String,
    val firstStepPluginKey: String? = "",
    val callHasTwoSteps: Boolean = false
)
