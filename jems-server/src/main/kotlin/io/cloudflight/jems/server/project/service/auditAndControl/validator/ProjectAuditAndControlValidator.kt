package io.cloudflight.jems.server.project.service.auditAndControl.validator

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlUpdate
import org.springframework.stereotype.Service

@Service
class ProjectAuditAndControlValidator(
    private val generalValidator: GeneralValidatorService,
) {
    companion object {
        private const val AUDIT_COMMENT_MAX_LENGTH = 2000

        fun verifyAuditControlOngoing(auditControl: AuditControl) {
            if (auditControl.status.isClosed())
                throw AuditControlNotOngoingException()
        }
    }

    fun validateData(auditData: AuditControlUpdate) {
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
