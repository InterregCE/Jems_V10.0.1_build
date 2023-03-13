package io.cloudflight.jems.server.project.service.report.project.annexes.list

import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileSearchRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListProjectReportAnnexesInteractor {
    fun list(
        projectId: Long,
        reportId: Long,
        pageable: Pageable,
        searchRequest: JemsFileSearchRequest,
    ): Page<JemsFile>
}
