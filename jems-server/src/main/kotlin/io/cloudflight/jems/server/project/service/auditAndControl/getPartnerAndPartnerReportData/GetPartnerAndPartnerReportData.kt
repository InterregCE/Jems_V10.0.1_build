package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.getPartnerAndPartnerReportData

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.project.authorization.CanViewProjectAuditAndControl
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePayment
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailableReportTmp
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionProjectReport
import io.cloudflight.jems.server.project.service.auditAndControl.getPartnerAndPartnerReportData.GetPartnerAndPartnerReportDataInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getPartnerAndPartnerReportData.GetPartnerAndPartnerReportException
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerAndPartnerReportData(
    private val partnerReportPersistence: ProjectPartnerReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val versionPersistence: ProjectVersionPersistence
) : GetPartnerAndPartnerReportDataInteractor {

    @CanViewProjectAuditAndControl
    @Transactional
    @ExceptionWrapper(GetPartnerAndPartnerReportException::class)
    override fun getPartnerAndPartnerReportData(
        projectId: Long,
    ): List<CorrectionAvailablePartner> {
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId)
        val partnersById = partnerPersistence.findTop50ByProjectId(projectId, version).associateBy { it.id }

        return partnerReportPersistence.getAvailableReports(
            partnersById.keys,
            setOf(ReportStatus.Certified, ReportStatus.ReOpenCertified)
        ).toCorrectionPartner(partnersById)
    }

    private fun List<CorrectionAvailableReportTmp>.toCorrectionPartner(partnersById: Map<Long, ProjectPartnerDetail>) = groupBy { partnersById[it.partnerId]!! }
        .map { (partner, partnerReports) ->
            CorrectionAvailablePartner(
                partnerId = partner.id,
                partnerNumber = partner.sortNumber!!,
                partnerAbbreviation = partner.abbreviation,
                partnerRole = partner.role,
                partnerDisabled = !partner.active,
                availableReports = partnerReports.toCorrectionPartnerReport()
            )
        }.sortedBy { it.partnerNumber }

    private fun List<CorrectionAvailableReportTmp>.toCorrectionPartnerReport() = groupBy {
        CorrectionAvailablePartnerReport(
            id = it.id,
            reportNumber = it.reportNumber,
            projectReport = it.toCorrectionProjectReport(),
            availableReportFunds = it.availableReportFunds,
            availablePayments = emptyList()
        )
    }.map { (correctionPartnerReport, payments) ->
        correctionPartnerReport.copy(availablePayments = payments.toCorrectionPayments())
    }.sortedBy { it.reportNumber }

    private fun CorrectionAvailableReportTmp.toCorrectionProjectReport() = projectReportId?.let {
        CorrectionProjectReport(
            id = it,
            number = projectReportNumber!!,
        )
    }

    private fun List<CorrectionAvailableReportTmp>.toCorrectionPayments() = filter { it.paymentFund != null }
        .map {
            CorrectionAvailablePayment(
                fund = it.paymentFund!!,
                ecPayment = it.toCorrectionEcPayment()
            )
        }

    private fun CorrectionAvailableReportTmp.toCorrectionEcPayment() = ecPaymentId?.let {
        PaymentApplicationToEc(
            id = ecPaymentId,
            status = ecPaymentStatus!!,
            programmeFund = paymentFund!!,
            accountingYear = ecPaymentAccountingYear!!
        )
    }

}
