package io.cloudflight.jems.server.project.service.checklist.clone.verification

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditReportVerificationPrivileged
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.clone.updateWith
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CloneVerificationChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
    private val securityService: SecurityService
) : CloneVerificationChecklistInstanceInteractor {

    @CanEditReportVerificationPrivileged
    @Transactional
    @ExceptionWrapper(CloneVerificationChecklistInstanceException::class)
    override fun clone(projectId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetail {
        val reportStatus = this.projectReportPersistence.getReportById(projectId, reportId).status
        if (reportStatus.verificationNotStartedYet())
            throw CloneVerificationChecklistInstanceStatusNotAllowedException()

        val existingChecklist = persistence.getChecklistDetail(checklistId)
        val newChecklist = persistence.create(
            createChecklist = CreateChecklistInstanceModel(
                relatedToId = existingChecklist.relatedToId!!,
                programmeChecklistId = existingChecklist.programmeChecklistId
            ),
            creatorId = securityService.getUserIdOrThrow()
        )
        return persistence.update(newChecklist.updateWith(existingChecklist))
    }
}
