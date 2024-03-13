package io.cloudflight.jems.server.project.service.checklist.clone.closure

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.clone.updateWith
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CloneClosureChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
    private val securityService: SecurityService
): CloneClosureChecklistInstanceInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(CloneClosureChecklistInstanceException::class)
    override fun clone(reportId: Long, checklistId: Long): ChecklistInstanceDetail {
        val report = this.projectReportPersistence.getReportByIdUnSecured(reportId)
        if (report.status.isClosed())
            throw CloneClosureChecklistInstanceStatusNotAllowedException()

        val existingChecklist = persistence.getChecklistDetail(checklistId, ProgrammeChecklistType.CLOSURE, reportId)
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
