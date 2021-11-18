package io.cloudflight.jems.server.project.service.get_modification_decisions

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProject
import io.cloudflight.jems.server.project.service.ProjectWorkflowPersistence
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetModificationDecision(private val persistence: ProjectWorkflowPersistence) : GetModificationDecisionsInteractor {

    @CanRetrieveProject
    @Transactional
    @ExceptionWrapper(GetModificationDecisionExceptions::class)
    override fun getModificationDecisions(projectId: Long): List<ProjectStatus> =
        this.persistence.getModificationDecisions(projectId)

}
