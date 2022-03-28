package io.cloudflight.jems.server.project.service.report.model.file

import java.io.InputStream

data class ProjectReportFileCreate(
    val projectId: Long,
    val partnerId: Long,
    val name: String,
    val path: String,
    val type: ProjectPartnerReportFileType,
    val size: Long,
    val content: InputStream,
    val userId: Long,
)
