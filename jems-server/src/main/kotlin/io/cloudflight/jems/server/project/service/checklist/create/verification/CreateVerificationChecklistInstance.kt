package io.cloudflight.jems.server.project.service.checklist.create.verification

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditReportVerification
import io.cloudflight.jems.server.project.service.checklist.VerificationChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import io.cloudflight.jems.server.project.service.report.project.base.ProjectReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateVerificationChecklistInstance(
    private val persistence: VerificationChecklistInstancePersistence,
    private val projectReportPersistence: ProjectReportPersistence,
    private val securityService: SecurityService
) : CreateVerificationChecklistInstanceInteractor {

    @CanEditReportVerification
    @Transactional
    @ExceptionWrapper(CreateVerificationChecklistInstanceException::class)
    override fun create(projectId: Long, reportId: Long, createCheckList: CreateChecklistInstanceModel): ChecklistInstanceDetail {
        val reportStatus = this.projectReportPersistence.getReportById(projectId, reportId).status
        if (reportStatus.verificationNotStartedYet())
            throw CreateVerificationChecklistInstanceStatusNotAllowedException()

        return persistence.create(
            createChecklist = createCheckList,
            creatorId = securityService.currentUser?.user?.id!!,
            reportId = reportId
        )
    }
}
