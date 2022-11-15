package io.cloudflight.jems.server.project.service.contracting.model

import io.cloudflight.jems.server.project.service.report.model.partner.file.ProjectPartnerReportFileType

data class ProjectContractingFileSearchRequest(
    val treeNode: ProjectPartnerReportFileType,

    val filterSubtypes: Set<ProjectPartnerReportFileType> = emptySet(),
)
