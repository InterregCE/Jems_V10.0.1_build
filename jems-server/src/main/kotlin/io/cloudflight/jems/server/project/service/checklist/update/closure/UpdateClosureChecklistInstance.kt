package io.cloudflight.jems.server.project.service.checklist.update.closure

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.checklist.*
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateClosureChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val checklistInstanceValidator: ChecklistInstanceValidator,
    private val reportPersistence: ProjectReportPersistence,
    private val securityService: SecurityService
): UpdateClosureChecklistInstanceInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateClosureChecklistInstanceException::class)
    override fun update(reportId: Long, checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        val existing = persistence.getChecklistDetail(checklist.id, ProgrammeChecklistType.CLOSURE, reportId)
        val report = reportPersistence.getReportByIdUnSecured(reportId)

        if (report.status.isClosed() || existing.status != checklist.status || (securityService.currentUser?.user?.id != existing.creatorId))
            throw UpdateClosureChecklistInstanceNotAllowedException()

        checklistInstanceValidator.validateChecklistComponents(checklist.components)
        return persistence.update(checklist)
    }

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateClosureChecklistInstanceException::class)
    override fun changeStatus(reportId: Long, checklistId: Long, status: ChecklistInstanceStatus): ChecklistInstance {
        val existing = persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CLOSURE, reportId)
        val report = reportPersistence.getReportByIdUnSecured(reportId)

        val statusNotChanged = status == existing.status
        val isNotAuthor = securityService.getUserIdOrThrow() != existing.creatorId
        val isFinishedNotByAuthor = status == ChecklistInstanceStatus.FINISHED && isNotAuthor

        if (report.status.isClosed() || statusNotChanged || isFinishedNotByAuthor || report.finalReport != true)
            throw UpdateClosureChecklistInstanceNotAllowedException()

        return persistence.changeStatus(checklistId, status).also {
            auditPublisher.publishEvent(
                projectClosureChecklistStatusChanged(
                    context = this,
                    checklist = it,
                    oldStatus = existing.status,
                    projectId = report.projectId,
                    reportNumber = report.reportNumber
                )
            )
        }
    }

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(UpdateClosureChecklistInstanceException::class)
    override fun updateDescription(reportId: Long, checklistId: Long, description: String?): ChecklistInstance {
        val checklist = persistence.getChecklistSummary(checklistId)
        if (checklist.relatedToId != reportId || checklist.type != ProgrammeChecklistType.CLOSURE)
            throw UpdateClosureChecklistInstanceNotFoundException()
        return persistence.updateDescription(checklistId, description)

    }
}
