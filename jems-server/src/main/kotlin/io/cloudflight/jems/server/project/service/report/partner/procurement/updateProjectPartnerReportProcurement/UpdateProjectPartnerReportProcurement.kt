package io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurementChange
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.getStaticValidationResults
import io.cloudflight.jems.server.project.service.report.partner.procurement.validateAllowedCurrenciesIfEur
import io.cloudflight.jems.server.project.service.report.partner.procurement.validateContractNameIsUnique
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartnerReportProcurement(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportProcurementPersistence: ProjectPartnerReportProcurementPersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateProjectPartnerReportProcurementInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportProcurementException::class)
    override fun update(
        partnerId: Long,
        reportId: Long,
        procurementData: ProjectPartnerReportProcurementChange,
    ): ProjectPartnerReportProcurement {
        procurementData.validateInputFields()

        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        if (report.status.isClosed())
            throw ReportAlreadyClosed()

        procurementData.validateAllowedCurrenciesIfEur(report.identification.currency, { InvalidCurrency(it) })

        val previousReportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = report.id)
        procurementData.validateContractNameIsUnique(
            currentProcurementId = procurementData.id,
            existingContractNames = reportProcurementPersistence
                .getProcurementContractNamesForReportIds(reportIds = previousReportIds.plus(reportId)),
            exceptionResolver = { ContractNameIsNotUnique(it) },
        )

        return reportProcurementPersistence.updatePartnerReportProcurement(
            partnerId = partnerId,
            reportId = reportId,
            procurement = procurementData,
        )
    }

    private fun ProjectPartnerReportProcurementChange.validateInputFields() {
        generalValidator.throwIfAnyIsInvalid(
            *getStaticValidationResults(generalValidator).toTypedArray(),
        )
    }

}
