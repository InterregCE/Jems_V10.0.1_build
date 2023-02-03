package io.cloudflight.jems.server.project.service.report.model.partner.control.file

import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata

data class PartnerReportControlFile(
    val id: Long,
    val reportId: Long,
    val generatedFile: JemsFile,
    val signedFile: JemsFileMetadata?,
)
