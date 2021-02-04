package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundType

data class UpdateProjectPartnerCoFinancing(

    val fundType: ProjectPartnerCoFinancingFundType,
    val fundId: Long? = null,
    val percentage: Int? = null,

    )
