package io.cloudflight.jems.server.project.service.checklist.delete.verification

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditReportVerification
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.isChecklistCreatedAfterVerification
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceStatus
import io.cloudflight.jems.server.project.service.checklist.projectVerificationReportChecklistDeleted
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteVerificationChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val reportPersistence: ProjectReportPersistence,
    private val securityService: SecurityService
) : DeleteVerificationChecklistInstanceInteractor {

    @CanEditReportVerification
    @Transactional
    @ExceptionWrapper(DeleteVerificationChecklistInstanceException::class)
    override fun deleteById(projectId: Long, reportId: Long, checklistId: Long) {
        val checklistToBeDeleted = persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.VERIFICATION, reportId)
        val report = this.reportPersistence.getReportById(projectId, reportId)

        if (report.status.verificationNotStartedYet() ||
            isChecklistCreatedAfterVerification(checklistToBeDeleted, report.verificationEndDate)  ||
            checklistToBeDeleted.status == ChecklistInstanceStatus.FINISHED ||
            (securityService.currentUser?.user?.id != checklistToBeDeleted.creatorId))
                throw DeleteVerificationChecklistInstanceStatusNotAllowedException()

        persistence.deleteById(checklistId).also {
            auditPublisher.publishEvent(
                projectVerificationReportChecklistDeleted(
                    context = this,
                    checklist = checklistToBeDeleted,
                    projectId = projectId,
                    reportId = report.reportNumber.toLong()
                )
            )
        }
    }
}
