package io.cloudflight.jems.server.project.service.auditAndControl.correction.listProjectAuditCorrection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectAuditAndControl
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.toLineModel
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionLine
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ListProjectAuditControlCorrections(
    private val correctionPersistence: AuditControlCorrectionPersistence,
    private val auditControlPersistence: AuditControlPersistence
): ListProjectAuditControlCorrectionsInteractor {

    @CanViewProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(ListProjectAuditControlCorrectionsException::class)
    override fun listProjectAuditCorrections(
        projectId: Long,
        auditControlId: Long,
        pageable: Pageable
    ): Page<ProjectAuditControlCorrectionLine> {
        val auditControl = auditControlPersistence.getByIdAndProjectId(auditControlId, projectId)

        val correctionList = correctionPersistence.getAllCorrectionsByAuditControlId(
            auditControlId,
            pageable
        )
        val lastOngoingCorrectionId = correctionPersistence.getLastCorrectionOngoingId(auditControlId)

        return correctionList.map {
            it.toLineModel(
                auditControlNumber = auditControl.number,
                canBeDeleted = checkCorrectionCanBeDeleted(it, lastOngoingCorrectionId)
            )
        }
    }

    private fun checkCorrectionCanBeDeleted(correction: ProjectAuditControlCorrection, lastOngoingCorrectionId: Long?): Boolean =
         correction.id == lastOngoingCorrectionId


}
