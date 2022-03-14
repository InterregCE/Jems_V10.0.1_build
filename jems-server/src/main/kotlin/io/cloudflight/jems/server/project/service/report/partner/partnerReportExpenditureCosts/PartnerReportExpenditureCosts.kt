package io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureCosts

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.repository.report.expenditureCosts.toExpenditureModel
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.workPlan.updateProjectPartnerWorkPlan.ReportAlreadyClosed
import org.springframework.stereotype.Service

@Service
class PartnerReportExpenditureCosts(
    private val reportPersistence: ProjectReportPersistence,
    private val generalValidator: GeneralValidatorService
) : PartnerReportExpenditureCostsInteractor {

    @CanEditPartnerReport
    @ExceptionWrapper(PartnerReportExpenditureCostsException::class)
    override fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<PartnerReportExpenditureCost>
    ): List<PartnerReportExpenditureCost> {
        validateReportNotClosed(
            status = reportPersistence.getPartnerReportStatusAndVersion(
                partnerId,
                reportId = reportId
            ).status
        )
        validateInputs(expenditureCosts = expenditureCosts)
        return reportPersistence.updatePartnerReportExpenditureCosts(reportId, expenditureCosts)
            .expenditureCosts.toExpenditureModel().sortedBy { it.id }
    }

    @CanViewPartnerReport
    @ExceptionWrapper(PartnerReportExpenditureCostsException::class)
    override fun getPartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long
    ): List<PartnerReportExpenditureCost> =
        reportPersistence.getPartnerReportExpenditureCosts(reportId).toExpenditureModel()


    private fun validateReportNotClosed(status: ReportStatus) {
        if (status.isClosed())
            throw ReportAlreadyClosed()
    }

    private fun validateInputs(expenditureCosts: List<PartnerReportExpenditureCost>) {
        expenditureCosts.forEach {
            generalValidator.throwIfAnyIsInvalid(
                generalValidator.maxLength(it.description, 255, "description"),
            )
            generalValidator.throwIfAnyIsInvalid(
                generalValidator.maxLength(it.comment, 255, "comment"),
            )
            generalValidator.throwIfAnyIsInvalid(
                generalValidator.maxLength(it.internalReferenceNumber, 30, "internalReferenceNumber"),
            )
            generalValidator.throwIfAnyIsInvalid(
                generalValidator.maxLength(it.invoiceNumber, 30, "invoiceNumber"),
            )
        }
    }
}
