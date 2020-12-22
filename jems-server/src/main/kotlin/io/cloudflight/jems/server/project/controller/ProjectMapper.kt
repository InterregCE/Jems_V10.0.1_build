package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.server.project.controller.partner.toOutputProjectPartner
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget

fun PartnerBudget.toOutputDto() =
    ProjectPartnerBudgetDTO(
        partner = partner?.toOutputProjectPartner(),
        staffCosts = extractStaffCosts(),
        travelCosts = extractTravelCosts(),
        externalCosts = externalCosts,
        equipmentCosts = equipmentCosts,
        infrastructureCosts = infrastructureCosts,
        officeAndAdministrationCosts = extractOfficeAndAdministrationCosts(),
        otherCosts = extractOtherCosts(),
        totalSum = totalSum()
    )

fun Collection<PartnerBudget>.toOutputDto() = map { it.toOutputDto() }
    .sortedBy { it.partner?.sortNumber }
