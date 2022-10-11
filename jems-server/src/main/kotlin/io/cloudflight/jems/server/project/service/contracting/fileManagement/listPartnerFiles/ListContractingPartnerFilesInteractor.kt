package io.cloudflight.jems.server.project.service.contracting.fileManagement.listPartnerFiles

import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingFileSearchRequest
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListContractingPartnerFilesInteractor {

    fun listPartner(
        partnerId: Long,
        pageable: Pageable,
        searchRequest: ProjectContractingFileSearchRequest,
    ): Page<ProjectReportFile>
}
