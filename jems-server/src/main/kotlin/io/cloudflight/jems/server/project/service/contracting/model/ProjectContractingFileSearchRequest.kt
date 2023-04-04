package io.cloudflight.jems.server.project.service.contracting.model

import io.cloudflight.jems.server.common.file.service.model.JemsFileType

data class ProjectContractingFileSearchRequest(
    val treeNode: JemsFileType,

    val filterSubtypes: Set<JemsFileType> = emptySet(),
)
