package io.cloudflight.jems.api.project.dto.partner.cofinancing

data class ProjectPartnerCoFinancingInputDTO(

    val fundType: ProjectPartnerCoFinancingFundType,
    val fundId: Long? = null,
    val percentage: Int? = null,

)
