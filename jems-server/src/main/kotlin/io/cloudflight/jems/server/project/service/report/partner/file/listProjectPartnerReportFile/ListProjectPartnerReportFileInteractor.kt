package io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile

import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileSearchRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListProjectPartnerReportFileInteractor {

    fun list(
        partnerId: Long,
        pageable: Pageable,
        searchRequest: JemsFileSearchRequest,
    ): Page<JemsFile>

}
