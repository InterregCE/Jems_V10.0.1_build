package io.cloudflight.jems.server.project.service.report.model.file

data class JemsFileSearchRequest(
    val reportId: Long,
    val treeNode: JemsFileType,

    val filterSubtypes: Set<JemsFileType> = emptySet(),
)
