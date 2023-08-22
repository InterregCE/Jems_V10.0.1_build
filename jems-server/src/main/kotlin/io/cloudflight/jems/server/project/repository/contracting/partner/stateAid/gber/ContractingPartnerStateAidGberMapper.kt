package io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.gber

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGberEntity
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGber

fun ProjectContractingPartnerStateAidGberEntity.toModel() =
    ContractingPartnerStateAidGber(
        aidIntensity = aidIntensity,
        locationInAssistedArea = locationInAssistedArea,
        comment = comment,
        amountGrantingAid = amountGrantingAid
    )

