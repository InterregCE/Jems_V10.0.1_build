package io.cloudflight.jems.server.project.controller.report.partner.procurement

import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.ProjectPartnerReportProcurementDTO
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurementChange
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.data.domain.Page

private val mapper = Mappers.getMapper(ProjectPartnerReportProcurementMapper::class.java)

fun ProjectPartnerReportProcurement.toDto() = mapper.map(this)

fun Page<ProjectPartnerReportProcurement>.toDto() = map { mapper.map(it) }

fun ProjectPartnerReportProcurementChangeDTO.toModel() = ProjectPartnerReportProcurementChange(
    id = id ?: 0L,
    contractName = contractName,
    referenceNumber = referenceNumber,
    contractDate = contractDate,
    contractType = contractType,
    contractAmount = contractAmount,
    currencyCode = currencyCode,
    supplierName = supplierName,
    vatNumber = vatNumber,
    comment = comment,
)

@Mapper
interface ProjectPartnerReportProcurementMapper {
    fun map(model: ProjectPartnerReportProcurement): ProjectPartnerReportProcurementDTO
}
