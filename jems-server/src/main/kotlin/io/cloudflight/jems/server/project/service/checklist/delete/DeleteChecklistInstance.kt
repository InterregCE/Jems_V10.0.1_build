package io.cloudflight.jems.server.programme.service.checklist.delete

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanDeleteChecklistAssessment
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteChecklistInstance(
    private val persistence: ChecklistInstancePersistence
) : DeleteChecklistInstanceInteractor {

    @CanDeleteChecklistAssessment
    @Transactional
    @ExceptionWrapper(DeleteChecklistInstanceException::class)
    override fun deleteById(checklistId: Long) {
        persistence.deleteById(checklistId)
    }
}
