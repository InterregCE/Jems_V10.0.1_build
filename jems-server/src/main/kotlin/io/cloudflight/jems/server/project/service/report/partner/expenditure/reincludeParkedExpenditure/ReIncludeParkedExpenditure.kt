package io.cloudflight.jems.server.project.service.report.partner.expenditure.reincludeParkedExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.file.ProjectPartnerReportFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream

@Service
class ReIncludeParkedExpenditure(
    private val reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val reportFilePersistence: ProjectPartnerReportFilePersistence,
    private val filePersistence: JemsFilePersistence,
    private val partnerPersistence: PartnerPersistence,
) : ReIncludeParkedExpenditureInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(ReIncludeParkedExpenditureException::class)
    override fun reIncludeParkedExpenditure(partnerId: Long, reportId: Long, expenditureId: Long) {
        val attachment = reportExpenditurePersistence.getExpenditureAttachment(partnerId, expenditureId = expenditureId)

        val newExpenditure = reportExpenditurePersistence
            .reIncludeParkedExpenditure(partnerId, reportId = reportId, expenditureId = expenditureId)
        reportParkedExpenditurePersistence.unParkExpenditures(setOf(expenditureId))

        if (attachment != null) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val clonedFile = attachment.toCreateFile(
                projectId = projectId,
                partnerId = partnerId,
                reportId = reportId,
                expenditureId = newExpenditure.id!!,
                content = filePersistence.downloadFile(partnerId = partnerId, fileId = attachment.id)!!.second,
            )
            reportFilePersistence.updatePartnerReportExpenditureAttachment(expenditureId = newExpenditure.id, clonedFile)
        }
    }

    private fun JemsFile.toCreateFile(
        projectId: Long,
        partnerId: Long,
        reportId: Long,
        expenditureId: Long,
        content: ByteArray,
    ) = JemsFileCreate(
        projectId = projectId,
        partnerId = partnerId,
        name = name,
        path = JemsFileType.Expenditure.generatePath(projectId, partnerId, reportId, expenditureId),
        type = type,
        size = size,
        content = ByteArrayInputStream(content),
        userId = author.id,
    )

}
