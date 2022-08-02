package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.plugin.contract.export.ApplicationFormExportPlugin
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import java.time.LocalDateTime

const val PreConditionCheckSamplePluginKey = "key-1"
const val ApplicationFormExportSamplePluginKey = "key-2"

class PreConditionCheckSamplePlugin : PreConditionCheckPlugin {
    override fun getKey(): String =
        PreConditionCheckSamplePluginKey

    override fun check(projectId: Long) =
        PreConditionCheckResult(emptyList(), true)

    override fun getDescription(): String =
        "description of PreConditionCheckSamplePlugin"

    override fun getName(): String =
        "name-1"

    override fun getVersion(): String =
        "1.0.0"
}

class ApplicationFormExportSamplePlugin : ApplicationFormExportPlugin {
    override fun getKey(): String =
        ApplicationFormExportSamplePluginKey

    override fun export(
        projectId: Long,
        exportLanguage: SystemLanguageData,
        dataLanguage: SystemLanguageData,
        localDateTime: LocalDateTime,
        version: String?,
        logo: String?
    ) =
        ExportResult("", "", byteArrayOf())

    override fun getDescription(): String =
        "description of ApplicationFormExportSamplePlugin"

    override fun getName(): String =
        "name-2"

    override fun getVersion(): String =
        "1.1.0"
}

class UnknownSamplePlugin : JemsPlugin {
    override fun getKey(): String =
        "unknown"

    override fun getDescription(): String =
        "description of UnknownSamplePlugin"

    override fun getName(): String =
        "name-3"

    override fun getVersion(): String =
        "1.1.0"
}
