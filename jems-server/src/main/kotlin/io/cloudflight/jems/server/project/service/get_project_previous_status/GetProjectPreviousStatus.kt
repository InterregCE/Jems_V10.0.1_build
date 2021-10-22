package io.cloudflight.jems.server.project.service.get_project_previous_status

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import org.springframework.stereotype.Service

@Service
class GetProjectPreviousStatus(
    private val projectWorkflowPersistance: ProjectWorkflowPersistence
) : GetProjectPreviousStatusInteractor {

    @ExceptionWrapper(GetProjectPreviousStatusExceptions::class)
    override fun getProjectPreviousStatus(projectId: Long) =
        this.projectWorkflowPersistance.getApplicationPreviousStatus(projectId)
}
