package io.cloudflight.jems.server.project.repository.report.financialOverview

import io.cloudflight.jems.server.project.entity.report.financialOverview.ReportProjectPartnerExpenditureCostCategoryEntity
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ReportExpenditureCostCategory

fun ReportProjectPartnerExpenditureCostCategoryEntity.toModel() = ReportExpenditureCostCategory(
    options = ProjectPartnerBudgetOptions(
        partnerId = reportEntity.partnerId,
        officeAndAdministrationOnStaffCostsFlatRate = officeAndAdministrationOnStaffCostsFlatRate,
        officeAndAdministrationOnDirectCostsFlatRate = officeAndAdministrationOnDirectCostsFlatRate,
        travelAndAccommodationOnStaffCostsFlatRate = travelAndAccommodationOnStaffCostsFlatRate,
        staffCostsFlatRate = staffCostsFlatRate,
        otherCostsOnStaffCostsFlatRate = otherCostsOnStaffCostsFlatRate,
    ),
    totalsFromAF = BudgetCostsCalculationResultFull(
        staff = staffTotal,
        office = officeTotal,
        travel = travelTotal,
        external = externalTotal,
        equipment = equipmentTotal,
        infrastructure = infrastructureTotal,
        other = otherTotal,
        lumpSum = lumpSumTotal,
        unitCost = unitCostTotal,
        sum = sumTotal,
    ),
    currentlyReported = BudgetCostsCalculationResultFull(
        staff = staffCurrent,
        office = officeCurrent,
        travel = travelCurrent,
        external = externalCurrent,
        equipment = equipmentCurrent,
        infrastructure = infrastructureCurrent,
        other = otherCurrent,
        lumpSum = lumpSumCurrent,
        unitCost = unitCostCurrent,
        sum = sumCurrent,
    ),
    previouslyReported = BudgetCostsCalculationResultFull(
        staff = staffPreviouslyReported,
        office = officePreviouslyReported,
        travel = travelPreviouslyReported,
        external = externalPreviouslyReported,
        equipment = equipmentPreviouslyReported,
        infrastructure = infrastructurePreviouslyReported,
        other = otherPreviouslyReported,
        lumpSum = lumpSumPreviouslyReported,
        unitCost = unitCostPreviouslyReported,
        sum = sumPreviouslyReported,
    ),
)
