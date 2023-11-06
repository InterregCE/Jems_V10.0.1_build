package io.cloudflight.jems.server.project.service.auditAndControl

import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.CorrectionsStillOpenException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit.MaxNumberOfAuditsReachedException
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControlUpdate
import org.springframework.stereotype.Service

@Service
class ProjectAuditAndControlValidator(
    private val generalValidator: GeneralValidatorService,
    private val correctionPersistence: AuditControlCorrectionPersistence
) {
    companion object {
        private const val MAX_NUMBER_OF_AUDITS = 100
        private const val AUDIT_COMMENT_MAX_LENGTH = 2000
    }

    fun verifyAuditControlOngoing(auditControl: ProjectAuditControl) {
        if (auditControl.status != AuditStatus.Ongoing) {
            throw AuditControlNotOngoingException()
        }
    }

    fun validateMaxNumberOfAudits(numberOfExistingAudits: Long) {
        if (numberOfExistingAudits >= MAX_NUMBER_OF_AUDITS) {
            throw MaxNumberOfAuditsReachedException()
        }
    }

    fun validateAllCorrectionsAreClosed(auditControlId: Long) {
        val ongoingCorrections = correctionPersistence.getOngoingCorrectionsByAuditControlId(auditControlId)

        if (ongoingCorrections.isNotEmpty()) {
            throw CorrectionsStillOpenException()
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
