package io.cloudflight.jems.server.project.controller.report.expenditureCosts

import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportExpenditureCostDTO
import io.cloudflight.jems.server.project.service.report.model.PartnerReportExpenditureCost
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportMapper::class.java)

fun PartnerReportExpenditureCost.toDto() =
    mapper.map(this)

fun PartnerReportExpenditureCostDTO.toModel() =
    mapper.map(this)

fun List<PartnerReportExpenditureCostDTO>.toModel() = map { mapper.map(it) }.toList()
fun List<PartnerReportExpenditureCost>.toDto() = map { mapper.map(it) }.toList()

@Mapper
interface ProjectPartnerReportMapper {
    fun map(partnerReportExpenditureCost: PartnerReportExpenditureCost): PartnerReportExpenditureCostDTO
    fun map(partnerReportExpenditureCostDTO: PartnerReportExpenditureCostDTO): PartnerReportExpenditureCost
}
