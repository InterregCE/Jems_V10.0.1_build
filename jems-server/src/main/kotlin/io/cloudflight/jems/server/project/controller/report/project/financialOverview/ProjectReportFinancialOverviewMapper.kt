package io.cloudflight.jems.server.project.controller.report.project.financialOverview

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCoFinancingBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateInvestmentBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateLumpSumBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.PerPartnerCostCategoryBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.CertificateUnitCostBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.FinancingSourceBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.FinancingSourceBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.FinancingSourceBreakdownSplitLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.FinancingSourceFundDTO
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.CertificateCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner.PerPartnerCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectReportFinancialOverviewMapper::class.java)

fun CertificateCoFinancingBreakdown.toDto() = mapper.map(this)
fun CertificateCostCategoryBreakdown.toDto() = mapper.map(this)
fun PerPartnerCostCategoryBreakdown.toDto() = mapper.map(this)
fun CertificateLumpSumBreakdown.toDto() = mapper.map(this)
fun CertificateUnitCostBreakdown.toDto() = mapper.map(this)
fun CertificateInvestmentBreakdown.toDto() = mapper.map(this)

fun FinancingSourceBreakdown.toDto() = FinancingSourceBreakdownDTO(
    sources = sources.map { it.toDto() },
    total = total.toDto(),
)
fun FinancingSourceBreakdownLine.toDto() = FinancingSourceBreakdownLineDTO(
    partnerReportId = partnerReportId,
    partnerReportNumber = partnerReportNumber,
    partnerId = partnerId,
    partnerRole = partnerRole?.let { ProjectPartnerRoleDTO.valueOf(it.name) },
    partnerNumber = partnerNumber,
    fundsSorted = fundsSorted.map {
        FinancingSourceFundDTO(
            id = it.first.id,
            type = ProgrammeFundTypeDTO.valueOf(it.first.type.name),
            abbreviation = it.first.abbreviation,
            amount = it.second,
        )
    },
    partnerContribution = partnerContribution,
    publicContribution = publicContribution,
    automaticPublicContribution = automaticPublicContribution,
    privateContribution = privateContribution,
    total = total,
    split = split.map {
        FinancingSourceBreakdownSplitLineDTO(
            fundId = it.fundId,
            value = it.value,
            partnerContribution = it.partnerContribution,
            publicContribution = it.publicContribution,
            automaticPublicContribution = it.automaticPublicContribution,
            privateContribution = it.privateContribution,
            total = it.total,
        )
    },
)

@Mapper
interface ProjectReportFinancialOverviewMapper {
    fun map(certificateCoFinancing: CertificateCoFinancingBreakdown): CertificateCoFinancingBreakdownDTO
    fun map(certificateCostCategory: CertificateCostCategoryBreakdown): CertificateCostCategoryBreakdownDTO
    fun map(certificatePerPartner: PerPartnerCostCategoryBreakdown): PerPartnerCostCategoryBreakdownDTO
    fun map(certificateLumpSums: CertificateLumpSumBreakdown): CertificateLumpSumBreakdownDTO
    fun map(certificateUnitCosts: CertificateUnitCostBreakdown): CertificateUnitCostBreakdownDTO
    fun map(certificateInvestments: CertificateInvestmentBreakdown): CertificateInvestmentBreakdownDTO
}
