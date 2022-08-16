package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.updateProjectPartnerReportProcurementSubcontract

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChange
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.MAX_AMOUNT_OF_SUBCONTRACT
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.ProjectReportProcurementSubcontractPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.fillThisReportFlag
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectPartnerReportProcurementSubcontract(
    private val reportPersistence: ProjectReportPersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
    private val reportProcurementSubcontractPersistence: ProjectReportProcurementSubcontractPersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateProjectPartnerReportProcurementSubcontractInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportProcurementSubcontractException::class)
    override fun update(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        subcontracts: List<ProjectPartnerReportProcurementSubcontractChange>,
    ): List<ProjectPartnerReportProcurementSubcontract> {
        subcontracts.validateInput()

        // we need to fetch those 2 because of security
        val procurement = reportProcurementPersistence.getById(partnerId, procurementId)
        if (!reportPersistence.exists(partnerId, reportId = reportId))
            throw ReportNotFound(reportId)

        val amountFromPreviousReports = reportProcurementSubcontractPersistence
            .countSubcontractorsCreatedBefore(procurement.id, reportId)

        validateMaxAmountOfSubcontractors(subcontracts, oldAmount = amountFromPreviousReports)

        return reportProcurementSubcontractPersistence.updateSubcontract(
            partnerId = partnerId,
            reportId = reportId,
            procurementId = procurementId,
            data = subcontracts,
        ).fillThisReportFlag(currentReportId = reportId)
    }

    private fun validateMaxAmountOfSubcontractors(dataNew: List<ProjectPartnerReportProcurementSubcontractChange>, oldAmount: Long) {
        if (dataNew.size + oldAmount > MAX_AMOUNT_OF_SUBCONTRACT)
            throw MaxAmountOfSubcontractorsReachedException(MAX_AMOUNT_OF_SUBCONTRACT)
    }

    private fun List<ProjectPartnerReportProcurementSubcontractChange>.validateInput() {
        generalValidator.throwIfAnyIsInvalid(
            *mapIndexed { index, it ->
                generalValidator.maxLength(it.contractName, 50, "contractName[$index]")
            }.toTypedArray(),
            *mapIndexed { index, it ->
                generalValidator.maxLength(it.referenceNumber, 30, "referenceNumber[$index]")
            }.toTypedArray(),
            *mapIndexed { index, it ->
                generalValidator.numberBetween(it.contractAmount, BigDecimal.ZERO, BigDecimal.valueOf(999_999_999_99, 2), "contractAmount[$index]")
            }.toTypedArray(),
            generalValidator.onlyValidCurrencies(mapTo(HashSet()) { it.currencyCode }, "currencyCode"),
            *mapIndexed { index, it ->
                generalValidator.maxLength(it.supplierName, 50, "supplierName[$index]")
            }.toTypedArray(),
            *mapIndexed { index, it ->
                generalValidator.maxLength(it.vatNumber, 30, "vatNumber[$index]")
            }.toTypedArray(),
            *mapIndexed { index, it ->
                generalValidator.notBlank(it.vatNumber, "vatNumber[$index]")
            }.toTypedArray(),
        )
    }

}
