package io.cloudflight.jems.server.common.file.service.model

import java.time.ZonedDateTime

data class JemsFile(
    val id: Long,
    var name: String,
    val type: JemsFileType,
    val uploaded: ZonedDateTime,
    val author: UserSimple,
    val size: Long,
    var description: String,
    val indexedPath: String,
) {
    fun toSimple() = JemsFileMetadata(
        id = id,
        name = name,
        uploaded = uploaded,
    )
}
