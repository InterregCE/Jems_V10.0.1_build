package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory

interface ExpenditureCostWithCategory {

    fun getCategory(): BudgetCostCategory

    fun ReportBudgetCategory.translateCostCategory(): BudgetCostCategory {
        return when (this) {
            ReportBudgetCategory.StaffCosts -> BudgetCostCategory.Staff
            ReportBudgetCategory.OfficeAndAdministrationCosts -> BudgetCostCategory.Office
            ReportBudgetCategory.TravelAndAccommodationCosts -> BudgetCostCategory.Travel
            ReportBudgetCategory.ExternalCosts -> BudgetCostCategory.External
            ReportBudgetCategory.EquipmentCosts -> BudgetCostCategory.Equipment
            ReportBudgetCategory.InfrastructureCosts -> BudgetCostCategory.Infrastructure
            ReportBudgetCategory.Multiple -> BudgetCostCategory.UnitCost
            ReportBudgetCategory.SpfCosts -> BudgetCostCategory.SpfCost
        }
    }

}
