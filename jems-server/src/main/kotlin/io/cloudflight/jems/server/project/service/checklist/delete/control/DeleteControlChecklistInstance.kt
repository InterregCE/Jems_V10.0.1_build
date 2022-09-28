package io.cloudflight.jems.server.project.service.checklist.delete.control

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.controlChecklistDeleted
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteControlChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val partnerPersistence: PartnerPersistence
) : DeleteControlChecklistInstanceInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(DeleteControlChecklistInstanceException::class)
    override fun deleteById(partnerId: Long, reportId: Long, checklistId: Long) {
        val partner = partnerPersistence.getById(partnerId)
        val partnerRole = if (partner.role.isLead) "LP" else "PP"
        val partnerName = partnerRole.plus(partner.sortNumber)

        val checklistToBeDeleted = persistence.getChecklistDetail(checklistId)
        if (checklistToBeDeleted.status == ChecklistInstanceStatus.FINISHED)
            throw DeleteControlChecklistInstanceStatusNotAllowedException()
        persistence.deleteById(checklistId).also {
            auditPublisher.publishEvent(
                controlChecklistDeleted(
                    context = this,
                    checklist = checklistToBeDeleted,
                    partnerName = partnerName,
                    reportId = reportId,
                    projectId = partner.projectId
                )
            )
        }
    }
}