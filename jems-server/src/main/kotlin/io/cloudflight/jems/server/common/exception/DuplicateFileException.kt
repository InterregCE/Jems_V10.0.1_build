package io.cloudflight.jems.server.common.exception

import io.minio.StatObjectResponse
import io.minio.messages.Item
import java.time.ZonedDateTime

class DuplicateFileException : Exception {

    var error: DuplicateFileError

    enum class Origin {
        FILE_STORAGE,
        DATABASE
    }

    data class DuplicateFileError(
        val name: String,
        val updated: ZonedDateTime,
        val origin: Origin
    )

    constructor(item: Item) : super("File already exists. Info about file: $item") {
        error = DuplicateFileError(
            name = item.objectName(),
            updated = item.lastModified(),
            origin = Origin.FILE_STORAGE
        )
    }

    constructor(
        projectId: Long?,
        name: String,
        updated: ZonedDateTime
    ) : super("File already exists. Info about file: projectId: $projectId, identifier: $name") {
        error = DuplicateFileError(
            name = name,
            updated = updated,
            origin = Origin.DATABASE
        )
    }

}
