package io.cloudflight.jems.server.project.service.report.partner.file.deleteProjectPartnerReportFile

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType.PartnerReport
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectPartnerReportFile(
    private val partnerPersistence: PartnerPersistence,
    private val filePersistence: JemsFilePersistence
) : DeleteProjectPartnerReportFileInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(DeleteProjectPartnerReportFileException::class)
    override fun delete(partnerId: Long, reportId: Long, fileId: Long) {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        // to make sure fileId corresponds to correct report, we need to verify it through location path
        val reportPrefix = PartnerReport.generatePath(projectId, partnerId, reportId)

        if (!filePersistence.existsFile(partnerId = partnerId, pathPrefix = reportPrefix, fileId = fileId))
            throw FileNotFound()

        filePersistence.deleteFile(partnerId = partnerId, fileId = fileId)
    }

}
