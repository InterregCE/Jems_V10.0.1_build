package io.cloudflight.jems.server.project.repository.auditAndControl.correction.tmpModel

import java.math.BigDecimal

data class AuditControlCorrectionRelatedData(
    val totalCorrections: BigDecimal,
    val existsOngoing: Boolean,
    val existsClosed: Boolean,
)
