package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.ems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.ems.api.project.dto.InputProjectStatus
import io.cloudflight.ems.api.project.dto.InputRevertProjectStatus
import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.api.project.dto.status.OutputRevertProjectStatus

interface ProjectStatusService {

    fun setProjectStatus(projectId: Long, statusChange: InputProjectStatus): OutputProject

    fun setQualityAssessment(projectId: Long, qualityAssessmentData: InputProjectQualityAssessment): OutputProject

    fun setEligibilityAssessment(projectId: Long, eligibilityAssessmentData: InputProjectEligibilityAssessment): OutputProject

    /**
     * If the last status was made by decision, check if there is possibility to revert it.
     * If there is such possibility, will return detail info about such transition.
     */
    fun findPossibleDecisionRevertStatusOutput(projectId: Long): OutputRevertProjectStatus?

    fun revertLastDecision(projectId: Long, request: InputRevertProjectStatus): OutputProject
}
