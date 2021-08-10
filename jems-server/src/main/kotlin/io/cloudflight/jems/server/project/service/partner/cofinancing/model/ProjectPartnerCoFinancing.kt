package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import java.math.BigDecimal

data class ProjectPartnerCoFinancing(

    val fundType: ProjectPartnerCoFinancingFundTypeDTO,
    val fund: ProgrammeFund? = null,
    val percentage: BigDecimal

)
