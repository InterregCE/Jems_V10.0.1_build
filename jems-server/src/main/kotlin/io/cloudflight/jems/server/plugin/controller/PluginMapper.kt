package io.cloudflight.jems.server.plugin.controller

import io.cloudflight.jems.api.plugin.dto.PluginTypeDTO
import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.plugin.contract.export.ApplicationFormExportPlugin
import io.cloudflight.jems.plugin.contract.export.BudgetExportPlugin
import io.cloudflight.jems.plugin.contract.export.PaymentApplicationToEcAuditExportPlugin
import io.cloudflight.jems.plugin.contract.export.ProgrammeDataExportPlugin
import io.cloudflight.jems.plugin.contract.export.checklist.ChecklistExportPlugin
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerControlReportCertificatePlugin
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerControlReportExportPlugin
import io.cloudflight.jems.plugin.contract.export.partner.report.PartnerReportExportPlugin
import io.cloudflight.jems.plugin.contract.export.project.report.ProjectReportExportPlugin
import io.cloudflight.jems.plugin.contract.export.project.report.ProjectReportVerificationCertificatePlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportPartnerCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ControlReportSamplingCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportPartnerCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.ReportProjectCheckPlugin
import io.cloudflight.jems.server.plugin.UnknownPluginTypeException


fun JemsPlugin.toPluginType(): PluginTypeDTO =
    when (this) {
        is ProgrammeDataExportPlugin -> PluginTypeDTO.PROGRAMME_DATA_EXPORT
        is PreConditionCheckPlugin -> PluginTypeDTO.PRE_SUBMISSION_CHECK
        is BudgetExportPlugin -> PluginTypeDTO.BUDGET_EXPORT
        is ApplicationFormExportPlugin -> PluginTypeDTO.APPLICATION_FORM_EXPORT
        is ReportPartnerCheckPlugin -> PluginTypeDTO.REPORT_PARTNER_CHECK
        is ReportProjectCheckPlugin -> PluginTypeDTO.REPORT_PROJECT_CHECK
        is PartnerControlReportCertificatePlugin -> PluginTypeDTO.PARTNER_CONTROL_REPORT_CERTIFICATE
        is PartnerControlReportExportPlugin -> PluginTypeDTO.PARTNER_CONTROL_REPORT_EXPORT
        is ControlReportPartnerCheckPlugin -> PluginTypeDTO.PARTNER_CONTROL_REPORT_CHECK
        is PartnerReportExportPlugin -> PluginTypeDTO.PARTNER_REPORT_EXPORT
        is ControlReportSamplingCheckPlugin -> PluginTypeDTO.PARTNER_CONTROL_RISK_BASED_SAMPLING
        is ProjectReportExportPlugin -> PluginTypeDTO.REPORT_PROJECT_EXPORT
        is ProjectReportVerificationCertificatePlugin -> PluginTypeDTO.REPORT_PROJECT_EXPORT
        is ChecklistExportPlugin -> PluginTypeDTO.CHECKLIST_EXPORT
        is PaymentApplicationToEcAuditExportPlugin -> PluginTypeDTO.PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT
        else -> throw UnknownPluginTypeException(this.javaClass.name)
    }

fun PluginTypeDTO.toType() =
    when (this) {
        PluginTypeDTO.PROGRAMME_DATA_EXPORT -> ProgrammeDataExportPlugin::class
        PluginTypeDTO.PRE_SUBMISSION_CHECK -> PreConditionCheckPlugin::class
        PluginTypeDTO.REPORT_PARTNER_CHECK -> ReportPartnerCheckPlugin::class
        PluginTypeDTO.REPORT_PROJECT_CHECK -> ReportProjectCheckPlugin::class
        PluginTypeDTO.BUDGET_EXPORT -> BudgetExportPlugin::class
        PluginTypeDTO.APPLICATION_FORM_EXPORT -> ApplicationFormExportPlugin::class
        PluginTypeDTO.PARTNER_CONTROL_REPORT_CERTIFICATE -> PartnerControlReportCertificatePlugin::class
        PluginTypeDTO.PARTNER_CONTROL_REPORT_EXPORT -> PartnerControlReportExportPlugin::class
        PluginTypeDTO.PARTNER_CONTROL_REPORT_CHECK -> ControlReportPartnerCheckPlugin::class
        PluginTypeDTO.PARTNER_REPORT_EXPORT -> PartnerReportExportPlugin::class
        PluginTypeDTO.PARTNER_CONTROL_RISK_BASED_SAMPLING -> ControlReportSamplingCheckPlugin::class
        PluginTypeDTO.REPORT_PROJECT_EXPORT -> ProjectReportExportPlugin::class
        PluginTypeDTO.REPORT_PROJECT_VERIFICATION_CERTIFICATE -> ProjectReportVerificationCertificatePlugin::class
        PluginTypeDTO.CHECKLIST_EXPORT -> ChecklistExportPlugin::class
        PluginTypeDTO.PAYMENT_APPLICATION_TO_EC_AUDIT_EXPORT -> PaymentApplicationToEcAuditExportPlugin::class
        PluginTypeDTO.ALL -> JemsPlugin::class
        else -> throw UnknownPluginTypeException(this.javaClass.name)
    }
