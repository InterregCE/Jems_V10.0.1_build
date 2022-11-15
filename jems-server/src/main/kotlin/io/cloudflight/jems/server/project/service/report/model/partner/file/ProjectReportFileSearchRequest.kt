package io.cloudflight.jems.server.project.service.report.model.partner.file

data class ProjectReportFileSearchRequest(
    val reportId: Long,
    val treeNode: ProjectPartnerReportFileType,

    val filterSubtypes: Set<ProjectPartnerReportFileType> = emptySet(),
)
