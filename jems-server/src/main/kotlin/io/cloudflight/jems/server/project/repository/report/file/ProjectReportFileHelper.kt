package io.cloudflight.jems.server.project.repository.report.file

import io.cloudflight.jems.server.project.service.report.model.partner.file.ProjectReportFileCreate

fun ProjectReportFileCreate.getMinioFullPath() = "$path$name"
