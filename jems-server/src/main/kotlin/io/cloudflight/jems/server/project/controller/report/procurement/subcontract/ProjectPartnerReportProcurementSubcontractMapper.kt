package io.cloudflight.jems.server.project.controller.report.procurement.subcontract

import io.cloudflight.jems.api.project.dto.report.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChangeDTO
import io.cloudflight.jems.api.project.dto.report.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontractDTO
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChange
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerReportProcurementSubcontractMapper::class.java)

fun ProjectPartnerReportProcurementSubcontract.toDto() = mapper.map(this)

fun List<ProjectPartnerReportProcurementSubcontract>.toDto() = map { mapper.map(it) }

fun ProjectPartnerReportProcurementSubcontractChangeDTO.toModel() = ProjectPartnerReportProcurementSubcontractChange(
    id = id ?: 0L,
    contractName = contractName,
    referenceNumber = referenceNumber,
    contractDate = contractDate,
    contractAmount = contractAmount,
    currencyCode = currencyCode,
    supplierName = supplierName,
    vatNumber = vatNumber,
)

fun List<ProjectPartnerReportProcurementSubcontractChangeDTO>.toModel() = map { it.toModel() }

@Mapper
interface ProjectPartnerReportProcurementSubcontractMapper {
    fun map(model: ProjectPartnerReportProcurementSubcontract): ProjectPartnerReportProcurementSubcontractDTO
}
