package io.cloudflight.jems.server.common.file.service.model

import java.time.ZonedDateTime

data class JemsFileMetadata(
    val id: Long,
    val name: String,
    val uploaded: ZonedDateTime,
)
