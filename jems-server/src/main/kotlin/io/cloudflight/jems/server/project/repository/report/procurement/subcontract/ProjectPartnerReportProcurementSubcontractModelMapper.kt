package io.cloudflight.jems.server.project.repository.report.procurement.subcontract

import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.entity.report.procurement.subcontract.ProjectPartnerReportProcurementSubcontractEntity
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChange

fun List<ProjectPartnerReportProcurementSubcontractEntity>.toModel() = map {
    ProjectPartnerReportProcurementSubcontract(
        id = it.id,
        reportId = it.createdInReportId,
        contractName = it.contractName,
        referenceNumber = it.referenceNumber,
        contractDate = it.contractDate,
        contractAmount = it.contractAmount,
        currencyCode = it.currencyCode,
        supplierName = it.supplierName,
        vatNumber = it.vatNumber,
    )
}

fun ProjectPartnerReportProcurementSubcontractChange.toEntity(procurement: ProjectPartnerReportProcurementEntity, reportId: Long) =
    ProjectPartnerReportProcurementSubcontractEntity(
        procurement = procurement,
        createdInReportId = reportId,
        contractName = contractName,
        referenceNumber = referenceNumber,
        contractDate = contractDate,
        contractAmount = contractAmount,
        currencyCode = currencyCode,
        supplierName = supplierName,
        vatNumber = vatNumber,
    )
