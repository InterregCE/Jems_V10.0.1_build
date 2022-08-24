package io.cloudflight.jems.server.project.controller.report.identification

import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportPeriodDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.UpdateProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ProjectPartnerControlReportChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.control.ProjectPartnerControlReportDTO
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.identification.UpdateProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.control.ProjectPartnerControlReport
import io.cloudflight.jems.server.project.service.report.model.identification.control.ProjectPartnerControlReportChange
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportIdentificationMapper::class.java)

fun ProjectPartnerReportIdentification.toDto() =
    mapper.map(this)

fun UpdateProjectPartnerReportIdentificationDTO.toModel() =
    mapper.map(this)

fun List<ProjectPartnerReportPeriod>.toDto() = map { mapper.map(it) }

fun ProjectPartnerControlReport.toDto() =
    mapper.map(this)

fun ProjectPartnerControlReportChangeDTO.toModel() =
    mapper.map(this)

@Mapper
interface ProjectPartnerReportIdentificationMapper {
    fun map(dto: ProjectPartnerReportIdentification): ProjectPartnerReportIdentificationDTO
    @Mapping(target = "summaryAsMap", ignore = true)
    @Mapping(target = "problemsAndDeviationsAsMap", ignore = true)
    fun map(dto: UpdateProjectPartnerReportIdentificationDTO): UpdateProjectPartnerReportIdentification
    fun map(model: ProjectPartnerReportPeriod): ProjectPartnerReportPeriodDTO
    fun map(dto: ProjectPartnerControlReportChangeDTO): ProjectPartnerControlReportChange
    fun map(model: ProjectPartnerControlReport): ProjectPartnerControlReportDTO
}
