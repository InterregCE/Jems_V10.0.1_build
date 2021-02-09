package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundType
import io.cloudflight.jems.server.programme.service.model.ProgrammeFund

data class ProjectPartnerCoFinancing(

    val fundType: ProjectPartnerCoFinancingFundType,
    val fund: ProgrammeFund? = null,
    val percentage: Int

)
