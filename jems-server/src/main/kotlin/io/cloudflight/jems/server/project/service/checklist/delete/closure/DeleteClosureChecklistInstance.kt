package io.cloudflight.jems.server.project.service.checklist.delete.closure

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectClosureChecklistDeleted
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteClosureChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val reportPersistence: ProjectReportPersistence,
    private val securityService: SecurityService
): DeleteClosureChecklistInstanceInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(DeleteClosureChecklistInstanceException::class)
    override fun deleteById(reportId: Long, checklistId: Long) {
        val checklistToBeDeleted = persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CLOSURE, reportId)
        val report = reportPersistence.getReportByIdUnSecured(reportId)

        if (report.status.isClosed() ||
            checklistToBeDeleted.status == ChecklistInstanceStatus.FINISHED ||
            (securityService.currentUser?.user?.id != checklistToBeDeleted.creatorId))
            throw DeleteClosureChecklistInstanceStatusNotAllowedException()

        persistence.deleteById(checklistId).also {
            auditPublisher.publishEvent(
                projectClosureChecklistDeleted(
                    context = this,
                    checklist = checklistToBeDeleted,
                    projectId = report.projectId,
                    reportNumber = report.reportNumber
                )
            )
        }
    }
}
