package io.cloudflight.jems.server.project.repository.report.procurement.attachment

import io.cloudflight.jems.server.project.entity.report.procurement.file.ProjectPartnerReportProcurementFileEntity
import io.cloudflight.jems.server.project.repository.report.file.toModel
import io.cloudflight.jems.server.project.service.report.model.file.procurement.ProjectReportProcurementFile

fun List<ProjectPartnerReportProcurementFileEntity>.toModel() = map {
    ProjectReportProcurementFile(
        id = it.file.id,
        reportId = it.createdInReportId,
        name = it.file.name,
        type = it.file.type,
        uploaded = it.file.uploaded,
        author = it.file.user.toModel(),
        size = it.file.size,
        description = it.file.description,
    )
}
