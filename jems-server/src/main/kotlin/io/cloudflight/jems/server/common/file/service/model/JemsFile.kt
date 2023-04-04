package io.cloudflight.jems.server.common.file.service.model

import java.time.ZonedDateTime

data class JemsFile(
    val id: Long,
    val name: String,
    val type: JemsFileType,
    val uploaded: ZonedDateTime,
    val author: UserSimple,
    val size: Long,
    val description: String
)
