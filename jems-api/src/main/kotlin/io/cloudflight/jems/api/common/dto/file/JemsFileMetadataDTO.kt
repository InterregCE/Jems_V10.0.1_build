package io.cloudflight.jems.api.common.dto.file

import java.time.ZonedDateTime

data class JemsFileMetadataDTO(
    val id: Long,
    val name: String,
    val uploaded: ZonedDateTime,
)
