package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import io.cloudflight.jems.server.programme.service.model.ProgrammeFund

data class ProjectPartnerCoFinancing(

    // if updating
    val id: Long? = null,

    val fund: ProgrammeFund? = null,
    val percentage: Int

)
