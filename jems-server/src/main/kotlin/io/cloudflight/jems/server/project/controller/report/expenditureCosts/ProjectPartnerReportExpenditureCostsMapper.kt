package io.cloudflight.jems.server.project.controller.report.expenditureCosts

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportExpenditureCostDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportLumpSumDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportUnitCostDTO
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportUnitCost
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportMapper::class.java)

fun ProjectPartnerReportExpenditureCost.toDto() =
    mapper.map(this)

fun ProjectPartnerReportExpenditureCostDTO.toModel() =
    mapper.map(this)

fun List<ProjectPartnerReportExpenditureCostDTO>.toModel() = map { mapper.map(it) }.toList()
fun List<ProjectPartnerReportExpenditureCost>.toDto() = map { mapper.map(it) }.toList()
fun List<ProjectPartnerReportLumpSum>.toLumpSumDto() = map { mapper.map(it) }.toList()
fun List<ProjectPartnerReportUnitCost>.toUnitCostDto() = map { mapper.map(it) }

@Mapper
interface ProjectPartnerReportMapper {
    fun map(partnerReportExpenditureCost: ProjectPartnerReportExpenditureCost): ProjectPartnerReportExpenditureCostDTO
    fun map(partnerReportExpenditureCostDTO: ProjectPartnerReportExpenditureCostDTO): ProjectPartnerReportExpenditureCost
    fun map(lumpSum: ProjectPartnerReportLumpSum): ProjectPartnerReportLumpSumDTO
    fun map(unitCost: ProjectPartnerReportUnitCost): ProjectPartnerReportUnitCostDTO
}
