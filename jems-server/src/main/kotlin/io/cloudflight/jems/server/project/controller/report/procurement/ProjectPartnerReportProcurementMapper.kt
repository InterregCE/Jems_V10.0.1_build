package io.cloudflight.jems.server.project.controller.report.procurement

import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.UpdateProjectPartnerReportProcurementDTO
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportProcurementMapper::class.java)

fun List<ProjectPartnerReportProcurement>.toDto() = map { mapper.map(it) }

fun List<UpdateProjectPartnerReportProcurementDTO>.toModel() = map { mapper.map(it) }

@Mapper
interface ProjectPartnerReportProcurementMapper {
    fun map(model: ProjectPartnerReportProcurement): ProjectPartnerReportProcurementDTO
    fun map(dto: UpdateProjectPartnerReportProcurementDTO): ProjectPartnerReportProcurementUpdate
}
