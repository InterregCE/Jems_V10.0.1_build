package io.cloudflight.jems.server.project.service.auditAndControl

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit.MaxNumberOfAuditsReachedException
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControlUpdate
import org.springframework.stereotype.Service

@Service
class ProjectAuditAndControlValidator(
    private val generalValidator: GeneralValidatorService,
) {
    companion object {
        private const val MAX_NUMBER_OF_AUDITS = 100
        private const val AUDIT_COMMENT_MAX_LENGTH = 2000
    }


    fun validateMaxNumberOfAudits(numberOfExistingAudits: Long) {
        if (numberOfExistingAudits >= MAX_NUMBER_OF_AUDITS) {
            throw MaxNumberOfAuditsReachedException()
        }
    }

    fun validateData(auditData: ProjectAuditControlUpdate) {
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.startDateBeforeEndDate(
                start = auditData.startDate,
                end = auditData.endDate,
                startDateFieldName = "startDate",
                endDateFieldName = "endDate"
            ),
            generalValidator.maxLength(auditData.comment, AUDIT_COMMENT_MAX_LENGTH, "comment")
        )
    }

}