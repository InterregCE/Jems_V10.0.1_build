package io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.fillThisReportFlag
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectPartnerReportProcurement(
    private val reportPersistence: ProjectReportPersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
    private val generalValidator: GeneralValidatorService,
) : UpdateProjectPartnerReportProcurementInteractor {

    companion object {
        private val MAX_NUMBER = BigDecimal.valueOf(999_999_999_99, 2)
        private val MIN_NUMBER = BigDecimal.ZERO

        private const val MAX_AMOUNT_OF_PROCUREMENTS = 50L
    }

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportProcurementException::class)
    override fun update(
        partnerId: Long,
        reportId: Long,
        procurementNew: List<ProjectPartnerReportProcurementUpdate>
    ): List<ProjectPartnerReportProcurement> {
        validateInputFields(data = procurementNew)

        val previousReportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = reportId)
        val previousProcurementsAmount = reportProcurementPersistence.countProcurementsForReportIds(previousReportIds)

        validateMaxAmountOfProcurements(dataNew = procurementNew, oldAmount = previousProcurementsAmount)
        validateContractIdsAreUnique(
            data = procurementNew,
            existingContractIdsFromOtherReports = reportProcurementPersistence
                .getProcurementContractIdsForReportIds(reportIds = previousReportIds),
        )

        val availableIdsToBeUsed = reportProcurementPersistence
            .getProcurementIdsForReport(partnerId = partnerId, reportId = reportId)
            .plus(0 /* to be created elements */)

        reportProcurementPersistence.updatePartnerReportProcurement(
            partnerId = partnerId,
            reportId = reportId,
            procurementNew = procurementNew.filter { availableIdsToBeUsed.contains(it.id) },
        )

        return reportProcurementPersistence.getProcurementsForReportIds(
            reportIds = previousReportIds.plus(reportId)
        ).fillThisReportFlag(currentReportId = reportId)
    }

    private fun validateInputFields(data: List<ProjectPartnerReportProcurementUpdate>) {
        generalValidator.throwIfAnyIsInvalid(
            *data.mapIndexed { index, it ->
                generalValidator.maxLength(it.contractId, 30, "contractId[$index]")
            }.toTypedArray(),
            *data.mapIndexed { index, it ->
                generalValidator.maxLength(it.contractType, 30, "contractType[$index]")
            }.toTypedArray(),
            *data.mapIndexed { index, it ->
                generalValidator.maxLength(it.supplierName, 30, "supplierName[$index]")
            }.toTypedArray(),
            *data.mapIndexed { index, it ->
                generalValidator.maxLength(it.comment, 2000, "comment[$index]")
            }.toTypedArray(),
            *data.mapIndexed { index, it ->
                generalValidator.numberBetween(it.contractAmount, MIN_NUMBER, MAX_NUMBER, "contractAmount[$index]")
            }.toTypedArray(),
        )
    }

    private fun validateMaxAmountOfProcurements(dataNew: List<ProjectPartnerReportProcurementUpdate>, oldAmount: Long) {
        if (dataNew.size + oldAmount > MAX_AMOUNT_OF_PROCUREMENTS)
            throw MaxAmountOfProcurementsReachedException(MAX_AMOUNT_OF_PROCUREMENTS.toInt())
    }

    private fun validateContractIdsAreUnique(
        data: List<ProjectPartnerReportProcurementUpdate>,
        existingContractIdsFromOtherReports: Set<String>,
    ) {
        val contractIds = data.groupBy { it.contractId }.mapValues { it.value.size }
        val contractIdsNotUnique = contractIds.filter { it.value > 1 }.keys

        val conflictingIdsWithOtherReports = existingContractIdsFromOtherReports intersect contractIds.keys
        val allConflicts = contractIdsNotUnique union conflictingIdsWithOtherReports

        if (allConflicts.isNotEmpty())
            throw ContractIdsAreNotUnique(notUniqueIds = allConflicts)
    }

}
