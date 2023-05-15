package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.clearConversions
import io.cloudflight.jems.server.project.service.report.partner.expenditure.clearParking
import io.cloudflight.jems.server.project.service.report.partner.expenditure.reNumber
import io.cloudflight.jems.server.project.service.report.partner.expenditure.anonymizeSensitiveDataIf
import io.cloudflight.jems.server.project.service.report.partner.expenditure.filterInvalidCurrencies
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillInLumpSum
import io.cloudflight.jems.server.project.service.report.partner.expenditure.fillInUnitCost
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateProjectPartnerReportExpenditure(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence,
    private val reportProcurementPersistence: ProjectPartnerReportProcurementPersistence,
    private val generalValidator: GeneralValidatorService,
    private val sensitiveDataAuthorization: SensitiveDataAuthorizationService,
    private val securityService: SecurityService,

    ) : UpdateProjectPartnerReportExpenditureInteractor {

    companion object {
        private val MAX_NUMBER = BigDecimal.valueOf(999_999_999_99, 2)
        private val MIN_NUMBER = BigDecimal.ZERO

        private const val MAX_AMOUNT = 150
    }

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UpdateProjectPartnerReportExpenditureException::class)
    override fun updatePartnerReportExpenditureCosts(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
    ): List<ProjectPartnerReportExpenditureCost> {
        validateInputs(expenditureCosts = expenditureCosts)

        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)
        validateReportNotClosed(status = report.status)
        validateCostCategories(expenditureCosts)
        validateCurrencies(expenditureCosts, defaultCurrency = report.identification.currency)

        validateLinkedProcurements(
            expenditureCosts = expenditureCosts,
            allowedProcurementIds = getAvailableProcurements(partnerId, reportId = report.id).mapTo(HashSet()) { it.first },
            allowedInvestmentIds = reportExpenditurePersistence.getAvailableInvestments(partnerId, reportId = reportId)
                .mapTo(HashSet()) { it.id },
        )

        validateCostOptions(
            expenditureCosts = expenditureCosts,
            allowedLumpSums = reportExpenditurePersistence.getAvailableLumpSums(partnerId, reportId = reportId),
            allowedUnitCosts = reportExpenditurePersistence.getAvailableUnitCosts(partnerId, reportId = reportId),
        )
        val oldExpenditures = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, reportId = reportId)
        val reIncludedExpenditures = oldExpenditures
            .filter { it.parkingMetadata != null }.associateBy { it.id!! }

        val canEditSensitivenessFlag = sensitiveDataAuthorization.isCurrentUserCollaboratorWithSensitiveFor(partnerId)
        val canEditPartnerSensitiveData = canEditSensitivenessFlag || hasGlobalMonitorEdit()
        var newExpenditures = expenditureCosts

         val sensitiveFlagChanged = newExpenditures.sensitiveFlagChanged(oldExpenditures)
         if(sensitiveFlagChanged && !canEditSensitivenessFlag) {
            throw ExpenditureSensitiveDataCannotBeUpdated()
         }

        if (!canEditPartnerSensitiveData) {
            newExpenditures = newExpenditures.skipSensitiveForUpdate(oldExpenditures)
        }


        return reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
            partnerId = partnerId,
            reportId = report.id,
            expenditureCosts = newExpenditures
                .clearConversions(exceptReIncluded = reIncludedExpenditures)
                .clearParking()
                .reNumber(ignoreIds = reIncludedExpenditures.keys),
        ).also { updatedExpenditureCosts ->
            updatedExpenditureCosts.anonymizeSensitiveDataIf(canNotWorkWithSensitive = !canEditPartnerSensitiveData)
        }
    }

    private fun List<ProjectPartnerReportExpenditureCost>.sensitiveFlagChanged(
        old: List<ProjectPartnerReportExpenditureCost>,
    ): Boolean {
        val oldSensitiveIds = old.filter { it.gdpr }.mapTo(HashSet()) { it.id!! } // will not have 0
        val newSensitiveIds = this.filter { it.gdpr }.mapTo(HashSet()) { it.id ?: 0L }
        return oldSensitiveIds != newSensitiveIds
    }

    private fun validateReportNotClosed(status: ReportStatus) {
        if (!status.isOpenInitially())
            throw ReportAlreadyClosed()
    }

    private fun validateCostCategories(expenditureCosts: List<ProjectPartnerReportExpenditureCost>) {
        expenditureCosts.forEach {
            if (it.costCategory == ReportBudgetCategory.StaffCosts || it.costCategory == ReportBudgetCategory.TravelAndAccommodationCosts)
                it.investmentId = null

            if (it.costCategory == ReportBudgetCategory.StaffCosts) {
                it.contractId = null
                it.vat = null
                it.invoiceNumber = null
            }
        }
    }

    private fun validateCurrencies(expenditureCosts: List<ProjectPartnerReportExpenditureCost>, defaultCurrency: String?) {
        val invalidCurrencies = expenditureCosts.filterInvalidCurrencies(defaultCurrency) { it.currencyCode }
        if (invalidCurrencies.isNotEmpty())
            throw PartnerWithDefaultEurCannotSelectOtherCurrency(invalidCurrencies)
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
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.numberBetween(it.declaredAmount, MIN_NUMBER, MAX_NUMBER, "declaredAmount[$index]")
            }.toTypedArray(),
            generalValidator.onlyValidCurrencies(expenditureCosts.mapTo(HashSet()) { it.currencyCode }, "currencyCode"),
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

    private fun validateCostOptions(
        expenditureCosts: List<ProjectPartnerReportExpenditureCost>,
        allowedLumpSums: List<ProjectPartnerReportLumpSum>,
        allowedUnitCosts: List<ProjectPartnerReportUnitCost>,
    ) {
        val lumpSumsById = allowedLumpSums.associateBy { it.id }
        val unitCostsById = allowedUnitCosts.associateBy { it.id }
        expenditureCosts.forEach {
            if (it.lumpSumId in lumpSumsById.keys) {
                it.fillInLumpSum(lumpSum = lumpSumsById[it.lumpSumId]!!)
            } else if (it.unitCostId in unitCostsById.keys) {
                it.fillInUnitCost(unitCost = unitCostsById[it.unitCostId]!!, currencyCodeValue = it.currencyCode)
            } else {
                it.numberOfUnits = BigDecimal.ZERO
                it.pricePerUnit = BigDecimal.ZERO
            }
        }
    }

    private fun getAvailableProcurements(partnerId: Long, reportId: Long) =
        reportProcurementPersistence.getProcurementContractNamesForReportIds(
            reportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = reportId).plus(reportId),
        )

    private fun List<ProjectPartnerReportExpenditureCost>
            .skipSensitiveForUpdate(oldExpenditures: List<ProjectPartnerReportExpenditureCost>): List<ProjectPartnerReportExpenditureCost> {
        val oldSensitiveExpendituresMap = oldExpenditures.associateBy { it.id }
        return this.toMutableList().apply {
            this.replaceAll { expenditureCost ->
                expenditureCost.takeIf { it.gdpr }?.let { oldSensitiveExpendituresMap[it.id] } ?: expenditureCost
            }
        }
    }

    private fun hasGlobalMonitorEdit(): Boolean {
        return securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit) ?: false
    }
}
