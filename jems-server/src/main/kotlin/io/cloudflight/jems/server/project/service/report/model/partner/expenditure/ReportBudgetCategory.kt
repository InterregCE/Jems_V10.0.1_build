package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

enum class ReportBudgetCategory {
    StaffCosts,
    OfficeAndAdministrationCosts,
    TravelAndAccommodationCosts,
    ExternalCosts,
    EquipmentCosts,
    InfrastructureCosts,
    Multiple;

    fun investmentAllowed() = this !in setOf(StaffCosts, TravelAndAccommodationCosts)
    fun procurementAllowed() = this != StaffCosts
    fun invoiceNumberAllowed() = this != StaffCosts
    fun vatAllowed() = this != StaffCosts
}
