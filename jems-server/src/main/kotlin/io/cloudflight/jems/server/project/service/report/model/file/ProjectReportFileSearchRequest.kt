package io.cloudflight.jems.server.project.service.report.model.file

data class ProjectReportFileSearchRequest(
    val reportId: Long,
    val treeNode: ProjectPartnerReportFileType,

    val filterSubtypes: Set<ProjectPartnerReportFileType> = emptySet(),
)
