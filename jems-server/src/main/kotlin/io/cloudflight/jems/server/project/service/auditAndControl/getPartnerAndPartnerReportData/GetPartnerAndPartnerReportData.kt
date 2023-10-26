package io.cloudflight.jems.server.project.service.auditAndControl.getPartnerAndPartnerReportData

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewProjectAuditAndControl
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionEcPayment
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionProjectReport
import io.cloudflight.jems.server.project.service.auditAndControl.getPartnerAndPartnerReportData.GetPartnerAndPartnerReportDataInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getPartnerAndPartnerReportData.GetPartnerAndPartnerReportException
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerAndPartnerReportData(
    private val partnerReportPersistence: ProjectPartnerReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val versionPersistence: ProjectVersionPersistence
): GetPartnerAndPartnerReportDataInteractor {

    @CanViewProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(GetPartnerAndPartnerReportException::class)
    override fun getPartnerAndPartnerReportData(
        projectId: Long,
    ): List<CorrectionAvailablePartner> {
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId)
        val partnersById = partnerPersistence.findTop50ByProjectId(projectId, version).associateBy { it.id }

        val reportsByPartner = partnerReportPersistence.getAvailableReports(
            partnersById.keys,
            setOf(ReportStatus.Certified, ReportStatus.ReOpenCertified)
        ).groupBy { it.partnerId }

        return reportsByPartner.map { (partnerId, partnerReports) -> CorrectionAvailablePartner(
            partnerId = partnerId,
            partnerNumber = partnersById[partnerId]!!.sortNumber!!,
            partnerAbbreviation = partnersById[partnerId]!!.abbreviation,
            partnerRole = partnersById[partnerId]!!.role,
            partnerDisabled = !partnersById[partnerId]!!.active,
            availableReports = partnerReports.map { CorrectionAvailablePartnerReport(
                id = it.id,
                reportNumber = it.reportNumber,
                projectReport = if (it.projectReportId == null) null else CorrectionProjectReport(
                    id = it.projectReportId,
                    number = it.projectReportNumber!!,
                    ecPayment = if (it.ecPaymentId == null) null else CorrectionEcPayment(
                        id = it.ecPaymentId,
                    )
                ),
                availableFunds = it.availableFunds,
            ) }.sortedBy { it.reportNumber }
        ) }.sortedBy { it.partnerNumber }
    }

}
