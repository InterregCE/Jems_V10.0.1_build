package io.cloudflight.jems.server.plugin.controller

import io.cloudflight.jems.api.plugin.dto.PluginTypeDTO
import io.cloudflight.jems.plugin.contract.JemsPlugin
import io.cloudflight.jems.plugin.contract.export.ApplicationFormExportPlugin
import io.cloudflight.jems.plugin.contract.export.BudgetExportPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.server.plugin.UnknownPluginTypeException


fun JemsPlugin.toPluginType(): PluginTypeDTO =
    when (this) {
        is PreConditionCheckPlugin -> PluginTypeDTO.PRE_SUBMISSION_CHECK
        is BudgetExportPlugin -> PluginTypeDTO.BUDGET_EXPORT
        is ApplicationFormExportPlugin -> PluginTypeDTO.APPLICATION_FORM_EXPORT
        else -> throw UnknownPluginTypeException(this.javaClass.name)
    }

fun PluginTypeDTO.toType() =
    when (this) {
        PluginTypeDTO.PRE_SUBMISSION_CHECK -> PreConditionCheckPlugin::class
        PluginTypeDTO.BUDGET_EXPORT -> BudgetExportPlugin::class
        PluginTypeDTO.APPLICATION_FORM_EXPORT -> ApplicationFormExportPlugin::class
        PluginTypeDTO.ALL -> JemsPlugin::class
    }
