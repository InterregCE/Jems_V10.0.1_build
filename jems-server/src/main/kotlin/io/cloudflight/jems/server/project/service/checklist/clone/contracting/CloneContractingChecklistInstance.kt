package io.cloudflight.jems.server.project.service.checklist.clone.contracting

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.clone.updateWith
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CloneContractingChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val securityService: SecurityService
) : CloneContractingChecklistInstanceInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(CloneContractingChecklistInstanceException::class)
    override fun clone(projectId: Long, checklistId: Long): ChecklistInstanceDetail {
        val existingChecklist = persistence.getChecklistDetail(checklistId)
        val newChecklist = persistence.create(
            createChecklist = CreateChecklistInstanceModel(
                relatedToId = existingChecklist.relatedToId!!,
                programmeChecklistId = existingChecklist.programmeChecklistId
            ),
            creatorId = securityService.getUserIdOrThrow()
        )
        return persistence.update(newChecklist.updateWith(existingChecklist))
    }
}
