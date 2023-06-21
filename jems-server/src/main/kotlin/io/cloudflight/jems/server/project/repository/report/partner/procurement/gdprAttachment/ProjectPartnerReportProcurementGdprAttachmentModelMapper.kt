package io.cloudflight.jems.server.project.repository.report.partner.procurement.gdprAttachment

import io.cloudflight.jems.server.common.file.service.toModel
import io.cloudflight.jems.server.project.entity.report.partner.procurement.file.ProjectPartnerReportProcurementGdprFileEntity
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile

fun List<ProjectPartnerReportProcurementGdprFileEntity>.toModel() = map {
    ProjectReportProcurementFile(
        id = it.file.id,
        reportId = it.createdInReportId,
        name = it.file.name,
        type = it.file.type,
        uploaded = it.file.uploaded,
        author = it.file.user.toModel(),
        size = it.file.size,
        description = it.file.description
    )
}
