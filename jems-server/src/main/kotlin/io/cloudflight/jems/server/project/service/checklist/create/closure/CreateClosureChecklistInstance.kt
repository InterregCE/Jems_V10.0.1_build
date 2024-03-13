package io.cloudflight.jems.server.project.service.checklist.create.closure

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.checklist.ProgrammeChecklistPersistence
import io.cloudflight.jems.server.programme.service.checklist.model.ProgrammeChecklistType
import io.cloudflight.jems.server.project.authorization.CanEditProjectReport
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateClosureChecklistInstance(
    private val checklistInstancePersistence: ChecklistInstancePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
    private val programmeChecklistPersistence: ProgrammeChecklistPersistence,
    private val securityService: SecurityService
) : CreateClosureChecklistInstanceInteractor {

    @CanEditProjectReport
    @Transactional
    @ExceptionWrapper(CreateClosureChecklistInstanceException::class)
    override fun create(reportId: Long, programmeChecklistId: Long): ChecklistInstanceDetail {
        val report = this.projectReportPersistence.getReportByIdUnSecured(reportId)
        if (report.status.isClosed())
            throw CreateClosureChecklistInstanceStatusNotAllowedException()

        if (report.finalReport != true)
            throw CreateClosureChecklistInstanceNotFinalReportException()

        if (programmeChecklistPersistence.getChecklistDetail(programmeChecklistId).type != ProgrammeChecklistType.CLOSURE)
            throw CreateClosureChecklistInstanceTypeNotValidException()

        return checklistInstancePersistence.create(
            createChecklist = CreateChecklistInstanceModel(reportId, programmeChecklistId),
            creatorId = securityService.getUserIdOrThrow()
        )
    }

}
