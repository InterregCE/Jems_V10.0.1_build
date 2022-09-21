package io.cloudflight.jems.server.project.service.report.partner.file.control.listProjectPartnerControlReportFile

import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ListControlReportFileInteractor {

    fun list(
        partnerId: Long,
        reportId: Long,
        pageable: Pageable,
    ): Page<ProjectReportFile>

}
