package io.cloudflight.jems.api.project.dto.report.file

data class PartnerReportControlFileDTO(
    val id: Long,
    val reportId: Long,
    val generatedFile: ProjectReportFileDTO,
    val signedFile: ProjectReportFileDTO?,
)
