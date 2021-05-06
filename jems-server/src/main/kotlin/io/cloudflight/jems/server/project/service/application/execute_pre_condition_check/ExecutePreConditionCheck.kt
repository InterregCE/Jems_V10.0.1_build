package io.cloudflight.jems.server.project.service.application.execute_pre_condition_check

import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExecutePreConditionCheck(private val jemsPluginRegistry: JemsPluginRegistry) :
    ExecutePreConditionCheckInteractor {

    @ExceptionWrapper(ExecutePreConditionCheckException::class)
    @Transactional(readOnly = true)
    override fun execute(projectId: Long): PreConditionCheckResult =
        // todo key should be fetched from call settings for the project when it is added
        jemsPluginRegistry.get(PreConditionCheckPlugin::class, key = "standard-pre-condition-check-plugin")
            .check(projectId)
}
