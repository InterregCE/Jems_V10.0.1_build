package io.cloudflight.jems.server.project.controller.report.procurement.beneficial

import io.cloudflight.jems.api.project.dto.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialDTO
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportProcurementBeneficialMapper::class.java)

fun ProjectPartnerReportProcurementBeneficialOwner.toDto() = mapper.map(this)

fun List<ProjectPartnerReportProcurementBeneficialOwner>.toDto() = map { mapper.map(it) }

fun ProjectPartnerReportProcurementBeneficialChangeDTO.toModel() = ProjectPartnerReportProcurementBeneficialChange(
    id = id ?: 0L,
    firstName = firstName,
    lastName = lastName,
    birth = birth,
    vatNumber = vatNumber,
)

fun List<ProjectPartnerReportProcurementBeneficialChangeDTO>.toModel() = map { it.toModel() }

@Mapper
interface ProjectPartnerReportProcurementBeneficialMapper {
    fun map(model: ProjectPartnerReportProcurementBeneficialOwner): ProjectPartnerReportProcurementBeneficialDTO
}
