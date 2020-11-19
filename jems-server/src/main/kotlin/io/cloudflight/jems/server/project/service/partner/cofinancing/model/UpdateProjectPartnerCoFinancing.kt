package io.cloudflight.jems.server.project.service.partner.cofinancing.model

data class UpdateProjectPartnerCoFinancing(

    // if updating
    val id: Long? = null,

    val fundId: Long? = null,
    val percentage: Int? = null

)
