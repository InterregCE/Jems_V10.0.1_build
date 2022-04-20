package io.cloudflight.jems.server.programme.service.checklist.update

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.authorization.CanUpdateChecklistAssessment
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.checklistStatusChanged
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher
) : UpdateChecklistInstanceInteractor {

    @CanUpdateChecklistAssessment
    @Transactional
    @ExceptionWrapper(UpdateChecklistInstanceException::class)
    override fun update(checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        val oldStatus = persistence.getStatus(checklist.id);
        if (oldStatus == ChecklistInstanceStatus.FINISHED)
            throw UpdateChecklistInstanceStatusNotAllowedException()
        return persistence.update(checklist).also {
            if (oldStatus !== checklist.status)
                auditPublisher.publishEvent(
                    checklistStatusChanged(
                        context = this,
                        checklist = it,
                        oldStatus = oldStatus,
                    )
                )
        }
    }
}
