package io.cloudflight.jems.server.project.service.contracting.management.file.listContractingFiles

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListContractingFilesInteractor {

    fun list(
        projectId: Long,
        partnerId: Long?,
        pageable: Pageable,
        searchRequest: ProjectContractingFileSearchRequest,
    ): Page<ProjectReportFile>

}
