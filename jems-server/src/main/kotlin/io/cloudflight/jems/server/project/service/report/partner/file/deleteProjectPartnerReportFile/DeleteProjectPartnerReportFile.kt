package io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectPartnerReportFile(
    private val reportFilePersistence: ProjectReportFilePersistence,
) : DeleteProjectPartnerReportFileInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(DeleteProjectPartnerReportFileException::class)
    override fun delete(partnerId: Long, fileId: Long) {
        if (!reportFilePersistence.existsFile(partnerId = partnerId, fileId = fileId))
            throw FileNotFound()

        reportFilePersistence.deleteFile(partnerId = partnerId, fileId = fileId)
    }

}
