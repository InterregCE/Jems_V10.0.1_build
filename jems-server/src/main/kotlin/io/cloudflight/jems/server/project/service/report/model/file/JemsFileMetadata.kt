package io.cloudflight.jems.server.project.service.report.model.file

import java.time.ZonedDateTime

data class JemsFileMetadata(
    val id: Long,
    val name: String,
    val uploaded: ZonedDateTime,
)
