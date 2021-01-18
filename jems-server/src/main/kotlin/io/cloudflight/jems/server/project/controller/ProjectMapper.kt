package io.cloudflight.jems.server.project.controller

import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.budget.ProjectPartnerBudgetDTO
import io.cloudflight.jems.server.call.controller.toDto
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.programme.controller.costoption.toDto
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
        totalSum = totalSum(),
        lumpSumContribution = lumpSumContribution,
        unitCosts = unitCosts,
    )

fun Collection<PartnerBudget>.toOutputDto() = map { it.toOutputDto() }
    .sortedBy { it.partner?.sortNumber }

fun ProjectCallSettings.toDto() = ProjectCallSettingsDTO(
    callId = callId,
    callName = callName,
    startDate = startDate,
    endDate = endDate,
    lengthOfPeriod = lengthOfPeriod,
    flatRates = flatRates.toDto(),
    lumpSums = lumpSums.map { it.toDto() },
    unitCosts = unitCosts.map { it.toDto() },
)
