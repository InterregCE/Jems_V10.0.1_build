package io.cloudflight.jems.api.project.dto.report.file

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO

data class PartnerReportControlFileDTO(
    val id: Long,
    val reportId: Long,
    val generatedFile: JemsFileDTO,
    val signedFile: JemsFileMetadataDTO?,
)
