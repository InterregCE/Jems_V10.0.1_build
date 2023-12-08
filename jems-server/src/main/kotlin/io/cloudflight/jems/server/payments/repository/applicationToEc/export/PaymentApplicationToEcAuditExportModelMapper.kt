package io.cloudflight.jems.server.payments.repository.applicationToEc.export

import io.cloudflight.jems.server.common.file.service.toFullModel
import io.cloudflight.jems.server.payments.entity.PaymentApplicationToEcAuditExportEntity
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.project.entity.report.control.certificate.PartnerReportControlFileEntity
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import org.springframework.data.domain.Page

fun Page<PaymentApplicationToEcAuditExportEntity>.toModel() = map { it.toModel() }

fun PaymentApplicationToEcAuditExportEntity.toModel() = PaymentToEcExportMetadata(
    id = id,
    pluginKey = pluginKey,
    generatedFile = generatedFile.toFullModel(),
    accountingYear = accountingYear,
    fundType = fundType,
    requestTime = requestTime,
    exportStartedAt = exportStartedAt,
    exportEndedAt = exportEndedAt,
)
