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

const val pluginKey = "standard-pre-condition-check-plugin"

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
        if (isPluginEnabled())
            projectPersistence.getProjectCallSettings(projectId).let { callSettings ->
                projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                    when {
                        // todo pluginKey should be fetched from call settings for the project when it is added
                        callSettings.endDateStep1 == null || projectSummary.status.isInStep2() ->
                            jemsPluginRegistry.get(
                                PreConditionCheckPlugin::class, key = pluginKey
                            ).check(projectId)
                        else -> throw PreConditionCheckCannotBeExecutedException()
                    }
                }
            }
        else PreConditionCheckResult(emptyList(), true)


    private fun isPluginEnabled(): Boolean =
        pluginStatusRepository.findById(pluginKey).let {
            when {
                it.isPresent -> it.get().enabled
                else -> true
            }
        }

}
