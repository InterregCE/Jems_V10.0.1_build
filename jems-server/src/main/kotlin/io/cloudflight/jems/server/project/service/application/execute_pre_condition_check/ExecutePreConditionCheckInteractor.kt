package io.cloudflight.jems.server.project.service.application.execute_pre_condition_check

import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult

interface ExecutePreConditionCheckInteractor {
    fun execute(projectId: Long): PreConditionCheckResult
}
