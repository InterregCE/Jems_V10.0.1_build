package io.cloudflight.jems.server.plugin.controller

import io.cloudflight.jems.api.plugin.dto.PluginTypeDTO
import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.plugin.contract.export.ApplicationFormExportPlugin
import io.cloudflight.jems.plugin.contract.export.BudgetExportPlugin
import io.cloudflight.jems.plugin.contract.export.ProgrammeDataExportPlugin
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerControlReportCertificatePlugin
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerControlReportExportPlugin
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerReportExportPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportSamplingCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportPartnerCheckPlugin
import io.cloudflight.jems.server.plugin.UnknownPluginTypeException


fun JemsPlugin.toPluginType(): PluginTypeDTO =
    when (this) {
        is ProgrammeDataExportPlugin -> PluginTypeDTO.PROGRAMME_DATA_EXPORT
        is PreConditionCheckPlugin -> PluginTypeDTO.PRE_SUBMISSION_CHECK
        is BudgetExportPlugin -> PluginTypeDTO.BUDGET_EXPORT
        is ApplicationFormExportPlugin -> PluginTypeDTO.APPLICATION_FORM_EXPORT
        is ReportPartnerCheckPlugin -> PluginTypeDTO.REPORT_PARTNER_CHECK
        is PartnerControlReportCertificatePlugin -> PluginTypeDTO.PARTNER_CONTROL_REPORT_CERTIFICATE
        is PartnerControlReportExportPlugin -> PluginTypeDTO.PARTNER_CONTROL_REPORT_EXPORT
        is PartnerReportExportPlugin -> PluginTypeDTO.PARTNER_REPORT_EXPORT
        is ControlReportSamplingCheckPlugin -> PluginTypeDTO.PARTNER_CONTROL_RISK_BASED_SAMPLING
        else -> throw UnknownPluginTypeException(this.javaClass.name)
    }

fun PluginTypeDTO.toType() =
    when (this) {
        PluginTypeDTO.PROGRAMME_DATA_EXPORT -> ProgrammeDataExportPlugin::class
        PluginTypeDTO.PRE_SUBMISSION_CHECK -> PreConditionCheckPlugin::class
        PluginTypeDTO.REPORT_PARTNER_CHECK -> ReportPartnerCheckPlugin::class
        PluginTypeDTO.BUDGET_EXPORT -> BudgetExportPlugin::class
        PluginTypeDTO.APPLICATION_FORM_EXPORT -> ApplicationFormExportPlugin::class
        PluginTypeDTO.PARTNER_CONTROL_REPORT_CERTIFICATE -> PartnerControlReportCertificatePlugin::class
        PluginTypeDTO.PARTNER_CONTROL_REPORT_EXPORT -> PartnerControlReportExportPlugin::class
        PluginTypeDTO.PARTNER_REPORT_EXPORT -> PartnerReportExportPlugin::class
        PluginTypeDTO.PARTNER_CONTROL_RISK_BASED_SAMPLING -> ControlReportSamplingCheckPlugin::class
        PluginTypeDTO.ALL -> JemsPlugin::class
        else -> throw UnknownPluginTypeException(this.javaClass.name)
    }
