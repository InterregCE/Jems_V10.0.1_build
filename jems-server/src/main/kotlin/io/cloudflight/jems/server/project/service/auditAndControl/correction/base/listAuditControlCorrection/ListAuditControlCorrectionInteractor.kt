package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListAuditControlCorrectionInteractor {

    fun listCorrections(auditControlId: Long, pageable: Pageable): Page<AuditControlCorrectionLine>

}
