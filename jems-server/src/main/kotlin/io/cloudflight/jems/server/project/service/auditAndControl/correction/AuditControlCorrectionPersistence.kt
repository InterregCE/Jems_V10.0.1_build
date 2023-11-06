package io.cloudflight.jems.server.project.service.auditAndControl.correction

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface AuditControlCorrectionPersistence {

    fun getAllCorrectionsByAuditControlId(auditControlId: Long, pageable: Pageable): Page<ProjectAuditControlCorrection>

    fun getPreviousClosedCorrections(auditControlId: Long, correctionId: Long): List<ProjectAuditControlCorrection>

    fun getByCorrectionId(correctionId: Long): ProjectAuditControlCorrection

    fun getExtendedByCorrectionId(correctionId: Long): ProjectAuditControlCorrectionExtended

    fun getLastUsedOrderNr(auditControlId: Long): Int?

    fun deleteCorrectionById(id: Long)

    fun closeCorrection(correctionId: Long): ProjectAuditControlCorrection

    fun getOngoingCorrectionsByAuditControlId(auditControlId: Long): List<ProjectAuditControlCorrection>

    fun getLastCorrectionOngoingId(auditControlId: Long): Long?

}
