package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.server.call.controller.toDto
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.programme.controller.costoption.toDto
import io.cloudflight.jems.server.project.controller.partner.toOutputProjectPartner
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget

fun PartnerBudget.toProjectPartnerBudgetDTO() =
    ProjectPartnerBudgetDTO(
        partner = partner?.toOutputProjectPartner(),
        staffCosts = staffCosts,
        travelCosts = travelCosts,
        externalCosts = externalCosts,
        equipmentCosts = equipmentCosts,
        infrastructureCosts = infrastructureCosts,
        officeAndAdministrationCosts = officeAndAdministrationCosts,
        otherCosts = otherCosts,
        lumpSumContribution = lumpSumContribution,
        unitCosts = unitCosts,
        totalSum = totalCosts
    )

fun Collection<PartnerBudget>.toProjectPartnerBudgetDTO() = map { it.toProjectPartnerBudgetDTO() }
    .sortedBy { it.partner.sortNumber }

fun ProjectCallSettings.toDto() = ProjectCallSettingsDTO(
    callId = callId,
    callName = callName,
    startDate = startDate,
    endDate = endDate,
    lengthOfPeriod = lengthOfPeriod,
    isAdditionalFundAllowed = isAdditionalFundAllowed,
    flatRates = flatRates.toDto(),
    lumpSums = lumpSums.map { it.toDto() },
    unitCosts = unitCosts.map { it.toDto() },
)
