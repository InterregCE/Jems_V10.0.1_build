package io.cloudflight.jems.server.project.service.checklist.delete.control

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportChecklist
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.isChecklistCreatedAfterControl
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectControlReportChecklistDeleted
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class DeleteControlChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val partnerPersistence: PartnerPersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val securityService: SecurityService
) : DeleteControlChecklistInstanceInteractor {

    @CanEditPartnerControlReportChecklist
    @Transactional
    @ExceptionWrapper(DeleteControlChecklistInstanceException::class)
    override fun deleteById(partnerId: Long, reportId: Long, checklistId: Long) {
        val partner = partnerPersistence.getById(partnerId)
        val checklistToBeDeleted = persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CONTROL, reportId)
        val report = this.reportPersistence.getPartnerReportById(partnerId, reportId)

        if (report.status.controlNotStartedYet() ||
            isChecklistCreatedAfterControl(checklistToBeDeleted, report.controlEnd)  ||
            checklistToBeDeleted.status == ChecklistInstanceStatus.FINISHED ||
            (securityService.currentUser?.user?.id != checklistToBeDeleted.creatorId))
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
