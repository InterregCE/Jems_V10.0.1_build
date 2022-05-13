package io.cloudflight.jems.server.project.service.checklist.getAllInstances

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.consolidateInstance.ConsolidateChecklistNotAllowed
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAllChecklistInstances(
    private val persistence: ChecklistInstancePersistence,
    private val checklistAuthorization: ProjectChecklistAuthorization
) : GetAllChecklistInstancesInteractor {

    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAllChecklistInstancesException::class)
    override fun getChecklistInstancesByTypeAndRelatedId(
        relatedToId: Long, type: ProgrammeChecklistType
    ): List<ChecklistInstance> {
        if (!checklistAuthorization.canConsolidate(relatedToId)) {
            throw ConsolidateChecklistNotAllowed()
        }

        return persistence.getChecklistsByRelatedIdAndType(relatedToId, type)
    }
}
