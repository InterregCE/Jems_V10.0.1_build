package io.cloudflight.jems.server.project.service.contracting.fileManagement.listContractingFiles

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListContractingFilesInteractor {

    fun list(
        projectId: Long,
        partnerId: Long?,
        pageable: Pageable,
        searchRequest: ProjectContractingFileSearchRequest,
    ): Page<JemsFile>

}
