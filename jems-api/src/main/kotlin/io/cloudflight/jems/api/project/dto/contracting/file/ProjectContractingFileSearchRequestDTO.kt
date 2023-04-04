package io.cloudflight.jems.api.project.dto.contracting.file

import io.cloudflight.jems.api.common.dto.file.JemsFileTypeDTO

data class ProjectContractingFileSearchRequestDTO(
    val treeNode: JemsFileTypeDTO,

    val filterSubtypes: Set<JemsFileTypeDTO> = emptySet(),
)
