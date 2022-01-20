package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.repository.PluginStatusRepository
import io.cloudflight.jems.server.project.authorization.CanSubmitApplication
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.project.service.unsuccessfulProjectSubmission
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubmitApplication(
    private val projectPersistence: ProjectPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val auditPublisher: ApplicationEventPublisher,
    //TODO should be replaced with persistence after MP2-1510
    private val pluginStatusRepository: PluginStatusRepository
) : SubmitApplicationInteractor {

    @CanSubmitApplication
    @Transactional
    @ExceptionWrapper(SubmitApplicationException::class)
    override fun submit(projectId: Long): ApplicationStatus =
        projectPersistence.getProjectCallSettings(projectId).let { callSettings ->
            projectPersistence.getProjectSummary(projectId).let { projectSummary ->
                when {
                    callSettings.endDateStep1 == null || projectSummary.status.isInStep2() ->
                        preCheckAndSubmit(projectSummary, callSettings.preSubmissionCheckPluginKey)
                    else -> submitApplication(projectSummary)
                }
            }
        }

    private fun preCheckAndSubmit(projectSummary: ProjectSummary, pluginKey: String?) =
        if (isPluginEnabled(pluginKey))
            jemsPluginRegistry.get(PreConditionCheckPlugin::class, pluginKey).check(projectSummary.id).let {
                when {
                    it.isSubmissionAllowed -> this.submitApplication(projectSummary)
                    else -> {
                        auditPublisher.publishEvent(unsuccessfulProjectSubmission(this, projectSummary))
                        throw SubmitApplicationPreConditionCheckFailedException()
                    }
                }
            }
        else submitApplication(projectSummary)

    private fun submitApplication(projectSummary: ProjectSummary): ApplicationStatus {
        return applicationStateFactory.getInstance(projectSummary).submit().also {
            auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
        }
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
