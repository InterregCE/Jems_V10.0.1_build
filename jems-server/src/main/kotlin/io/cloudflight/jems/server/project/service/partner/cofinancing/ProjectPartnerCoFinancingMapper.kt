package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.InputProjectPartnerCoFinancing
import io.cloudflight.jems.api.project.dto.partner.cofinancing.OutputProjectPartnerCoFinancing
import io.cloudflight.jems.server.programme.entity.ProgrammeFund
import io.cloudflight.jems.server.programme.service.toOutputProgrammeFund
import io.cloudflight.jems.server.project.entity.partner.cofinancing.ProjectPartnerCoFinancing

fun Set<InputProjectPartnerCoFinancing>.toEntity(partnerId: Long, availableFunds: Map<Long, ProgrammeFund>) = mapTo(HashSet()) {
    ProjectPartnerCoFinancing(
        id = it.id ?: 0,
        partnerId = partnerId,
        percentage = it.percentage!!,
        programmeFund = availableFunds[it.fundId]
    )
}

fun ProjectPartnerCoFinancing.toOutputProjectCoFinancing() = OutputProjectPartnerCoFinancing(
    percentage = percentage,
    fund = programmeFund?.toOutputProgrammeFund()
)
