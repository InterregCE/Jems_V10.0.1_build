package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProjectEligibilityAssessment
import io.cloudflight.ems.api.dto.InputProjectQualityAssessment
import io.cloudflight.ems.api.dto.InputProjectStatus
import io.cloudflight.ems.api.dto.InputRevertProjectStatus
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputRevertProjectStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus

val possibleRevertTransitions = listOf(
    // remove eligibility decision
    Pair(ProjectApplicationStatus.INELIGIBLE, ProjectApplicationStatus.SUBMITTED),
    Pair(ProjectApplicationStatus.ELIGIBLE, ProjectApplicationStatus.SUBMITTED),
    // remove funding decision
    Pair(ProjectApplicationStatus.APPROVED, ProjectApplicationStatus.APPROVED_WITH_CONDITIONS),
    Pair(ProjectApplicationStatus.NOT_APPROVED, ProjectApplicationStatus.APPROVED_WITH_CONDITIONS),
    Pair(ProjectApplicationStatus.APPROVED, ProjectApplicationStatus.ELIGIBLE),
    Pair(ProjectApplicationStatus.NOT_APPROVED, ProjectApplicationStatus.ELIGIBLE),
    Pair(ProjectApplicationStatus.APPROVED_WITH_CONDITIONS, ProjectApplicationStatus.ELIGIBLE)
)

interface ProjectStatusService {

    fun setProjectStatus(projectId: Long, statusChange: InputProjectStatus): OutputProject

    fun setQualityAssessment(projectId: Long, qualityAssessmentData: InputProjectQualityAssessment): OutputProject

    fun setEligibilityAssessment(projectId: Long, eligibilityAssessmentData: InputProjectEligibilityAssessment): OutputProject

    /**
     * If the last status was made by decision, check if there is possibility to revert it.
     * If there is such possibility, will return detail info about such transition.
     */
    fun findPossibleDecisionRevertStatusOutput(projectId: Long): OutputRevertProjectStatus

    fun revertLastDecision(projectId: Long, request: InputRevertProjectStatus)
}
