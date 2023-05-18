package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCostOld
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCostUpdate
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.anonymizeSensitiveDataIf
import io.cloudflight.jems.server.project.service.report.partner.expenditure.asOld
import io.cloudflight.jems.server.project.service.report.partner.expenditure.reNumberButSkipReIncluded
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

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
        private const val TO_CREATE = 0L
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

        val validationData = ExpenditureValidationData(
            allowedLumpSumsById = reportExpenditurePersistence.getAvailableLumpSums(partnerId, reportId = reportId).associateBy { it.id },
            allowedUnitCostsById = reportExpenditurePersistence.getAvailableUnitCosts(partnerId, reportId = reportId).associateBy { it.id },
            allowedProcurementIds = getAvailableProcurements(partnerId, reportId = report.id).mapTo(HashSet()) { it.first },
            allowedInvestmentIds = reportExpenditurePersistence.getAvailableInvestments(partnerId, reportId = reportId)
                .mapTo(HashSet()) { it.id },
            defaultCurrency = report.identification.currency,
        )
        val newValues = expenditureCosts.map { it.toValidItem(validationData) }.groupBy { it.id }
        val oldValues = reportExpenditurePersistence.getPartnerReportExpenditureCosts(partnerId, reportId = reportId)
            .map { it.asOld() }.associateBy { it.id }

        val canEditSensitivenessFlag = sensitiveDataAuthorization.isCurrentUserCollaboratorWithSensitiveFor(partnerId)
        val canEditPartnerSensitiveData = canEditSensitivenessFlag || hasGlobalMonitorEdit()

        // old updated with new values and at the end add new ones to be created
        val toUpdate = oldValues.updateWith(
            new = newValues.minus(TO_CREATE).mapValues { it.value.first() },
            canEditPartnerSensitiveData = canEditPartnerSensitiveData,
            canEditSensitivenessFlag = canEditSensitivenessFlag,
            status = report.status,
        ).plus(newValues.toCreate())
            .reNumberButSkipReIncluded()

        return reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
            partnerId = partnerId,
            reportId = report.id,
            expenditureCosts = toUpdate,
        ).also { updatedExpenditureCosts ->
            updatedExpenditureCosts.anonymizeSensitiveDataIf(canNotWorkWithSensitive = !canEditPartnerSensitiveData)
        }
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
            *expenditureCosts.mapIndexed { index, it ->
                generalValidator.numberBetween(it.declaredAmount, MIN_NUMBER, MAX_NUMBER, "declaredAmount[$index]")
            }.toTypedArray(),
            generalValidator.onlyValidCurrencies(expenditureCosts.mapTo(HashSet()) { it.currencyCode }, "currencyCode"),
        )
    }

    private fun getAvailableProcurements(partnerId: Long, reportId: Long) =
        reportProcurementPersistence.getProcurementContractNamesForReportIds(
            reportIds = reportPersistence.getReportIdsBefore(partnerId = partnerId, beforeReportId = reportId).plus(reportId),
        )

    private fun hasGlobalMonitorEdit(): Boolean {
        return securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit) ?: false
    }

    private fun ProjectPartnerReportExpenditureCost.toValidItem(validationData: ExpenditureValidationData): ProjectPartnerReportExpenditureCostUpdate {
        val lumpSumsById = validationData.allowedLumpSumsById
        val unitCostsById = validationData.allowedUnitCostsById
        val investmentIds = validationData.allowedInvestmentIds
        val procurementIds = validationData.allowedProcurementIds

        val lumpSum = if (lumpSumId in lumpSumsById.keys) lumpSumsById[lumpSumId] else null
        val unitCost = if (unitCostId in unitCostsById.keys) unitCostsById[unitCostId] else null

        if (lumpSum != null && unitCost != null)
            throw LumpSumCannotBeSelectedTogetherWithUnitCost()

        val category = when {
            lumpSum != null -> ReportBudgetCategory.Multiple
            unitCost != null -> unitCost.category
            else -> costCategory
        }

        val investmentId = if (category.investmentAllowed() && investmentId in investmentIds) investmentId else null
        val procurementId = if (category.procurementAllowed() && contractId in procurementIds) contractId else null

        val currency = currencyCode.also {
            if (validationData.defaultCurrency == "EUR" && it != validationData.defaultCurrency)
                throw PartnerWithDefaultEurCannotSelectOtherCurrency(it)
        }

        val numberOfUnits = when {
            lumpSum != null -> BigDecimal.ONE
            unitCost != null -> numberOfUnits
            else -> BigDecimal.ZERO
        }
        val pricePerUnit = when {
            lumpSum != null -> lumpSum.cost
            unitCost != null -> if (currency == "EUR") unitCost.costPerUnit else unitCost.costPerUnitForeignCurrency!!
            else -> BigDecimal.ZERO
        }
        val declaredAmount = when {
            lumpSum != null -> lumpSum.cost
            unitCost != null -> numberOfUnits.multiply(pricePerUnit).setScale(2, RoundingMode.DOWN)
            else -> declaredAmount
        }

        return ProjectPartnerReportExpenditureCostUpdate(
            id = id ?: 0L,
            lumpSumId = lumpSum?.id,
            unitCostId = unitCost?.id,
            gdpr = gdpr,
            category = category,
            investmentId = investmentId,
            procurementId = procurementId,
            internalReferenceNumber = internalReferenceNumber,
            invoiceNumber = if (category.invoiceNumberAllowed()) invoiceNumber else null,
            invoiceDate = invoiceDate,
            dateOfPayment = dateOfPayment,
            comment = comment,
            description = description,
            totalValueInvoice = totalValueInvoice,
            vat = if (category.vatAllowed()) vat else null,
            numberOfUnits = numberOfUnits,
            pricePerUnit = pricePerUnit,
            declaredAmount = declaredAmount,
            currencyCode = currency,
        )
    }

    private fun Map<Long, List<ProjectPartnerReportExpenditureCostUpdate>>.toCreate() = getOrDefault(TO_CREATE, emptyList())
        .map { it.toNew() }

    private fun Map<Long, ProjectPartnerReportExpenditureCostOld>.updateWith(
        new: Map<Long, ProjectPartnerReportExpenditureCostUpdate>,
        canEditPartnerSensitiveData: Boolean,
        canEditSensitivenessFlag: Boolean,
        status: ReportStatus,
    ): List<ProjectPartnerReportExpenditureCost> {
        val deletedItems = keys.minus(new.keys)
        if (deletedItems.isNotEmpty() && status.hasBeenClosed())
            throw DeletionNotAllowedAnymore()

        val deletedGdprItems = filter { it.key in deletedItems }.any { it.value.gdpr }
        if (deletedGdprItems && !canEditSensitivenessFlag)
            throw ExpenditureSensitiveDataRemoved()

        return this.minus(deletedItems).map { (id, old) ->
            val newExp = new[id]!!
            return@map if (status in setOf(ReportStatus.ReOpenSubmittedLimited, ReportStatus.ReOpenInControlLimited))
                old.changeOnly(
                    newProcurementId = newExp.procurementId,
                    newDescription = newExp.description,
                    newComment = newExp.comment,
                    canEditPartnerSensitiveData,
                )
            else
                old.changeNumbersWith(new = newExp, canEditPartnerSensitiveData, canEditSensitivenessFlag, status)
        }
    }

    private fun ProjectPartnerReportExpenditureCostOld.changeNumbersWith(
        new: ProjectPartnerReportExpenditureCostUpdate,
        canEditPartnerSensitiveData: Boolean,
        canEditSensitivenessFlag: Boolean,
        status: ReportStatus,
    ): ProjectPartnerReportExpenditureCost {
        // only initially open (not reopened) and not-reIncluded items
        val isCurrencyChangeable = status.isOpenInitially() && this.isNotReIncluded()

        return if (gdpr && !canEditPartnerSensitiveData) toNewWithoutChange() else ProjectPartnerReportExpenditureCost(
            id = id,
            number = number,
            lumpSumId = new.lumpSumId,
            unitCostId = new.unitCostId,
            costCategory = new.category,
            gdpr = if (canEditSensitivenessFlag) new.gdpr else gdpr,
            investmentId = new.investmentId,
            contractId = new.procurementId,
            internalReferenceNumber = new.internalReferenceNumber,
            invoiceNumber = new.invoiceNumber,
            invoiceDate = new.invoiceDate,
            dateOfPayment = new.dateOfPayment,
            description = new.description,
            comment = new.comment,
            totalValueInvoice = new.totalValueInvoice,
            vat = new.vat,
            numberOfUnits = new.numberOfUnits /* numbers are changeable in this case */,
            pricePerUnit = new.pricePerUnit /* numbers are changeable in this case */,
            declaredAmount = new.declaredAmount /* numbers are changeable in this case */,
            currencyCode = if (isCurrencyChangeable) new.currencyCode else currencyCode,
            currencyConversionRate = if (isCurrencyChangeable) null else currencyConversionRate,
            declaredAmountAfterSubmission = if (isCurrencyChangeable) null else declaredAmountAfterSubmission,
            // attachment not important - handled in different use-case
            attachment = null,
            // parking not touched here, but we need to track if reIncluded, to protect currency and renumbering
            parkingMetadata = parkingMetadata,
        )
    }

    private fun ProjectPartnerReportExpenditureCostOld.changeOnly(
        newProcurementId: Long?,
        newDescription: Set<InputTranslation>,
        newComment: Set<InputTranslation>,
        canEditPartnerSensitiveData: Boolean,
    ) = toNewWithoutChange().apply {
        if (!gdpr || canEditPartnerSensitiveData) {
            contractId = newProcurementId
            description = newDescription
            comment = newComment
        }
    }

    private fun ProjectPartnerReportExpenditureCostOld.toNewWithoutChange() = ProjectPartnerReportExpenditureCost(
        id = id,
        number = number,
        lumpSumId = lumpSumId,
        unitCostId = unitCostId,
        costCategory = category,
        gdpr = gdpr,
        investmentId = investmentId,
        contractId = procurementId,
        internalReferenceNumber = internalReferenceNumber,
        invoiceNumber = invoiceNumber,
        invoiceDate = invoiceDate,
        dateOfPayment = dateOfPayment,
        description = description,
        comment = comment,
        totalValueInvoice = totalValueInvoice,
        vat = vat,
        numberOfUnits = numberOfUnits,
        pricePerUnit = pricePerUnit,
        declaredAmount = declaredAmount,
        currencyCode = currencyCode,
        currencyConversionRate = currencyConversionRate,
        declaredAmountAfterSubmission = declaredAmountAfterSubmission,
        // attachment not important - handled in different use-case
        attachment = null,
        // parking not touched here, but we need to track if reIncluded, to protect currency and renumbering
        parkingMetadata = parkingMetadata,
    )

    private fun ProjectPartnerReportExpenditureCostUpdate.toNew() = ProjectPartnerReportExpenditureCost(
        id = id,
        number = 0,
        lumpSumId = lumpSumId,
        unitCostId = unitCostId,
        costCategory = category,
        gdpr = gdpr,
        investmentId = investmentId,
        contractId = procurementId,
        internalReferenceNumber = internalReferenceNumber,
        invoiceNumber = invoiceNumber,
        invoiceDate = invoiceDate,
        dateOfPayment = dateOfPayment,
        description = description,
        comment = comment,
        totalValueInvoice = totalValueInvoice,
        vat = vat,
        numberOfUnits = numberOfUnits,
        pricePerUnit = pricePerUnit,
        declaredAmount = declaredAmount,
        currencyCode = currencyCode,
        currencyConversionRate = null,
        declaredAmountAfterSubmission = null,
        // no attachment never when created
        attachment = null,
        // reIncluding is never done here in update use-case
        parkingMetadata = null,
    )

}
