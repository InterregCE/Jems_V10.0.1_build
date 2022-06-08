package io.cloudflight.jems.api.project.dto.report.file

data class ProjectReportFileSearchRequestDTO(
    val reportId: Long,
    val treeNode: ProjectPartnerReportFileTypeDTO,

    val filterSubtypes: Set<ProjectPartnerReportFileTypeDTO> = emptySet(),
)
