package io.cloudflight.jems.server.project.service.report.partner.procurement.createProjectPartnerReportProcurement

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurementChange
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.getStaticValidationResults
import io.cloudflight.jems.server.project.service.report.partner.procurement.validateContractNameIsUnique
import io.cloudflight.jems.server.project.service.report.partner.procurement.validateMaxAmountOfProcurements
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProjectPartnerReportProcurement(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportProcurementPersistence: ProjectPartnerReportProcurementPersistence,
    private val generalValidator: GeneralValidatorService,
) : CreateProjectPartnerReportProcurementInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(CreateProjectPartnerReportProcurementException::class)
    override fun create(
        partnerId: Long,
        reportId: Long,
        procurementData: ProjectPartnerReportProcurementChange,
    ): ProjectPartnerReportProcurement {
        procurementData.validateInputFields()

        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId)
        if (report.status.isClosed())
            throw ReportAlreadyClosed()

        validateMaxAmountOfProcurements(
            amount = reportProcurementPersistence.countProcurementsForPartner(partnerId = partnerId),
            exceptionResolver = { MaxAmountOfProcurementsReachedException(it) },
        )

        val previousReportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = report.id)
        procurementData.validateContractNameIsUnique(
            currentProcurementId = 0L,
            existingContractNames = reportProcurementPersistence
                .getProcurementContractNamesForReportIds(reportIds = previousReportIds.plus(reportId)),
            exceptionResolver = { ContractNameIsNotUnique(it) },
        )

        return reportProcurementPersistence.createPartnerReportProcurement(
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
