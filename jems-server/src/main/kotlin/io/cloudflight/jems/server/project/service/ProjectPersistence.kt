package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.application.ApplicationActionInfo
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import java.time.LocalDate

interface ProjectPersistence {

    fun getProjectSummary(projectId: Long): ProjectSummary

    fun getProjectEligibilityDecisionDate(projectId: Long): LocalDate?

    fun getProjectCallSettings(projectId: Long): ProjectCallSettings

    fun getProjectUnitCosts(projectId: Long): List<ProgrammeUnitCost>

    fun getProjectPeriods(projectId: Long): List<ProjectPeriod>

    fun getProjectIdForPartner(partnerId: Long): Long

    fun getApplicationPreviousStatus(projectId: Long): ApplicationStatus

    fun updateApplicationFirstSubmission(projectId: Long, userId: Long): ApplicationStatus

    fun updateProjectLastResubmission(projectId: Long, userId: Long, status: ApplicationStatus): ApplicationStatus

    fun updateProjectCurrentStatus(
        projectId: Long,
        userId: Long,
        status: ApplicationStatus,
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
