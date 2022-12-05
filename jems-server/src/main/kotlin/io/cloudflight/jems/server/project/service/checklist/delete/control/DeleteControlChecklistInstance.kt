package io.cloudflight.jems.server.project.service.checklist.delete.control

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectControlReportChecklistDeleted
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteControlChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val partnerPersistence: PartnerPersistence,
    private val reportPersistence: ProjectReportPersistence
) : DeleteControlChecklistInstanceInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(DeleteControlChecklistInstanceException::class)
    override fun deleteById(partnerId: Long, reportId: Long, checklistId: Long) {
        val partner = partnerPersistence.getById(partnerId)
        val checklistToBeDeleted = persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CONTROL, reportId)

        if (checklistToBeDeleted.status == ChecklistInstanceStatus.FINISHED)
            throw DeleteControlChecklistInstanceStatusNotAllowedException()

        persistence.deleteById(checklistId).also {
            auditPublisher.publishEvent(
                projectControlReportChecklistDeleted(
                    context = this,
                    checklist = checklistToBeDeleted,
                    projectId = partner.projectId,
                    partner = partner,
                    reportId = reportPersistence.getPartnerReportById(partnerId, reportId).reportNumber.toLong()
                )
            )
        }
    }
}
