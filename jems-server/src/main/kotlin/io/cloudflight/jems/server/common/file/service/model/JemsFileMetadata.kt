package io.cloudflight.jems.server.common.file.service.model

import java.time.ZonedDateTime

data class JemsFileMetadata(
    val id: Long,
    var name: String,
    val uploaded: ZonedDateTime,
)
