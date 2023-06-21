package io.cloudflight.jems.server.project.controller.contracting.fileManagement

import io.cloudflight.jems.api.project.dto.contracting.file.ProjectContractingFileSearchRequestDTO
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest

fun ProjectContractingFileSearchRequestDTO.toModel() = ProjectContractingFileSearchRequest(
    treeNode = JemsFileType.valueOf(treeNode.name),
    filterSubtypes = filterSubtypes.mapTo(HashSet()) { JemsFileType.valueOf(it.name) }
)
