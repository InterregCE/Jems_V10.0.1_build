package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.scope.getCorrectionCostItems

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetCorrectionCostItemsInteractor {

    fun getCostItems(correctionId: Long, pageable: Pageable): Page<CorrectionCostItem>
}