package io.cloudflight.jems.server.project.service.report.partner.procurement.deleteProjectPartnerReportProcurement

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectPartnerReportProcurement(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportProcurementPersistence: ProjectPartnerReportProcurementPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
    private val expenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val partnerPersistence: PartnerPersistence,
    private val jemsFilePersistence: JemsFilePersistence,
) : DeleteProjectPartnerReportProcurementInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(DeleteProjectPartnerReportProcurementException::class)
    override fun delete(partnerId: Long, reportId: Long, procurementId: Long) {
        val report = reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId)
        validateReportOpen(report)
        validateUsedInExpenditureOrCorrection(procurementId)

        deleteProcurementAttachments(partnerId, reportId, procurementId, JemsFileType.ProcurementAttachment)
        deleteProcurementAttachments(partnerId, reportId, procurementId, JemsFileType.ProcurementGdprAttachment)

        reportProcurementPersistence.deletePartnerReportProcurement(
            partnerId = partnerId,
            reportId = reportId,
            procurementId = procurementId,
        )
    }

    private fun validateReportOpen(report: ProjectPartnerReportStatusAndVersion) {
        if (!report.status.isOpenForNumbersChanges())
            throw ReportAlreadyClosed()
    }

    private fun validateUsedInExpenditureOrCorrection(procurementId: Long) {
        val usedInExpenditure = expenditurePersistence.existsByProcurementId(procurementId)
        val usedInCorrection = auditControlCorrectionPersistence.existsByProcurementId(procurementId)

        if (usedInExpenditure || usedInCorrection)
            throw ProcurementIsLinkedToExpenditureOrCorrection()
    }

    private fun deleteProcurementAttachments(partnerId: Long, reportId: Long, procurementId: Long, fileType: JemsFileType) {
        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        val location = fileType.generatePath(projectId, partnerId, reportId, procurementId)

        jemsFilePersistence.deleteFilesByPath(path = location)
    }
}
