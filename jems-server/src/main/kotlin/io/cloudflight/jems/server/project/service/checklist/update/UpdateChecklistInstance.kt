package io.cloudflight.jems.server.programme.service.checklist.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.authorization.CanUpdateChecklistAssessment
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateChecklistInstance(
    private val persistence: ChecklistInstancePersistence
) : UpdateChecklistInstanceInteractor {

    @CanUpdateChecklistAssessment
    @Transactional
    @ExceptionWrapper(UpdateChecklistInstanceException::class)
    override fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        if (persistence.getStatus(checklist.id) == ChecklistInstanceStatus.FINISHED)
            throw UpdateChecklistInstanceStatusNotAllowedException()
        return persistence.update(checklist)
    }
}
