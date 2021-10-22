package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import java.time.LocalDate

interface ProjectWorkflowPersistence {

    fun getProjectEligibilityDecisionDate(projectId: Long): LocalDate?

    fun getApplicationPreviousStatus(projectId: Long): ProjectStatus

    fun getLatestApplicationStatusNotEqualTo(projectId: Long, statusToIgnore: ApplicationStatus): ApplicationStatus

    fun updateApplicationFirstSubmission(projectId: Long, userId: Long, status: ApplicationStatus): ApplicationStatus

    fun updateProjectLastResubmission(projectId: Long, userId: Long, status: ProjectStatus): ApplicationStatus
    fun updateProjectLastResubmission(projectId: Long, userId: Long, status: ApplicationStatus): ApplicationStatus

    fun updateProjectCurrentStatus(
        projectId: Long,
        userId: Long,
        status: ApplicationStatus,
        actionInfo: ApplicationActionInfo? = null
    ): ApplicationStatus

    fun startSecondStep(
        projectId: Long,
        userId: Long,
        actionInfo: ApplicationActionInfo? = null
    ): ApplicationStatus

    fun revertCurrentStatusToPreviousStatus(projectId: Long): ApplicationStatus

    fun updateProjectEligibilityDecision(
        projectId: Long,
        userId: Long,
        status: ApplicationStatus,
        actionInfo: ApplicationActionInfo
    ): ApplicationStatus

    fun clearProjectEligibilityDecision(projectId: Long)

    fun updateProjectFundingDecision(
        projectId: Long,
        userId: Long,
        status: ApplicationStatus,
        actionInfo: ApplicationActionInfo
    ): ApplicationStatus

    fun clearProjectFundingDecision(projectId: Long)

    fun resetProjectFundingDecisionToCurrentStatus(projectId: Long): ApplicationStatus
}
