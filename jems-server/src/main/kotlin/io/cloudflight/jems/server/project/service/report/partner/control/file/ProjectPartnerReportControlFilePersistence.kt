package io.cloudflight.jems.server.project.service.report.partner.control.file

import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.partner.control.file.PartnerReportControlFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectPartnerReportControlFilePersistence {
    fun saveReportControlFile(reportId: Long, file: JemsFileCreate)

    fun listReportFiles(reportId: Long, pageable: Pageable): Page<PartnerReportControlFile>
}
