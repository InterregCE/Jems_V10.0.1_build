package io.cloudflight.jems.server.project.repository.report.procurement

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementChange
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementSummary
import org.springframework.data.domain.Page
import java.time.ZonedDateTime

fun ProjectPartnerReportProcurementEntity.toModel() = ProjectPartnerReportProcurement(
    id = id,
    reportId = reportEntity.id,
    reportNumber = reportEntity.number,
    lastChanged = lastChanged,
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

fun Page<ProjectPartnerReportProcurementEntity>.toModel() = map {
    ProjectPartnerReportProcurementSummary(
        id = it.id,
        reportId = it.reportEntity.id,
        reportNumber = it.reportEntity.number,
        lastChanged = it.lastChanged,
        contractName = it.contractName,
        referenceNumber = it.referenceNumber,
        contractDate = it.contractDate,
        contractType = it.contractType,
        contractAmount = it.contractAmount,
        currencyCode = it.currencyCode,
        supplierName = it.supplierName,
        vatNumber = it.vatNumber,
    )
}

fun ProjectPartnerReportProcurementChange.toEntity(report: ProjectPartnerReportEntity, lastChanged: ZonedDateTime) =
    ProjectPartnerReportProcurementEntity(
        id = id,
        reportEntity = report,
        contractName = contractName,
        referenceNumber = referenceNumber,
        contractDate = contractDate,
        contractType = contractType,
        contractAmount = contractAmount,
        currencyCode = currencyCode,
        supplierName = supplierName,
        vatNumber = vatNumber,
        comment = comment,
        lastChanged = lastChanged,
    )
