package io.cloudflight.jems.api.project.dto.contracting.file

import io.cloudflight.jems.api.project.dto.report.file.ProjectPartnerReportFileTypeDTO

data class ProjectContractingFileSearchRequestDTO(
    val treeNode: ProjectPartnerReportFileTypeDTO,

    val filterSubtypes: Set<ProjectPartnerReportFileTypeDTO> = emptySet(),
)
