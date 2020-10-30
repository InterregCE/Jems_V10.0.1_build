package io.cloudflight.jems.api.project.dto.partner.cofinancing

import io.cloudflight.jems.api.programme.dto.OutputProgrammeFund

data class OutputProjectPartnerCoFinancing(
    val percentage: Int,
    val fund: OutputProgrammeFund?
)
