package io.cloudflight.jems.server.project.controller.report.project.verification.overview

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.CertificateVerificationDeductionOverviewDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceBreakdownDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceBreakdownLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceBreakdownSplitLineDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.FinancingSourceFundDTO
import io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification.VerificationWorkOverviewDTO
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.CertificateVerificationDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverview
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectReportVerificationOverviewMapper::class.java)

fun VerificationWorkOverview.toDto() = mapper.map(this)

fun FinancingSourceBreakdown.toDto() = FinancingSourceBreakdownDTO(
    sources = sources.map { it.toDto() },
    total = total.toDto(),
)
fun FinancingSourceBreakdownLine.toDto() = FinancingSourceBreakdownLineDTO(
    partnerReportId = partnerReportId,
    partnerReportNumber = partnerReportNumber,
    spfLine = spfLine,
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

fun List<CertificateVerificationDeductionOverview>.toDto() = this.map { mapper.map(it) }

@Mapper
interface ProjectReportVerificationOverviewMapper {
    fun map(overview: VerificationWorkOverview): VerificationWorkOverviewDTO

    fun map(model: CertificateVerificationDeductionOverview): CertificateVerificationDeductionOverviewDTO
}
