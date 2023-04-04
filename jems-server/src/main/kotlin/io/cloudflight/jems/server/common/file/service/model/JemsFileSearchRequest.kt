package io.cloudflight.jems.server.common.file.service.model

data class JemsFileSearchRequest(
    val reportId: Long,
    val treeNode: JemsFileType,

    val filterSubtypes: Set<JemsFileType> = emptySet(),
)
