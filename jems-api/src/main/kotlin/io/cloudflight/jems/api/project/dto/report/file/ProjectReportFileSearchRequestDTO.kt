package io.cloudflight.jems.api.project.dto.report.file

import io.cloudflight.jems.api.common.dto.file.JemsFileTypeDTO

data class ProjectReportFileSearchRequestDTO(
    val reportId: Long,
    val treeNode: JemsFileTypeDTO,

    val filterSubtypes: Set<JemsFileTypeDTO> = emptySet(),
)
