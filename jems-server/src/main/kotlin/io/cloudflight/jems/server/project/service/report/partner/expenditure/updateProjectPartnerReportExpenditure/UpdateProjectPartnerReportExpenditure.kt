package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import org.springframework.stereotype.Service

@Service
class UpdateProjectPartnerReportExpenditure(
    private val reportPersistence: ProjectReportPersistence,
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
    private val workPackagePersistence: WorkPackagePersistence,
    private val partnerPersistence: PartnerPersistence,
    private val generalValidator: GeneralValidatorService
) : UpdateProjectPartnerReportExpenditureInteractor {

    companion object {
        private const val MAX_AMOUNT = 150
    }

    @CanEditPartnerReport
    @ExceptionWrapper(UpdateProjectPartnerReportExpenditureException::class)
    override fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>
    ): List<ProjectPartnerReportExpenditureCost> {
        validateInputs(expenditureCosts = expenditureCosts)

        val statusAndVersion = reportPersistence.getPartnerReportStatusAndVersion(partnerId, reportId = reportId)
        validateReportNotClosed(
            status = statusAndVersion.status
        )
        validateLinkedProcurements(
            expenditureCosts = expenditureCosts,
            allowedProcurementIds = getAvailableProcurements(partnerId, reportId = reportId).mapTo(HashSet()) { it.id },
            allowedInvestmentIds = workPackagePersistence.getProjectInvestmentSummaries(
                projectId = partnerPersistence.getProjectIdForPartnerId(partnerId, statusAndVersion.version),
                version = statusAndVersion.version,
            ).mapTo(HashSet()) { it.id },
        )

        return reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
            partnerId = partnerId,
            reportId = reportId,
            expenditureCosts = expenditureCosts,
        )
    }

    private fun validateReportNotClosed(status: ReportStatus) {
        if (status.isClosed())
            throw ReportAlreadyClosed()
    }

    private fun validateInputs(expenditureCosts: List<ProjectPartnerReportExpenditureCost>) {
        if (expenditureCosts.size > MAX_AMOUNT)
            throw MaxAmountOfExpendituresReached(maxAmount = MAX_AMOUNT)

        generalValidator.throwIfAnyIsInvalid(
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.maxLength(it.description, 255, "description[$index]")
            }.toTypedArray(),
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.maxLength(it.comment, 255, "comment[$index]")
            }.toTypedArray(),
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.maxLength(it.internalReferenceNumber, 30, "internalReferenceNumber[$index]")
            }.toTypedArray(),
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.maxLength(it.invoiceNumber, 30, "invoiceNumber[$index]")
            }.toTypedArray(),
        )
    }

    private fun validateLinkedProcurements(
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
        allowedProcurementIds: Set<Long>,
        allowedInvestmentIds: Set<Long>,
    ) {
        expenditureCosts.forEach {
            it.contractId = if (it.contractId in allowedProcurementIds) it.contractId else null
            it.investmentId = if (it.investmentId in allowedInvestmentIds) it.investmentId else null
        }
    }

    private fun getAvailableProcurements(partnerId: Long, reportId: Long) =
        reportProcurementPersistence.getProcurementsForReportIds(
            reportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = reportId).plus(reportId),
        )
}
