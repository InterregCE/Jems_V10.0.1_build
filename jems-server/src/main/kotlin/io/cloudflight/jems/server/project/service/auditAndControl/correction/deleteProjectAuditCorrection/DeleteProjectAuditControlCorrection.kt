package io.cloudflight.jems.server.project.service.auditAndControl.correction.deleteProjectAuditCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditProjectAuditAndControl
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.AuditControlNotOngoingException
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.projectAuditControlCorrectionDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectAuditControlCorrection(
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val auditControlPersistence: AuditControlPersistence,
    private val auditPublisher: ApplicationEventPublisher,
    private val projectPersistence: ProjectPersistence
): DeleteProjectAuditControlCorrectionInteractor {

    @CanEditProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(DeleteProjectAuditControlCorrectionException::class)
    override fun deleteProjectAuditCorrection(
        projectId: Long,
        auditControlId: Long,
        correctionToBeDeletedId: Long
    ) {

        val auditControl = auditControlPersistence.getByIdAndProjectId(auditControlId, projectId)
        validateAuditControlStatus(auditControl)

        val lastCorrectionId = correctionPersistence.getLastCorrectionOngoingId(auditControlId)
        validateIsAtLeastOneCorrectionSaved(lastCorrectionId)
        validateIsLastCorrection(lastCorrectionId!!, correctionToBeDeletedId)

        val projectSummary = projectPersistence.getProjectSummary(projectId)
        val correctionToBeDeleted = correctionPersistence.getByCorrectionId(correctionToBeDeletedId)

        correctionPersistence.deleteCorrectionById(correctionToBeDeletedId).also {
            auditPublisher.publishEvent(
                projectAuditControlCorrectionDeleted(
                    context = this,
                    projectSummary = projectSummary,
                    auditControl = auditControl,
                    correction = correctionToBeDeleted
                )
            )
        }
    }

    private fun validateAuditControlStatus(auditControl: ProjectAuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlNotOngoingException()
    }

    private fun validateIsLastCorrection(lastCorrectionId: Long, correctionToBeDeletedId: Long) {
        if (lastCorrectionId != correctionToBeDeletedId)
            throw AuditControlIsNotLastCorrectionException()
    }

    private fun validateIsAtLeastOneCorrectionSaved(lastCorrectionId: Long?) {
        if (lastCorrectionId == null)
            throw AuditControlNoCorrectionSavedException()
    }

}
