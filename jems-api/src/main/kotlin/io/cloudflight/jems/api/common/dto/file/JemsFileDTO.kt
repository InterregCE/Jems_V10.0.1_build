package io.cloudflight.jems.api.common.dto.file

import java.time.ZonedDateTime

data class JemsFileDTO(
    val id: Long,
    val name: String,
    val type: JemsFileTypeDTO,
    val uploaded: ZonedDateTime,
    val author: UserSimpleDTO,
    val size: Long,
    val sizeString: String,
    val description: String
)
