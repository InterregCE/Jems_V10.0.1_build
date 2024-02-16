package io.cloudflight.jems.api.project.dto.report.project.identification

data class ProjectReportSpendingProfileDTO(
    val lines: List<ProjectReportSpendingProfileLineDto>,
    val total: ProjectReportSpendingProfileTotalDto
)

