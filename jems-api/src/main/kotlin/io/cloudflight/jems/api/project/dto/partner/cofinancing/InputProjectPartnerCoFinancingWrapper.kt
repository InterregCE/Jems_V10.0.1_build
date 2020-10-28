package io.cloudflight.jems.api.project.dto.partner.cofinancing

import javax.validation.constraints.Size

data class InputProjectPartnerCoFinancing(

    // if updating
    val id: Long? = null,

    val fundId: Long? = null,
    val percentage: Int? = null

)

@InputProjectPartnerCoFinancingValidator
data class InputProjectPartnerCoFinancingWrapper(

    @field:Size(max = 20, message = "project.partner.coFinancing.max.allowed.reached")
    val finances: Set<InputProjectPartnerCoFinancing> = emptySet()

)
