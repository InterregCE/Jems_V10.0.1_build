package io.cloudflight.jems.server.project.service.report.partner.file.listProjectPartnerReportFile

import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileSearchRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListProjectPartnerReportFileInteractor {

    fun list(
        partnerId: Long,
        pageable: Pageable,
        searchRequest: JemsFileSearchRequest,
    ): Page<JemsFile>

}
