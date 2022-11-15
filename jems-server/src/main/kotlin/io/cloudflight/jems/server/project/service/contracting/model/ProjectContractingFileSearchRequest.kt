package io.cloudflight.jems.server.project.service.contracting.model

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType

data class ProjectContractingFileSearchRequest(
    val treeNode: JemsFileType,

    val filterSubtypes: Set<JemsFileType> = emptySet(),
)
