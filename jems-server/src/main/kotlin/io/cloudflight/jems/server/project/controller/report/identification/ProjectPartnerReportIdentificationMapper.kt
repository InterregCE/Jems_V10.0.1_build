package io.cloudflight.jems.server.project.controller.report.identification

import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.UpdateProjectPartnerReportIdentificationDTO
import io.cloudflight.jems.server.project.service.report.model.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.identification.UpdateProjectPartnerReportIdentification
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportIdentificationMapper::class.java)

fun ProjectPartnerReportIdentification.toDto() =
    mapper.map(this)

fun UpdateProjectPartnerReportIdentificationDTO.toModel() =
    mapper.map(this)

@Mapper
interface ProjectPartnerReportIdentificationMapper {
    fun map(dto: ProjectPartnerReportIdentification): ProjectPartnerReportIdentificationDTO
    @Mapping(target = "summaryAsMap", ignore = true)
    @Mapping(target = "problemsAndDeviationsAsMap", ignore = true)
    fun map(dto: UpdateProjectPartnerReportIdentificationDTO): UpdateProjectPartnerReportIdentification
}
