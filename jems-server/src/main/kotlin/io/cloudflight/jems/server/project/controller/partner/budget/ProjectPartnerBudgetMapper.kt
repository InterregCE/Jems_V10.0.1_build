package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.BudgetCostsDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetGeneralCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetStaffCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetTravelAndAccommodationCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.BudgetUnitCostEntryDTO
import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.partner.budget.StaffCostTypeDTO
import io.cloudflight.jems.api.project.dto.partner.budget.StaffCostUnitTypeDTO
import io.cloudflight.jems.server.project.service.partner.model.BudgetCosts
import io.cloudflight.jems.server.project.service.partner.model.BudgetGeneralCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetStaffCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetTravelAndAccommodationCostEntry
import io.cloudflight.jems.server.project.service.partner.model.BudgetUnitCostEntry
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.partner.model.StaffCostType
import io.cloudflight.jems.server.project.service.partner.model.StaffCostUnitType

fun ProjectPartnerBudgetOptions.toProjectPartnerBudgetOptionsDto() = ProjectPartnerBudgetOptionsDto(
    officeAndAdministrationOnStaffCostsFlatRate = this.officeAndAdministrationOnStaffCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate = this.staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate = otherCostsOnStaffCostsFlatRate
)

fun ProjectPartnerBudgetOptionsDto.toProjectPartnerBudgetOptions(partnerId: Long) = ProjectPartnerBudgetOptions(
    partnerId = partnerId,
    officeAndAdministrationOnStaffCostsFlatRate = this.officeAndAdministrationOnStaffCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate = this.staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate = this.otherCostsOnStaffCostsFlatRate
)

fun StaffCostUnitType.toStaffCostUnitTypeDTO() = StaffCostUnitTypeDTO.valueOf(this.key)
fun StaffCostUnitTypeDTO.toStaffCostUnitType() = StaffCostUnitType.valueOf(this.key)
fun StaffCostType.toStaffCostTypeDTO() = StaffCostTypeDTO.valueOf(this.key)
fun StaffCostTypeDTO.toStaffCostType() = StaffCostType.valueOf(this.key)

fun List<BudgetStaffCostEntry>.toBudgetStaffCostEntryDTOList() = this.map { it.toBudgetStaffCostEntryDTO() }

fun BudgetStaffCostEntry.toBudgetStaffCostEntryDTO() = BudgetStaffCostEntryDTO(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    unitType = unitType?.toStaffCostUnitTypeDTO(),
    type = type?.toStaffCostTypeDTO(),
    description = description,
    comment = comment
)

fun List<BudgetStaffCostEntryDTO>.toBudgetStaffCostEntryList() = this.map { it.toBudgetStaffCostEntry() }

fun BudgetStaffCostEntryDTO.toBudgetStaffCostEntry() = BudgetStaffCostEntry(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    unitType = unitType?.toStaffCostUnitType(),
    type = type?.toStaffCostType(),
    description = description,
    comment = comment
)

fun List<BudgetTravelAndAccommodationCostEntry>.toBudgetTravelAndAccommodationCostsEntryDTOList() = this.map { it.toBudgetTravelAndAccommodationCostsEntryDTO() }

fun BudgetTravelAndAccommodationCostEntry.toBudgetTravelAndAccommodationCostsEntryDTO() = BudgetTravelAndAccommodationCostEntryDTO(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    unitType = unitType,
    description = description
)

fun List<BudgetTravelAndAccommodationCostEntryDTO>.toBudgetTravelAndAccommodationCostEntryList() = this.map { it.toBudgetTravelAndAccommodationCostEntry() }

fun BudgetTravelAndAccommodationCostEntryDTO.toBudgetTravelAndAccommodationCostEntry() = BudgetTravelAndAccommodationCostEntry(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    unitType = unitType,
    description = description
)

fun List<BudgetUnitCostEntry>.toBudgetUnitCostEntryDTOList() = this.map { it.toBudgetUnitCostEntryDTO() }

fun BudgetUnitCostEntry.toBudgetUnitCostEntryDTO() = BudgetUnitCostEntryDTO(
    id = id,
    numberOfUnits = numberOfUnits,
    rowSum = rowSum,
    unitCostId = unitCostId
)

fun List<BudgetUnitCostEntryDTO>.toBudgetUnitCostEntryList() = this.map { it.toBudgetUnitCostEntry() }

fun BudgetUnitCostEntryDTO.toBudgetUnitCostEntry() = BudgetUnitCostEntry(
    id = id,
    numberOfUnits = numberOfUnits,
    rowSum = rowSum,
    unitCostId = unitCostId
)

fun List<BudgetGeneralCostEntry>.toBudgetGeneralCostsEntryDTOList() = this.map { it.toBudgetGeneralCostsEntryDTO() }

fun BudgetGeneralCostEntry.toBudgetGeneralCostsEntryDTO() = BudgetGeneralCostEntryDTO(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    unitType = unitType,
    investmentId = investmentId,
    awardProcedures = awardProcedures,
    description = description
)

fun List<BudgetGeneralCostEntryDTO>.toBudgetGeneralCostEntryList() = this.map { it.toBudgetGeneralCostEntry() }

fun BudgetGeneralCostEntryDTO.toBudgetGeneralCostEntry() = BudgetGeneralCostEntry(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    unitType = unitType,
    investmentId = investmentId,
    awardProcedures = awardProcedures,
    description = description
)

fun BudgetCosts.toBudgetCostsDTO() = BudgetCostsDTO(
    staffCosts = staffCosts.toBudgetStaffCostEntryDTOList(),
    travelCosts = travelCosts.toBudgetTravelAndAccommodationCostsEntryDTOList(),
    externalCosts = externalCosts.toBudgetGeneralCostsEntryDTOList(),
    equipmentCosts = equipmentCosts.toBudgetGeneralCostsEntryDTOList(),
    infrastructureCosts = infrastructureCosts.toBudgetGeneralCostsEntryDTOList(),
    unitCosts = unitCosts.toBudgetUnitCostEntryDTOList()
)
