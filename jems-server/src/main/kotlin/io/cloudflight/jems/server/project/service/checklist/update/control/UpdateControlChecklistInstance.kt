package io.cloudflight.jems.server.project.service.checklist.update.control

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportChecklist
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstanceValidator
import io.cloudflight.jems.server.project.service.checklist.isChecklistCreatedAfterControl
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectControlReportChecklistStatusChanged
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateControlChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val checklistInstanceValidator: ChecklistInstanceValidator,
    private val partnerPersistence: PartnerPersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val securityService: SecurityService
) : UpdateControlChecklistInstanceInteractor {

    @CanEditPartnerControlReportChecklist
    @Transactional
    @ExceptionWrapper(UpdateControlChecklistInstanceException::class)
    override fun update(partnerId: Long, reportId: Long, checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        val existing = persistence.getChecklistDetail(checklist.id, ProgrammeChecklistType.CONTROL, reportId)
        val report = reportPersistence.getPartnerReportById(partnerId, reportId)

        if (report.status.controlNotStartedYet() ||
            isChecklistCreatedAfterControl(existing, report.controlEnd)
            || existing.status != checklist.status
            || (securityService.currentUser?.user?.id != existing.creatorId))
            throw UpdateControlChecklistInstanceStatusNotAllowedException()

        checklistInstanceValidator.validateChecklistComponents(checklist.components)

        return persistence.update(checklist)
    }

    @CanEditPartnerControlReportChecklist
    @Transactional
    @ExceptionWrapper(UpdateControlChecklistInstanceStatusException::class)
    override fun changeStatus(
        partnerId: Long,
        reportId: Long,
        checklistId: Long,
        status: ChecklistInstanceStatus
    ): ChecklistInstance {
        val partner = partnerPersistence.getById(partnerId)
        val report = this.reportPersistence.getPartnerReportById(partnerId, reportId)
        val existing = persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CONTROL, reportId)

        val statusNotChanged = status == existing.status
        val isNotAuthor = securityService.getUserIdOrThrow() != existing.creatorId
        val isFinishedNotByAuthor = status == ChecklistInstanceStatus.FINISHED && isNotAuthor

        if (report.status.controlNotStartedYet() || isChecklistCreatedAfterControl(existing, report.controlEnd)
            || statusNotChanged || isFinishedNotByAuthor)
            throw UpdateControlChecklistInstanceStatusNotAllowedException()

        return persistence.changeStatus(checklistId, status).also {
            auditPublisher.publishEvent(
                projectControlReportChecklistStatusChanged(
                    context = this,
                    checklist = it,
                    oldStatus = existing.status,
                    partner = partner,
                    reportId = reportPersistence.getPartnerReportById(partnerId, reportId).reportNumber.toLong()
                )
            )
        }
    }

    @CanEditPartnerControlReportChecklist
    @Transactional
    @ExceptionWrapper(UpdateControlChecklistInstanceException::class)
    override fun updateDescription(
        partnerId: Long,
        reportId: Long,
        checklistId: Long,
        description: String?
    ): ChecklistInstance {
        val checklist = persistence.getChecklistSummary(checklistId)
        if (checklist.relatedToId != reportId || checklist.type != ProgrammeChecklistType.CONTROL)
            throw UpdateControlChecklistInstanceNotFoundException()
        return persistence.updateDescription(checklistId, description)
    }
}
