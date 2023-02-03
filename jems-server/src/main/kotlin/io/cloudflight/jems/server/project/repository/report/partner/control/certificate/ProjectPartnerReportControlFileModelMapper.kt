package io.cloudflight.jems.server.project.repository.report.partner.control.certificate

import io.cloudflight.jems.server.common.file.service.toFullModel
import io.cloudflight.jems.server.project.entity.report.control.certificate.PartnerReportControlFileEntity
import io.cloudflight.jems.server.project.repository.report.partner.toModel
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import org.springframework.data.domain.Page

fun Page<PartnerReportControlFileEntity>.toModel() = map { it.toModel() }

fun PartnerReportControlFileEntity.toModel() = PartnerReportControlFile(
    id = id,
    reportId = reportId,
    generatedFile = generatedFile.toFullModel(),
    signedFile = signedFile?.toModel(),
)
