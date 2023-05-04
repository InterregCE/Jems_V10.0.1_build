package io.cloudflight.jems.server.call.service.model

data class PreSubmissionPlugins (
    val pluginKey: String,
    val firstStepPluginKey: String = "",
    val reportPartnerCheckPluginKey: String,
    val reportProjectCheckPluginKey: String,
    val controlReportPartnerCheckPluginKey: String,
    val controlReportSamplingCheckPluginKey: String,
) {
    fun getDiff(old: PreSubmissionPlugins? = null): Map<String, Pair<String?, String>> {
        val changes = mutableMapOf<String, Pair<String?, String>>()

        if (old?.firstStepPluginKey != firstStepPluginKey)
            changes["PreSubmissionCheckFirstStep"] = Pair(old?.firstStepPluginKey, firstStepPluginKey)

        if (old?.pluginKey != pluginKey)
            changes["PreSubmissionCheck"] = Pair(old?.pluginKey, pluginKey)

        if (old?.reportPartnerCheckPluginKey != reportPartnerCheckPluginKey)
            changes["PreSubmissionCheckPartnerReport"] = Pair(old?.reportPartnerCheckPluginKey, reportPartnerCheckPluginKey)

        if (old?.reportProjectCheckPluginKey != reportProjectCheckPluginKey)
            changes["PreSubmissionCheckProjectReport"] = Pair(old?.reportProjectCheckPluginKey, reportProjectCheckPluginKey)

        if (old?.controlReportPartnerCheckPluginKey != controlReportPartnerCheckPluginKey)
            changes["PreSubmissionCheckPartnerControlReport"] = Pair(old?.controlReportPartnerCheckPluginKey, controlReportPartnerCheckPluginKey)

        if (old?.controlReportSamplingCheckPluginKey != controlReportSamplingCheckPluginKey)
            changes["PreSubmissionCheckControlReportSampling"] = Pair(old?.controlReportSamplingCheckPluginKey, controlReportSamplingCheckPluginKey)

        return changes
    }
}
