package io.cloudflight.ems.exception

import io.minio.ObjectStat

class DuplicateFileException(
    objectStat: ObjectStat
) : Exception("File already exists. Info about file $objectStat")
