package io.cloudflight.jems.server.project.controller.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateLumpSumBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.PerPartnerCostCategoryBreakdownDTO
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectReportFinancialOverviewMapper::class.java)

fun CertificateCoFinancingBreakdown.toDto() = mapper.map(this)
fun CertificateCostCategoryBreakdown.toDto() = mapper.map(this)
fun PerPartnerCostCategoryBreakdown.toDto() = mapper.map(this)
fun CertificateLumpSumBreakdown.toDto() = mapper.map(this)

@Mapper
interface ProjectReportFinancialOverviewMapper {
    fun map(certificateCoFinancing: CertificateCoFinancingBreakdown): CertificateCoFinancingBreakdownDTO
    fun map(certificateCostCategory: CertificateCostCategoryBreakdown): CertificateCostCategoryBreakdownDTO
    fun map(certificatePerPartner: PerPartnerCostCategoryBreakdown): PerPartnerCostCategoryBreakdownDTO
    fun map(certificateLumpSums: CertificateLumpSumBreakdown): CertificateLumpSumBreakdownDTO
}
