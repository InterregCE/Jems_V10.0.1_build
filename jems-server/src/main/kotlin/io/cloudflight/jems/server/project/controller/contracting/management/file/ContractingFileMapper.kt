package io.cloudflight.jems.server.project.controller.contracting.management.file

import io.cloudflight.jems.api.project.dto.contracting.file.ProjectContractingFileSearchRequestDTO
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType

fun ProjectContractingFileSearchRequestDTO.toModel() = ProjectContractingFileSearchRequest(
    treeNode = ProjectPartnerReportFileType.valueOf(treeNode.name),
    filterSubtypes = filterSubtypes.mapTo(HashSet()) { ProjectPartnerReportFileType.valueOf(it.name) }
)
