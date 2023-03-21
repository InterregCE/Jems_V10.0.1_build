package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.plugin.contract.export.ApplicationFormExportPlugin
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportSamplingCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportPartnerCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.ControlReportSamplingCheckResult
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import java.time.LocalDateTime

const val PreConditionCheckSamplePluginKey = "key-1"
const val ApplicationFormExportSamplePluginKey = "key-2"
const val ReportCheckPluginKey = "key-3"
const val ControlReportSamplingCheckPluginKey = "key-4"

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

class ReportPartnerCheckSamplePlugin : ReportPartnerCheckPlugin {
    override fun getKey(): String =
        ReportCheckPluginKey

    override fun check(partnerId: Long, reportId: Long) =
        PreConditionCheckResult(emptyList(), true)

    override fun getDescription(): String =
        "description of ReportPartnerCheckSamplePlugin"

    override fun getName(): String =
        "name-3"

    override fun getVersion(): String =
        "1.0.0"
}

class ControlReportSamplingCheckSamplePlugin : ControlReportSamplingCheckPlugin {
    override fun getKey(): String =
        ControlReportSamplingCheckPluginKey

    override fun check(partnerId: Long, reportId: Long) =
        ControlReportSamplingCheckResult (setOf())

    override fun getDescription(): String =
        "description of ControlReportSamplingCheckPlugin"

    override fun getName(): String =
        "name-4"

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
        version: String?,
        logo: String?,
        localDateTime: LocalDateTime?
    ) =  ExportResult("", "", byteArrayOf())

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
