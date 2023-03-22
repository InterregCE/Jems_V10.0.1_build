package io.cloudflight.jems.server.project.repository.report.project.financialOverview.costCategory

import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCostCategoryEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import io.cloudflight.jems.server.project.repository.report.partner.model.PerPartnerCertificateCostCategory
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory

fun ReportProjectCertificateCostCategoryEntity.toModel() = ReportCertificateCostCategory(
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
    )
)

fun List<PerPartnerCertificateCostCategory>.toModel(
    partnersAvailable: List<ProjectReportSpendingProfileEntity>,
): List<PerPartnerCostCategoryBreakdownLine> {
    val partnersById = partnersAvailable.associateBy { it.id.partnerId }
    return map {
        val partnerData = partnersById[it.partnerId]!!
        return@map PerPartnerCostCategoryBreakdownLine(
            partnerId = partnerData.id.partnerId,
            partnerNumber = partnerData.partnerNumber,
            partnerAbbreviation = partnerData.partnerAbbreviation,
            partnerRole = partnerData.partnerRole,
            country = partnerData.country,
            officeAndAdministrationOnStaffCostsFlatRate = it.officeAndAdministrationOnStaffCostsFlatRate,
            officeAndAdministrationOnDirectCostsFlatRate = it.officeAndAdministrationOnDirectCostsFlatRate,
            travelAndAccommodationOnStaffCostsFlatRate = it.travelAndAccommodationOnStaffCostsFlatRate,
            staffCostsFlatRate = it.staffCostsFlatRate,
            otherCostsOnStaffCostsFlatRate = it.otherCostsOnStaffCostsFlatRate,
            current = BudgetCostsCalculationResultFull(
                staff = it.staffCurrent,
                office = it.officeCurrent,
                travel = it.travelCurrent,
                external = it.externalCurrent,
                equipment = it.equipmentCurrent,
                infrastructure = it.infrastructureCurrent,
                other = it.otherCurrent,
                lumpSum = it.lumpSumCurrent,
                unitCost = it.unitCostCurrent,
                sum = it.sumCurrent,
            ),
            afterControl = BudgetCostsCalculationResultFull(
                staff = it.staffAfterControl,
                office = it.officeAfterControl,
                travel = it.travelAfterControl,
                external = it.externalAfterControl,
                equipment = it.equipmentAfterControl,
                infrastructure = it.infrastructureAfterControl,
                other = it.otherAfterControl,
                lumpSum = it.lumpSumAfterControl,
                unitCost = it.unitCostAfterControl,
                sum = it.sumAfterControl,
            ),
        )
    }
}
