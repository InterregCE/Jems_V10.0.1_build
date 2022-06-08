package io.cloudflight.jems.server.project.service.application.execute_pre_condition_check

import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.plugin.contract.pre_condition_check.models.PreConditionCheckResult
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.repository.PluginStatusRepository
import io.cloudflight.jems.server.project.authorization.CanCheckApplicationForm
import io.cloudflight.jems.server.project.service.ProjectPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class ExecutePreConditionCheck(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val projectPersistence: ProjectPersistence,
    //TODO should be replaced with persistence after MP2-1510
    private val pluginStatusRepository: PluginStatusRepository
) : ExecutePreConditionCheckInteractor {

    @ExceptionWrapper(ExecutePreConditionCheckException::class)
    @Transactional(readOnly = true)
    @CanCheckApplicationForm
    override fun execute(projectId: Long): PreConditionCheckResult =
        projectPersistence.getProjectCallSettings(projectId).let { callSettings ->
            if (isPluginEnabled(callSettings.preSubmissionCheckPluginKey))
                projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                    when {
                       projectSummary.status.isInStep1() ->
                            jemsPluginRegistry.get(
                                PreConditionCheckPlugin::class, key = callSettings.firstStepPreSubmissionCheckPluginKey
                            ).check(projectId)

                       projectSummary.status.isInStep2() ->
                            jemsPluginRegistry.get(
                                PreConditionCheckPlugin::class, key = callSettings.preSubmissionCheckPluginKey
                            ).check(projectId)
                    else -> throw PreConditionCheckCannotBeExecutedException()
                    }
                }
            else PreConditionCheckResult(emptyList(), true)
        }


    private fun isPluginEnabled(pluginKey: String?): Boolean =
        if (pluginKey.isNullOrBlank()) false
        else pluginStatusRepository.findById(pluginKey).let {
            when {
                it.isPresent -> it.get().enabled
                else -> true
            }
        }

}
