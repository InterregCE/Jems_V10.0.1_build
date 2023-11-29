package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl

import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFund
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionEcPayment
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionProjectReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
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

        val reportsByPartner = partnerReportPersistence.getAvailableReports(partnersById.keys)
            .groupBy { it.partnerId }

        return reportsByPartner.map { (partnerId, partnerReports) -> CorrectionAvailablePartner(
            partnerId = partnerId,
            partnerNumber = partnersById[partnerId]!!.sortNumber!!,
            partnerAbbreviation = partnersById[partnerId]!!.abbreviation,
            partnerRole = partnersById[partnerId]!!.role,
            partnerDisabled = !partnersById[partnerId]!!.active,
            availableReports = partnerReports.groupBy { it.id }
                .map { (reportId, allFundsPerReport) ->
                    val report = allFundsPerReport.first()
                    CorrectionAvailablePartnerReport(
                        id = reportId,
                        reportNumber = report.reportNumber,
                        projectReport = if (report.projectReportId == null) null else CorrectionProjectReport(
                            id = report.projectReportId,
                            number = report.projectReportNumber!!,
                        ),
                        availableFunds = allFundsPerReport.map {
                            CorrectionAvailableFund(
                                fund = it.availableFund,
                                ecPayment = if (it.ecPaymentId == null) null else CorrectionEcPayment(
                                    id = it.ecPaymentId,
                                    status = it.ecPaymentStatus!!,
                                    accountingYear = it.ecPaymentAccountingYear!!,
                                ),
                            )
                        },
                    )
                }.sortedBy { it.reportNumber },
            availableFtls = emptyList(),
        ) }.sortedBy { it.partnerNumber }
    }

}
