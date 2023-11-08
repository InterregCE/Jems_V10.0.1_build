package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl

import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePayment
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableReportTmp
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionProjectReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerAndPartnerReportDataService(
    private val partnerReportPersistence: ProjectPartnerReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val versionPersistence: ProjectVersionPersistence
) {

    @Transactional
    fun getPartnerAndPartnerReportData(
        projectId: Long,
    ): List<CorrectionAvailablePartner> {
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId)
        val partnersById = partnerPersistence.findTop50ByProjectId(projectId, version).associateBy { it.id }

        return partnerReportPersistence.getAvailableReports(
            partnersById.keys,
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
