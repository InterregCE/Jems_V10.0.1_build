package io.cloudflight.jems.server.project.service.checklist.clone.control

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReportChecklist
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.clone.updateWith
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CloneControlChecklistInstance(
    private val persistence: ChecklistInstancePersistence,
    private val projectPartnerReportPersistence: ProjectPartnerReportPersistence,
    private val securityService: SecurityService
) : CloneControlChecklistInstanceInteractor {

    @CanEditPartnerControlReportChecklist
    @Transactional
    @ExceptionWrapper(CloneControlChecklistInstanceException::class)
    override fun clone(partnerId: Long, reportId: Long, checklistId: Long): ChecklistInstanceDetail {
        val reportStatus = this.projectPartnerReportPersistence.getPartnerReportStatusAndVersion(partnerId, reportId).status
        if (reportStatus.controlNotStartedYet())
            throw CloneControlChecklistInstanceStatusNotAllowedException()

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
