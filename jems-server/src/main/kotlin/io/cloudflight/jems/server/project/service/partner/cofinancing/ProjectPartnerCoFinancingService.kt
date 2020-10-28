package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.cofinancing.InputProjectPartnerCoFinancing

interface ProjectPartnerCoFinancingService {

    fun updatePartnerCoFinancing(partnerId: Long, financing: Set<InputProjectPartnerCoFinancing>): OutputProjectPartnerDetail

}
