package io.cloudflight.jems.server.project.controller.report.financialOverview

import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.partner.financialOverview.ExpenditureCostCategoryBreakdownDTO
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportFinancialOverviewMapper::class.java)

fun ExpenditureCoFinancingBreakdown.toDto() = mapper.map(this)
fun ExpenditureCostCategoryBreakdown.toDto() = mapper.map(this)

@Mapper
interface ProjectPartnerReportFinancialOverviewMapper {
    fun map(expenditureCoFinancing: ExpenditureCoFinancingBreakdown): ExpenditureCoFinancingBreakdownDTO
    fun map(expenditureCostCategory: ExpenditureCostCategoryBreakdown): ExpenditureCostCategoryBreakdownDTO
}
