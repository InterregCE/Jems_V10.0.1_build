package io.cloudflight.jems.server.project.controller.report.control.overview

import io.cloudflight.jems.api.project.dto.report.partner.control.overview.ControlWorkOverviewDTO
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportControlOverviewMapper::class.java)

fun ControlWorkOverview.toDto() = mapper.map(this)

@Mapper
interface ProjectPartnerReportControlOverviewMapper {
    fun map(overview: ControlWorkOverview): ControlWorkOverviewDTO
}
