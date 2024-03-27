package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl

import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFtls
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFtlsTmp
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFund
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableReportTmp
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionEcPayment
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionProjectReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown.isZero
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPartnerAndPartnerReportDataService(
    private val partnerReportPersistence: ProjectPartnerReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val versionPersistence: ProjectVersionPersistence,
    private val paymentPersistence: PaymentPersistence,
) {

    @Transactional(readOnly = true)
    fun getPartnerAndPartnerReportData(
        projectId: Long,
    ): List<CorrectionAvailablePartner> {
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId)
        val partnersById = partnerPersistence.findTop50ByProjectId(projectId, version).associateBy { it.id }

        val reportsByPartner = partnerReportPersistence.getAvailableReports(partnersById.keys)
            .groupBy { it.partnerId }
        val ftlsByPartner = paymentPersistence.getAvailableFtlsPayments(partnersById.keys)
            .groupBy { it.partnerId }
        val partnerIds = reportsByPartner.keys + ftlsByPartner.keys

        return partnerIds.map { partnerId ->
            val partnerReports = reportsByPartner[partnerId] ?: emptyList()
            val partnerFtls = ftlsByPartner[partnerId] ?: emptyList()

            CorrectionAvailablePartner(
                partnerId = partnerId,
                partnerNumber = partnersById[partnerId]!!.sortNumber!!,
                partnerAbbreviation = partnersById[partnerId]!!.abbreviation,
                partnerRole = partnersById[partnerId]!!.role,
                partnerDisabled = !partnersById[partnerId]!!.active,
                availableReports = partnerReports.toAvailableReports(),
                availableFtls = partnerFtls.toAvailableFtls(),
            )
        }.sortedBy { it.partnerNumber }
    }

    private fun List<CorrectionAvailableReportTmp>.toAvailableReports() = groupBy { it.id }
        .map { (reportId, allFundsPerReport) ->
            val report = allFundsPerReport.first()
            CorrectionAvailablePartnerReport(
                id = reportId,
                reportNumber = report.reportNumber,
                projectReport = report.projectReportId?.let { projectReportId ->
                    CorrectionProjectReport(
                        id = projectReportId,
                        number = report.projectReportNumber!!,
                    )
                },
                availableFunds = allFundsPerReport.map {
                    CorrectionAvailableFund(
                        fund = it.availableFund,
                        ecPayment = it.ecPaymentId?.let { ecPaymentId ->
                            CorrectionEcPayment(
                                id = ecPaymentId,
                                status = it.ecPaymentStatus!!,
                                accountingYear = it.ecPaymentAccountingYear!!,
                            )
                        },
                        disabled = it.fundShareTotal.isZero(),
                    )
                },
            )
        }.sortedBy { it.reportNumber }

    private fun List<CorrectionAvailableFtlsTmp>.toAvailableFtls() = groupBy { it.programmeLumpSumId to it.orderNr }
        .map { (ftlsId, allFundsPerFtls) ->
            val ftls = allFundsPerFtls.first()
            CorrectionAvailableFtls(
                programmeLumpSumId = ftlsId.first,
                orderNr = ftlsId.second,
                name = ftls.name,
                availableFunds = allFundsPerFtls.map {
                    CorrectionAvailableFund(
                        fund = it.availableFund,
                        ecPayment = it.ecPaymentId?.let { ecPaymentId ->
                            CorrectionEcPayment(
                                id = ecPaymentId,
                                status = it.ecPaymentStatus!!,
                                accountingYear = it.ecPaymentAccountingYear!!,
                            )
                        },
                        disabled = false,
                    )
                }
            )

        }

}
