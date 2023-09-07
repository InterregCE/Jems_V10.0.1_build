package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getDeductionOverview

import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.cloudflight.jems.server.project.repository.report.project.verification.ProjectReportVerificationExpenditurePersistenceProvider
import io.cloudflight.jems.server.project.service.budget.calculator.BudgetCostCategory
import io.cloudflight.jems.server.project.service.budget.model.BudgetCostsCalculationResultFull
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.CertificateVerificationDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.VerificationDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.VerificationDeductionOverviewRow
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.ProjectPartnerReportExpenditureCostCategoryPersistence
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.calculateCertified
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.calculateTotalDeductedPerCostCategory
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.calculateVerified
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.onlyParkedOnes
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.toIdentifiers
import io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.toVerification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class ProjectReportVerificationDeductionOverviewCalculator(
    private val projectReportVerificationExpenditurePersistenceProvider: ProjectReportVerificationExpenditurePersistenceProvider,
    private val typologyOfErrorsPersistence: ProgrammeTypologyErrorsPersistence,
    private val reportExpenditureCostCategoryPersistence: ProjectPartnerReportExpenditureCostCategoryPersistence,
) {

    @Transactional(readOnly = true)
    fun getDeductionOverview(reportId: Long): List<CertificateVerificationDeductionOverview> {

        val verificationExpendituresByCertificate =
            projectReportVerificationExpenditurePersistenceProvider.getProjectReportExpenditureVerification(reportId)
            .groupBy ({ it.toIdentifiers() }, {it.toVerification()})

        val allTypologyErrors = typologyOfErrorsPersistence.getAllTypologyErrors()
        val certificateIds = verificationExpendituresByCertificate.keys.mapTo(HashSet()) { it.partnerReportId }
        val expenditureCostCategoriesByCertificate =
            reportExpenditureCostCategoryPersistence.getCostCategoriesFor(certificateIds)

        return verificationExpendituresByCertificate.map { (certificate, verificationExpenditures) ->

            val certificateCostCategories = expenditureCostCategoriesByCertificate[certificate.partnerReportId]!!
            val options = certificateCostCategories.options
            val totalRequested = certificateCostCategories.totalEligibleAfterControl

            val totalParked = verificationExpenditures.onlyParkedOnes().calculateCertified(options)
            val totalEligibleAfterVerification = verificationExpenditures.calculateVerified(options)

            val totalDeductedSplit = totalRequested.minus(totalEligibleAfterVerification).minus(totalParked)
            val totalDeductedPerCostCategoryByTypologyOfError = verificationExpenditures
                .filter { it.typologyOfErrorId != null }
                .groupBy { it.typologyOfErrorId!! }
                .mapValues { it.value.calculateTotalDeductedPerCostCategory() }

            val certificateDeductionRows = allTypologyErrors.filter { it.id in totalDeductedPerCostCategoryByTypologyOfError.keys }
                .map { error -> totalDeductedPerCostCategoryByTypologyOfError.extractFor(error) }
                .appendFlatRatesDeductionRow(options, totalDeducted = totalDeductedSplit)

            val overview = VerificationDeductionOverview(
                deductionRows = certificateDeductionRows,
                staffCostsFlatRate = options.staffCostsFlatRate,
                officeAndAdministrationFlatRate = options.officeAndAdministrationOnStaffCostsFlatRate
                    ?: options.officeAndAdministrationOnDirectCostsFlatRate,
                travelAndAccommodationFlatRate = options.travelAndAccommodationOnStaffCostsFlatRate,
                otherCostsOnStaffCostsFlatRate = options.otherCostsOnStaffCostsFlatRate,
                total = certificateDeductionRows.sumUp()
            )

            return@map CertificateVerificationDeductionOverview(
                partnerReportNumber = certificate.partnerReportNumber,
                partnerNumber = certificate.partnerNumber,
                partnerRole = certificate.partnerRole,
                deductionOverview = overview
            )

        }
    }


    private fun Map<Long, Map<BudgetCostCategory, BigDecimal>>.getForTypologyAndCategory(typologyId: Long, category: BudgetCostCategory): BigDecimal =
        get(typologyId)?.get(category) ?: BigDecimal.ZERO

    private fun List<VerificationDeductionOverviewRow>.appendFlatRatesDeductionRow(
        options:  ProjectPartnerBudgetOptions,
        totalDeducted: BudgetCostsCalculationResultFull
    ): List<VerificationDeductionOverviewRow> {
        if (this.isEmpty())  {
            return this
        }
        return if (options.isEmpty()) this else this.plus(options.toFlatRatesRow(totalDeducted))
    }

    private fun Map<Long, Map<BudgetCostCategory, BigDecimal>>.extractFor(error: TypologyErrors) =
        VerificationDeductionOverviewRow(
            typologyOfErrorId = error.id,
            typologyOfErrorName = error.description,
            staffCost = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Staff),
            officeAndAdministration = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Office),
            travelAndAccommodation = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Travel),
            externalExpertise = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.External),
            equipment = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Equipment),
            infrastructureAndWorks = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Infrastructure),
            lumpSums = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.LumpSum),
            unitCosts = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.UnitCost),
            otherCosts = getForTypologyAndCategory(typologyId = error.id, BudgetCostCategory.Other),
            total = this[error.id]?.values?.sumOf { it } ?: BigDecimal.ZERO
        )

    private fun BudgetCostsCalculationResultFull.minus(subtractor: BudgetCostsCalculationResultFull) = BudgetCostsCalculationResultFull(
        staff = staff.minus(subtractor.staff),
        office = office.minus(subtractor.office),
        travel = travel.minus(subtractor.travel),
        external = external.minus(subtractor.external),
        equipment = equipment.minus(subtractor.equipment),
        infrastructure = infrastructure.minus(subtractor.infrastructure),
        other = other.minus(subtractor.other),
        lumpSum = lumpSum.minus(subtractor.lumpSum),
        unitCost = unitCost.minus(subtractor.unitCost),
        sum = sum.minus(subtractor.sum),
    )

    private fun ProjectPartnerBudgetOptions.toFlatRatesRow(flatRates: BudgetCostsCalculationResultFull): VerificationDeductionOverviewRow {
        val staffCost = if (staffCostsFlatRate == null) BigDecimal.ZERO else flatRates.staff
        val officeAndAdministration = if (officeAndAdministrationOnDirectCostsFlatRate == null
            && officeAndAdministrationOnStaffCostsFlatRate == null) BigDecimal.ZERO else flatRates.office
        val travelAndAccommodation = if (travelAndAccommodationOnStaffCostsFlatRate == null) BigDecimal.ZERO else flatRates.travel
        val otherCosts = if (otherCostsOnStaffCostsFlatRate == null) BigDecimal.ZERO else flatRates.other

        return VerificationDeductionOverviewRow(
            typologyOfErrorId = null,
            typologyOfErrorName = null,
            staffCost = staffCost,
            officeAndAdministration = officeAndAdministration,
            travelAndAccommodation = travelAndAccommodation,
            externalExpertise = BigDecimal.ZERO,
            equipment = BigDecimal.ZERO,
            infrastructureAndWorks = BigDecimal.ZERO,
            lumpSums = BigDecimal.ZERO,
            unitCosts = BigDecimal.ZERO,
            otherCosts = otherCosts,
            total = staffCost.plus(officeAndAdministration).plus(travelAndAccommodation).plus(otherCosts)
        )
    }

}