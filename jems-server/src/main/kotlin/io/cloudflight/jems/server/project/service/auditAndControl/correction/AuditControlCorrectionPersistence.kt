package io.cloudflight.jems.server.project.service.auditAndControl.correction

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectAuditControlCorrectionExtended
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface AuditControlCorrectionPersistence {

    fun saveCorrection(correction: ProjectAuditControlCorrection): ProjectAuditControlCorrection

    fun getAllCorrectionsByAuditControlId(auditControlId: Long, pageable: Pageable): Page<ProjectAuditControlCorrection>

    fun getByCorrectionId(correctionId: Long): ProjectAuditControlCorrection

    fun getExtendedByCorrectionId(correctionId: Long): ProjectAuditControlCorrectionExtended

    fun getLastCorrectionIdByAuditControlId(auditControlId: Long): Long?

    fun getLastUsedOrderNr(auditControlId: Long): Int?

    fun deleteCorrectionById(id: Long)
}
