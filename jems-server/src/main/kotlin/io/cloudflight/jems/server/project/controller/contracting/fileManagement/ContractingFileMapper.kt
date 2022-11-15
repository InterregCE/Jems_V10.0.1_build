package io.cloudflight.jems.server.project.controller.contracting.fileManagement

import io.cloudflight.jems.api.project.dto.contracting.file.ProjectContractingFileSearchRequestDTO
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType

fun ProjectContractingFileSearchRequestDTO.toModel() = ProjectContractingFileSearchRequest(
    treeNode = JemsFileType.valueOf(treeNode.name),
    filterSubtypes = filterSubtypes.mapTo(HashSet()) { JemsFileType.valueOf(it.name) }
)
