package io.cloudflight.jems.server.project.controller.partner.budget

import io.cloudflight.jems.api.project.dto.partner.budget.*
import io.cloudflight.jems.server.project.service.partner.model.*
import java.math.BigDecimal


fun ProjectPartnerBudgetOptions.toProjectPartnerBudgetOptionsDto() = ProjectPartnerBudgetOptionsDto(
    officeAndAdministrationOnStaffCostsFlatRate = this.officeAndAdministrationOnStaffCostsFlatRate,
    officeAndAdministrationOnDirectCostsFlatRate = this.officeAndAdministrationOnDirectCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate = this.staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate = otherCostsOnStaffCostsFlatRate
)

fun ProjectPartnerBudgetOptionsDto.toProjectPartnerBudgetOptions(partnerId: Long) = ProjectPartnerBudgetOptions(
    partnerId = partnerId,
    officeAndAdministrationOnStaffCostsFlatRate = this.officeAndAdministrationOnStaffCostsFlatRate,
    officeAndAdministrationOnDirectCostsFlatRate = this.officeAndAdministrationOnDirectCostsFlatRate,
    travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
    staffCostsFlatRate = this.staffCostsFlatRate,
    otherCostsOnStaffCostsFlatRate = this.otherCostsOnStaffCostsFlatRate
)

fun BudgetPeriod.toBudgetPeriodDTO() = BudgetPeriodDTO(this.number, this.amount)
fun Set<BudgetPeriod>.toBudgetPeriodDTOs() = this.map { it.toBudgetPeriodDTO() }.toMutableSet()
fun BudgetPeriodDTO.toBudgetPeriod() = BudgetPeriod(this.number, this.amount ?: BigDecimal.ZERO)
fun Set<BudgetPeriodDTO>.toBudgetPeriods() = this.map { it.toBudgetPeriod() }.toMutableSet()

fun List<BudgetStaffCostEntry>.toBudgetStaffCostEntryDTOList() = this.map { it.toBudgetStaffCostEntryDTO() }

fun BudgetStaffCostEntry.toBudgetStaffCostEntryDTO() = BudgetStaffCostEntryDTO(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.toBudgetPeriodDTOs(),
    unitCostId = unitCostId,
    unitType = unitType,
    description = description,
    comments = comments
)

fun List<BudgetStaffCostEntryDTO>.toBudgetStaffCostEntryList() = this.map { it.toBudgetStaffCostEntry() }

fun BudgetStaffCostEntryDTO.toBudgetStaffCostEntry() = BudgetStaffCostEntry(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.toBudgetPeriods(),
    unitCostId = unitCostId,
    unitType = unitType,
    description = description,
    comments = comments
)

fun List<BudgetTravelAndAccommodationCostEntry>.toBudgetTravelAndAccommodationCostsEntryDTOList() =
    this.map { it.toBudgetTravelAndAccommodationCostsEntryDTO() }

fun BudgetTravelAndAccommodationCostEntry.toBudgetTravelAndAccommodationCostsEntryDTO() =
    BudgetTravelAndAccommodationCostEntryDTO(
        id = id,
        numberOfUnits = numberOfUnits,
        pricePerUnit = pricePerUnit,
        rowSum = rowSum,
        budgetPeriods = budgetPeriods.toBudgetPeriodDTOs(),
        unitCostId = unitCostId,
        unitType = unitType,
        description = description,
        comments = comments
    )

fun List<BudgetTravelAndAccommodationCostEntryDTO>.toBudgetTravelAndAccommodationCostEntryList() =
    this.map { it.toBudgetTravelAndAccommodationCostEntry() }

fun BudgetTravelAndAccommodationCostEntryDTO.toBudgetTravelAndAccommodationCostEntry() =
    BudgetTravelAndAccommodationCostEntry(
        id = id,
        numberOfUnits = numberOfUnits,
        pricePerUnit = pricePerUnit,
        rowSum = rowSum,
        budgetPeriods = budgetPeriods.toBudgetPeriods(),
        unitCostId = unitCostId,
        unitType = unitType,
        description = description,
        comments = comments
    )

fun List<BudgetUnitCostEntry>.toBudgetUnitCostEntryDTOList() = this.map { it.toBudgetUnitCostEntryDTO() }

fun BudgetUnitCostEntry.toBudgetUnitCostEntryDTO() = BudgetUnitCostEntryDTO(
    id = id,
    numberOfUnits = numberOfUnits,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.toBudgetPeriodDTOs(),
    unitCostId = unitCostId
)

fun List<BudgetUnitCostEntryDTO>.toBudgetUnitCostEntryList() = this.map { it.toBudgetUnitCostEntry() }

fun BudgetUnitCostEntryDTO.toBudgetUnitCostEntry() = BudgetUnitCostEntry(
    id = id,
    numberOfUnits = numberOfUnits,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.toBudgetPeriods(),
    unitCostId = unitCostId
)

fun List<BudgetGeneralCostEntry>.toBudgetGeneralCostsEntryDTOList() = this.map { it.toBudgetGeneralCostsEntryDTO() }

fun BudgetGeneralCostEntry.toBudgetGeneralCostsEntryDTO() = BudgetGeneralCostEntryDTO(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.toBudgetPeriodDTOs(),
    unitType = unitType,
    investmentId = investmentId,
    unitCostId = unitCostId,
    awardProcedures = awardProcedures,
    description = description,
    comments = comments
)

fun List<BudgetGeneralCostEntryDTO>.toBudgetGeneralCostEntryList() = this.map { it.toBudgetGeneralCostEntry() }

fun BudgetGeneralCostEntryDTO.toBudgetGeneralCostEntry() = BudgetGeneralCostEntry(
    id = id,
    numberOfUnits = numberOfUnits,
    pricePerUnit = pricePerUnit,
    rowSum = rowSum,
    budgetPeriods = budgetPeriods.toBudgetPeriods(),
    unitType = unitType,
    investmentId = investmentId,
    unitCostId = unitCostId,
    awardProcedures = awardProcedures,
    description = description,
    comments = comments
)

fun BudgetCosts.toBudgetCostsDTO() = BudgetCostsDTO(
    staffCosts = staffCosts.toBudgetStaffCostEntryDTOList(),
    travelCosts = travelCosts.toBudgetTravelAndAccommodationCostsEntryDTOList(),
    externalCosts = externalCosts.toBudgetGeneralCostsEntryDTOList(),
    equipmentCosts = equipmentCosts.toBudgetGeneralCostsEntryDTOList(),
    infrastructureCosts = infrastructureCosts.toBudgetGeneralCostsEntryDTOList(),
    unitCosts = unitCosts.toBudgetUnitCostEntryDTOList()
)
