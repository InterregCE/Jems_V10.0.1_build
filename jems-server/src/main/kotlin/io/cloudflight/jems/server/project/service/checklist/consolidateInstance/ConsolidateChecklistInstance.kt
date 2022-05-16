package io.cloudflight.jems.server.project.service.checklist.consolidateInstance

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.ProjectChecklistAuthorization
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.checklistConsolidated
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ConsolidateChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val checklistAuthorization: ProjectChecklistAuthorization
): ConsolidateChecklistInstanceInteractor {

    @Transactional
    @ExceptionWrapper(ConsolidateChecklistNotAllowed::class)
    override fun consolidateChecklistInstance(checklistId: Long, consolidated: Boolean): Boolean {
        val checklist = persistence.getChecklistSummary(checklistId)

        if (!checklistAuthorization.canConsolidate(checklist.relatedToId!!) ||
            checklist.status !== ChecklistInstanceStatus.FINISHED) {
            throw ConsolidateChecklistNotAllowed()
        }

        return persistence.consolidateChecklistInstance(checklistId, consolidated).also {
            auditPublisher.publishEvent(
                checklistConsolidated(
                    context = this,
                    checklist = it
                )
            )
        }.consolidated
    }
}
