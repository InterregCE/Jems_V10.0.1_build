package io.cloudflight.jems.server.project.service.checklist.update.verification

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationPrivileged
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstanceValidator
import io.cloudflight.jems.server.project.service.checklist.isChecklistCreatedAfterVerification
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectVerificationReportChecklistStatusChanged
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateVerificationChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val checklistInstanceValidator: ChecklistInstanceValidator,
    private val reportPersistence: ProjectReportPersistence,
    private val securityService: SecurityService
) : UpdateVerificationChecklistInstanceInteractor {

    @CanEditReportVerificationPrivileged
    @Transactional
    @ExceptionWrapper(UpdateVerificationChecklistInstanceException::class)
    override fun update(projectId: Long, reportId: Long, checklist: ChecklistInstanceDetail): ChecklistInstanceDetail {
        val existing = persistence.getChecklistDetail(checklist.id, ProgrammeChecklistType.VERIFICATION, reportId)
        val report = reportPersistence.getReportById(projectId, reportId)

        if (report.status.verificationNotStartedYet() ||
            isChecklistCreatedAfterVerification(existing, report.verificationEndDate)
            || existing.status != checklist.status
            || (securityService.currentUser?.user?.id != existing.creatorId))
            throw UpdateVerificationChecklistInstanceStatusNotAllowedException()

        checklistInstanceValidator.validateChecklistComponents(checklist.components)

        return persistence.update(checklist)
    }

    @CanEditReportVerificationPrivileged
    @Transactional
    @ExceptionWrapper(UpdateVerificationChecklistInstanceStatusException::class)
    override fun changeStatus(
        projectId: Long,
        reportId: Long,
        checklistId: Long,
        status: ChecklistInstanceStatus
    ): ChecklistInstance {
        val report = this.reportPersistence.getReportById(projectId, reportId)
        val existing = persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.VERIFICATION, reportId)

        val statusNotChanged = status == existing.status
        val isNotAuthor = securityService.getUserIdOrThrow() != existing.creatorId
        val isFinishedNotByAuthor = status == ChecklistInstanceStatus.FINISHED && isNotAuthor

        if (report.status.verificationNotStartedYet() || isChecklistCreatedAfterVerification(existing, report.verificationEndDate)
            || statusNotChanged || isFinishedNotByAuthor)
            throw UpdateVerificationChecklistInstanceStatusNotAllowedException()

        return persistence.changeStatus(checklistId, status).also {
            auditPublisher.publishEvent(
                projectVerificationReportChecklistStatusChanged(
                    context = this,
                    checklist = it,
                    oldStatus = existing.status,
                    projectId = projectId,
                    reportId = report.reportNumber.toLong()
                )
            )
        }
    }

    @CanEditReportVerificationPrivileged
    @Transactional
    @ExceptionWrapper(UpdateVerificationChecklistInstanceException::class)
    override fun updateDescription(
        projectId: Long,
        reportId: Long,
        checklistId: Long,
        description: String?
    ): ChecklistInstance {
        val checklist = persistence.getChecklistSummary(checklistId)
        if (checklist.relatedToId != reportId || checklist.type != ProgrammeChecklistType.VERIFICATION)
            throw UpdateVerificationChecklistInstanceNotFoundException()
        return persistence.updateDescription(checklistId, description)
    }
}
