package io.cloudflight.jems.server.project.service.file.model

import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import java.io.InputStream

data class ProjectFile(
    val stream: InputStream,
    val name: String,
    val size: Long
) {
    fun getFileMetadata(
        projectId: Long,
        partnerId: Long,
        location: String,
        type: ProjectPartnerReportFileType,
        userId: Long,
    ) = ProjectReportFileCreate(
        projectId = projectId,
        partnerId = partnerId,
        name = name,
        path = location,
        type = type,
        size = size,
        content = stream,
        userId = userId,
    )
}
