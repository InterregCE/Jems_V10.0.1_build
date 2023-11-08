package io.cloudflight.jems.server.project.service.auditAndControl.correction

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface AuditControlCorrectionPersistence {

    fun getProjectIdForCorrection(correctionId: Long): Long

    fun getAllCorrectionsByAuditControlId(auditControlId: Long, pageable: Pageable): Page<AuditControlCorrection>

    fun getPreviousClosedCorrections(correctionId: Long): List<AuditControlCorrection>

    fun getByCorrectionId(correctionId: Long): AuditControlCorrectionDetail

    fun getLastUsedOrderNr(auditControlId: Long): Int?

    fun deleteCorrectionById(id: Long)

    fun closeCorrection(correctionId: Long): AuditControlCorrection

    fun getOngoingCorrectionsByAuditControlId(auditControlId: Long): List<AuditControlCorrection>

    fun updateCorrection(correctionId: Long, data: AuditControlCorrectionUpdate): AuditControlCorrectionDetail

}
