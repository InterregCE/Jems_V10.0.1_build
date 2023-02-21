package io.cloudflight.jems.server.project.controller.report.project.financialOverview

import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownDTO
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.CertificateCoFinancingBreakdown
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectReportFinancialOverviewMapper::class.java)

fun CertificateCoFinancingBreakdown.toDto() = mapper.map(this)

@Mapper
interface ProjectReportFinancialOverviewMapper {
    fun map(expenditureCoFinancing: CertificateCoFinancingBreakdown): CertificateCoFinancingBreakdownDTO
}
