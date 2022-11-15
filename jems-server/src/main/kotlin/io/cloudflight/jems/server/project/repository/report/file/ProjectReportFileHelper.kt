package io.cloudflight.jems.server.project.repository.report.file

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate

fun JemsFileCreate.getMinioFullPath() = "$path$name"
