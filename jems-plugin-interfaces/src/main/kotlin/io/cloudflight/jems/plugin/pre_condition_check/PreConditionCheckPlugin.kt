package io.cloudflight.jems.plugin.pre_condition_check

import io.cloudflight.jems.plugin.JemsPlugin
import io.cloudflight.jems.plugin.pre_condition_check.models.PreConditionCheckResult

interface PreConditionCheckPlugin : JemsPlugin {

    fun check(projectId: Long): PreConditionCheckResult
}
