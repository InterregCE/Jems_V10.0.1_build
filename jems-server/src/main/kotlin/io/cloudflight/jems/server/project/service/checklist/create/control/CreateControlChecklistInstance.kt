package io.cloudflight.jems.server.project.service.checklist.create.control

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerControlReport
import io.cloudflight.jems.server.project.service.checklist.ControlChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateControlChecklistInstance(
    private val persistence: ControlChecklistInstancePersistence,
    private val projectPartnerReportPersistence: ProjectPartnerReportPersistence,
    private val securityService: SecurityService
) : CreateControlChecklistInstanceInteractor {

    @CanEditPartnerControlReport
    @Transactional
    @ExceptionWrapper(CreateControlChecklistInstanceException::class)
    override fun create(partnerId: Long, reportId: Long, createCheckList: CreateChecklistInstanceModel): ChecklistInstanceDetail {
        val reportStatus = this.projectPartnerReportPersistence.getPartnerReportStatusAndVersion(partnerId, reportId).status

        if(reportStatus.controlNotOpenAnymore())
            throw CreateControlChecklistInstanceStatusNotAllowedException()

        return persistence.create(
            createChecklist = createCheckList,
            creatorId = securityService.currentUser?.user?.id!!,
            reportId = reportId
        )
    }
}
