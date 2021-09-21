package io.cloudflight.jems.server.project.service.application.submit_application

import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.plugin.repository.PluginStatusRepository
import io.cloudflight.jems.server.project.authorization.CanSubmitApplication
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.execute_pre_condition_check.pluginKey
import io.cloudflight.jems.server.project.service.application.workflow.ApplicationStateFactory
import io.cloudflight.jems.server.project.service.create_new_project_version.CreateNewProjectVersionInteractor
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.project.service.unsuccessfulProjectSubmission
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubmitApplication(
    private val projectPersistence: ProjectPersistence,
    private val projectWorkflowPersistence: ProjectWorkflowPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val createNewProjectVersion: CreateNewProjectVersionInteractor,
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
                        // todo plugin key should be used from call settings for the project when it is added
                        preCheckAndSubmit(projectSummary, "standard-pre-condition-check-plugin")
                    else -> submitApplication(projectSummary)
                }
            }
        }

    private fun preCheckAndSubmit(projectSummary: ProjectSummary, pluginKey: String) =
        if (isPluginEnabled())
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

    private fun submitApplication(projectSummary: ProjectSummary) =
        applicationStateFactory.getInstance(projectSummary).submit().also {
            auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
            createNewProjectVersion.create(
                projectId = projectSummary.id,
                status = projectWorkflowPersistence.getLatestApplicationStatusNotEqualTo(
                    projectSummary.id, ApplicationStatus.RETURNED_TO_APPLICANT
                )
            )
        }

    private fun isPluginEnabled(): Boolean =
        pluginStatusRepository.findById(pluginKey).let {
            when {
                it.isPresent -> it.get().enabled
                else -> true
            }
        }
}
