package io.cloudflight.jems.server.call.service.model

data class PreSubmissionPlugins (
    val pluginKey: String,
    val firstStepPluginKey: String? = "",
    val reportPartnerCheckPluginKey: String,
) {
    fun getDiff(old: PreSubmissionPlugins? = null): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        if (old == null || firstStepPluginKey != old.firstStepPluginKey)
            changes["PreSubmissionCheckFirstStep"] = Pair(old?.firstStepPluginKey, firstStepPluginKey)

        if (old == null || pluginKey != old.pluginKey)
            changes["PreSubmissionCheck"] = Pair(old?.pluginKey, pluginKey)

        if (old == null || reportPartnerCheckPluginKey != old.reportPartnerCheckPluginKey)
            changes["PreSubmissionCheckPartnerReport"] = Pair(old?.reportPartnerCheckPluginKey, reportPartnerCheckPluginKey)

        return changes
    }
}
