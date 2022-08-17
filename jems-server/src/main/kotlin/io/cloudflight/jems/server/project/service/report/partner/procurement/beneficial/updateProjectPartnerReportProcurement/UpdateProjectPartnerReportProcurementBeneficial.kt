package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.updateProjectPartnerReportProcurement

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.MAX_AMOUNT_OF_BENEFICIAL
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.ProjectReportProcurementBeneficialPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.fillThisReportFlag
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProjectPartnerReportProcurementBeneficial(
    private val reportPersistence: ProjectReportPersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
    private val reportProcurementBeneficialPersistence: ProjectReportProcurementBeneficialPersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateProjectPartnerReportProcurementBeneficialInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportProcurementBeneficialException::class)
    override fun update(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        owners: List<ProjectPartnerReportProcurementBeneficialChange>,
    ): List<ProjectPartnerReportProcurementBeneficialOwner> {
        owners.validateInput()

        // we need to fetch those 2 because of security
        val procurement = reportProcurementPersistence.getById(partnerId, procurementId)
        if (!reportPersistence.exists(partnerId, reportId = reportId))
            throw ReportNotFound(reportId)

        val amountFromPreviousReports = reportProcurementBeneficialPersistence
            .countBeneficialOwnersCreatedBefore(procurement.id, reportId)

        validateMaxAmountOfBeneficialOwners(owners, oldAmount = amountFromPreviousReports)

        return reportProcurementBeneficialPersistence.updateBeneficialOwners(
            partnerId = partnerId,
            reportId = reportId,
            procurementId = procurementId,
            owners = owners,
        ).fillThisReportFlag(currentReportId = reportId)
    }

    private fun validateMaxAmountOfBeneficialOwners(dataNew: List<ProjectPartnerReportProcurementBeneficialChange>, oldAmount: Long) {
        if (dataNew.size + oldAmount > MAX_AMOUNT_OF_BENEFICIAL)
            throw MaxAmountOfBeneficialReachedException(MAX_AMOUNT_OF_BENEFICIAL)
    }

    private fun List<ProjectPartnerReportProcurementBeneficialChange>.validateInput() {
        generalValidator.throwIfAnyIsInvalid(
            *mapIndexed { index, it ->
                generalValidator.maxLength(it.firstName, 50, "firstName[$index]")
            }.toTypedArray(),
            *mapIndexed { index, it ->
                generalValidator.maxLength(it.lastName, 50, "lastName[$index]")
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
