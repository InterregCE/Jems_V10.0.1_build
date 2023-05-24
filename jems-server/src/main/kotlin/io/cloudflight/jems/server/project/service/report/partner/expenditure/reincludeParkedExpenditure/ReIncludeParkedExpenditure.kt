package io.cloudflight.jems.server.project.service.report.partner.expenditure.reincludeParkedExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.file.ProjectPartnerReportFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureReIncluded
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayInputStream

@Service
class ReIncludeParkedExpenditure(
    private val reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val reportFilePersistence: ProjectPartnerReportFilePersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val filePersistence: JemsFilePersistence,
    private val partnerPersistence: PartnerPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : ReIncludeParkedExpenditureInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(ReIncludeParkedExpenditureException::class)
    override fun reIncludeParkedExpenditure(partnerId: Long, reportId: Long, expenditureId: Long) {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        if (!report.status.isOpenForNumbersChanges())
            throw ReIncludingForbiddenIfReOpenedReportIsNotLast()

        val attachment = reportExpenditurePersistence.getExpenditureAttachment(partnerId, expenditureId = expenditureId)

        val newExpenditure = reportExpenditurePersistence
            .reIncludeParkedExpenditure(partnerId, reportId = reportId, expenditureId = expenditureId)
        reportParkedExpenditurePersistence.unParkExpenditures(setOf(expenditureId))

        val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
        if (attachment != null) {
            val clonedFile = attachment.toCreateFile(
                projectId = projectId,
                partnerId = partnerId,
                reportId = reportId,
                expenditureId = newExpenditure.id!!,
                content = filePersistence.downloadFile(partnerId = partnerId, fileId = attachment.id)!!.second,
            )
            reportFilePersistence.updatePartnerReportExpenditureAttachment(expenditureId = newExpenditure.id, clonedFile)
        }

        auditPublisher.publishEvent(
            partnerReportExpenditureReIncluded(
                context = this,
                projectId = projectId,
                partnerReport = report,
                expenditure = newExpenditure.parkingMetadata!!,
            )
        )
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
