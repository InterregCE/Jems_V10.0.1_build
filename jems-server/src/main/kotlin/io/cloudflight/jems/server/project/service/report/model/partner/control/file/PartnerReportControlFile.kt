package io.cloudflight.jems.server.project.service.report.model.partner.control.file

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata

data class PartnerReportControlFile(
    val id: Long,
    val reportId: Long,
    val generatedFile: JemsFile,
    val signedFile: JemsFileMetadata?,
)
