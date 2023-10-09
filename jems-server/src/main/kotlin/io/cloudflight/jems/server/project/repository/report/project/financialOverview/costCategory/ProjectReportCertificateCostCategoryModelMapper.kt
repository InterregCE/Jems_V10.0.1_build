package io.cloudflight.jems.server.project.repository.report.project.financialOverview.costCategory

import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateCostCategoryEntity
import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import io.cloudflight.jems.server.project.repository.report.partner.model.PerPartnerCertificateCostCategory
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.ReportCertificateCostCategory
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdownLine
import java.math.BigDecimal

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
        spfCost = spfCostTotal,
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
        spfCost = BigDecimal.ZERO,
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
        spfCost = BigDecimal.ZERO,
        sum = sumPreviouslyReported,
    ),
    currentVerified = BudgetCostsCalculationResultFull(
        staff = staffCurrentVerified,
        office = officeCurrentVerified,
        travel = travelCurrentVerified,
        external = externalCurrentVerified,
        equipment = equipmentCurrentVerified,
        infrastructure = infrastructureCurrentVerified,
        other = otherCurrentVerified,
        lumpSum = lumpSumCurrentVerified,
        unitCost = unitCostCurrentVerified,
        spfCost = BigDecimal.ZERO,
        sum = sumCurrentVerified,
    ),
    previouslyVerified = BudgetCostsCalculationResultFull(
        staff = staffPreviouslyVerified,
        office = officePreviouslyVerified,
        travel = travelPreviouslyVerified,
        external = externalPreviouslyVerified,
        equipment = equipmentPreviouslyVerified,
        infrastructure = infrastructurePreviouslyVerified,
        other = otherPreviouslyVerified,
        lumpSum = lumpSumPreviouslyVerified,
        unitCost = unitCostPreviouslyVerified,
        spfCost = BigDecimal.ZERO,
        sum = sumPreviouslyVerified,
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
                spfCost = BigDecimal.ZERO,
                sum = it.sumCurrent,
            ),
            deduction = BudgetCostsCalculationResultFull(
                staff = it.staffDeduction,
                office = it.officeDeduction,
                travel = it.travelDeduction,
                external = it.externalDeduction,
                equipment = it.equipmentDeduction,
                infrastructure = it.infrastructureDeduction,
                other = it.otherDeduction,
                lumpSum = it.lumpSumDeduction,
                unitCost = it.unitCostDeduction,
                spfCost = BigDecimal.ZERO,
                sum = it.sumDeduction,
            ),
        )
    }
}
