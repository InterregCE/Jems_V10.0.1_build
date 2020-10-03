package io.cloudflight.jems.server.exception

import io.minio.ObjectStat
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

    constructor(
        objectStat: ObjectStat
    ) : super("File already exists. Info about file: $objectStat") {
        error = DuplicateFileError(
            name = objectStat.name(),
            updated = objectStat.createdTime(),
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
