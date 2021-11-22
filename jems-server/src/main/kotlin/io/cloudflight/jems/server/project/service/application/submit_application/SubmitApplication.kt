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
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectStatusChanged
import io.cloudflight.jems.server.project.service.save_project_version.SaveProjectVersionInteractor
import io.cloudflight.jems.server.project.service.unsuccessfulProjectSubmission
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubmitApplication(
    private val projectPersistence: ProjectPersistence,
    private val projectWorkflowPersistence: ProjectWorkflowPersistence,
    private val applicationStateFactory: ApplicationStateFactory,
    private val saveProjectVersion: SaveProjectVersionInteractor,
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

    private fun saveProjectVersion(projectSummary: ProjectSummary, nextStatus: ApplicationStatus) {
        val statusesWithoutNewVersion =
            arrayOf(ApplicationStatus.CONDITIONS_SUBMITTED, ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED)

        if (!statusesWithoutNewVersion.contains(projectWorkflowPersistence.getApplicationPreviousStatus(projectSummary.id).status)) {
            saveProjectVersion.createNewVersion(
                projectId = projectSummary.id,
                status = nextStatus
            )
        } else {
            saveProjectVersion.updateLastVersion(projectSummary.id)
        }
    }

    private fun createNewVersionBasedOnStatus(projectSummary: ProjectSummary) {

        when (projectSummary.status) {
            ApplicationStatus.RETURNED_TO_APPLICANT_FOR_CONDITIONS -> saveProjectVersion(
                projectSummary,
                ApplicationStatus.CONDITIONS_SUBMITTED
            )
            ApplicationStatus.MODIFICATION_PRECONTRACTING -> saveProjectVersion(
                projectSummary,
                ApplicationStatus.MODIFICATION_PRECONTRACTING_SUBMITTED
            )
            else -> saveProjectVersion.createNewVersion(
                projectId = projectSummary.id,
                status = projectWorkflowPersistence.getLatestApplicationStatusNotEqualTo(
                    projectSummary.id,
                    ApplicationStatus.RETURNED_TO_APPLICANT
                )
            )
        }
    }

    private fun submitApplication(projectSummary: ProjectSummary): ApplicationStatus {
        createNewVersionBasedOnStatus(projectSummary)
        return applicationStateFactory.getInstance(projectSummary).submit().also {
            auditPublisher.publishEvent(projectStatusChanged(this, projectSummary, newStatus = it))
        }
    }

    private fun isPluginEnabled(): Boolean =
        pluginStatusRepository.findById(pluginKey).let {
            when {
                it.isPresent -> it.get().enabled
                else -> true
            }
        }
}
