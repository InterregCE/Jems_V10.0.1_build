package io.cloudflight.jems.server.project.service.file.model

import java.io.InputStream

data class ProjectFile(
    val stream: InputStream,
    val name: String,
    val size: Long
)
