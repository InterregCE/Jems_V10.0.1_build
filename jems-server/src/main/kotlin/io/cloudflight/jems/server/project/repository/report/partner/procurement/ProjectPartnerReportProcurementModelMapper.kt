package io.cloudflight.jems.server.project.repository.report.partner.procurement

import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.partner.procurement.ProjectPartnerReportProcurementEntity
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurementChange
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

fun Page<ProjectPartnerReportProcurementEntity>.toModel() = map { it.toModel() }

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
