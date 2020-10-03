package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.ProjectStatusApi
import io.cloudflight.jems.api.project.dto.status.InputProjectEligibilityAssessment
import io.cloudflight.jems.api.project.dto.status.InputProjectQualityAssessment
import io.cloudflight.jems.api.project.dto.InputProjectStatus
import io.cloudflight.jems.api.project.dto.InputRevertProjectStatus
import io.cloudflight.jems.api.project.dto.OutputProject
import io.cloudflight.jems.api.project.dto.status.OutputRevertProjectStatus
import io.cloudflight.jems.server.project.service.ProjectStatusService
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
