package io.cloudflight.ems.controller

import io.cloudflight.ems.api.ProjectStatusApi
import io.cloudflight.ems.api.dto.InputProjectEligibilityAssessment
import io.cloudflight.ems.api.dto.InputProjectQualityAssessment
import io.cloudflight.ems.api.dto.InputProjectStatus
import io.cloudflight.ems.api.dto.InputRevertProjectStatus
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputRevertProjectStatus
import io.cloudflight.ems.service.ProjectStatusService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectStatusController(
    private val projectStatusService: ProjectStatusService
) : ProjectStatusApi {

    @PreAuthorize("@projectStatusAuthorization.canChangeStatusTo(#id, #status.status)")
    override fun setProjectStatus(id: Long, status: InputProjectStatus): OutputProject {
        return projectStatusService.setProjectStatus(id, statusChange = status)
    }

    @PreAuthorize("@projectStatusAuthorization.canSetQualityAssessment(#id)")
    override fun setQualityAssessment(id: Long, data: InputProjectQualityAssessment): OutputProject {
        return projectStatusService.setQualityAssessment(id, data)
    }

    @PreAuthorize("@projectStatusAuthorization.canSetEligibilityAssessment(#id)")
    override fun setEligibilityAssessment(id: Long, data: InputProjectEligibilityAssessment): OutputProject {
        return projectStatusService.setEligibilityAssessment(id, data)
    }

    @PreAuthorize("@projectStatusAuthorization.isAdmin()")
    override fun findPossibleDecisionRevertStatus(id: Long): OutputRevertProjectStatus? {
        return projectStatusService.findPossibleDecisionRevertStatusOutput(projectId = id)
    }

    @PreAuthorize("@projectStatusAuthorization.isAdmin()")
    override fun revertLastDecision(id: Long, data: InputRevertProjectStatus): OutputProject {
        return projectStatusService.revertLastDecision(projectId = id, request = data)
    }

}
