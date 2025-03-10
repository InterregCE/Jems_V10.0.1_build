package io.cloudflight.jems.server.project.service.partner.cofinancing.update_cofinancing

import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldSetting
import io.cloudflight.jems.server.call.service.model.CallFundRate
import io.cloudflight.jems.server.call.service.model.FieldVisibilityStatus
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.UpdateProjectPartnerCoFinancing
import java.math.BigDecimal
import org.springframework.http.HttpStatus
import java.util.SortedSet
import kotlin.collections.HashSet

const val PARTNER_CONTRIBUTIONS_NOT_ENABLED_ERROR_KEY = "project.partner.coFinancing.add.new.contributions.not.enabled"
private const val MAX_FUNDS = 5

fun validateFinancing(
    financing: Collection<UpdateProjectPartnerCoFinancing>,
    allowedFundIds: Set<Long>,
    fundRates: SortedSet<CallFundRate>
) {
    validateCoFinancingPercentageRange(financing, fundRates)
    validateCoFinancingTotalPercentage(financing)
    validateSingleCoFinancing(financing)
    validateMaxFundsPerPartner(financing, allowedFundIds)
    validateUniqueCoFinancingFund(financing)
    validateFundAllowedForCall(financing, allowedFundIds)
}

fun validateContributionAFConfiguration(
    afConfig: Set<ApplicationFormFieldConfiguration>,
    partnerContributions: List<ProjectPartnerContribution>) {
    val addPartnerContribution = afConfig
        .find { it.id == ApplicationFormFieldSetting.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN.id }
    if (addPartnerContribution?.visibilityStatus == FieldVisibilityStatus.NONE
        && partnerContributions.size > 1) {
        throw I18nValidationException(i18nKey = PARTNER_CONTRIBUTIONS_NOT_ENABLED_ERROR_KEY)
    }
}

fun validateContribution(
    partnerContributions: Collection<ProjectPartnerContribution>
) {
    validateSinglePartnerContribution(partnerContributions)
    validateMandatoryPartnerContributionName(partnerContributions)
    validateMandatoryPartnerContributionStatus(partnerContributions)
    validateMandatoryPartnerContributionAmount(partnerContributions)
    validatePartnerContributionStatusIsValid(partnerContributions)
}

fun validateSpfContributionAFConfiguration(
    afConfig: Set<ApplicationFormFieldConfiguration>,
    partnerContributions: List<ProjectPartnerContributionSpf>) {
    val addPartnerContribution = afConfig
        .find { it.id == ApplicationFormFieldSetting.PARTNER_ADD_NEW_CONTRIBUTION_ORIGIN.id }
    if (addPartnerContribution?.visibilityStatus == FieldVisibilityStatus.NONE
        && partnerContributions.size > 1) {
        throw I18nValidationException(i18nKey = PARTNER_CONTRIBUTIONS_NOT_ENABLED_ERROR_KEY)
    }
}

fun validateContributionSpf(
    partnerContributions: Collection<ProjectPartnerContributionSpf>
) {
    validateMandatoryPartnerContributionSpfName(partnerContributions)
    validateMandatoryPartnerContributionSpfStatus(partnerContributions)
    validateMandatoryPartnerContributionSpfAmount(partnerContributions)
}



//co financing block
private fun validateCoFinancingPercentageRange(financing: Collection<UpdateProjectPartnerCoFinancing>, fundRates: SortedSet<CallFundRate>) {
    if (!financing.all { it.percentage != null && it.percentage <= BigDecimal.valueOf(100) && it.percentage >= BigDecimal.ZERO })
        invalid("project.partner.coFinancing.percentage.invalid")
    val financingFromFunds = financing.filter{ it.fundId != null }
    val fixedFundRates = fundRates.filter{ !it.adjustable }
    if (financingFromFunds.isNotEmpty()
        && !financingFromFunds.all {
            validateFixedFundRate(it, fixedFundRates.firstOrNull { rate -> rate.programmeFund.id == it.fundId })
        })
            invalid("project.partner.coFinancing.fixed.percentage.invalid")
}

private fun validateFixedFundRate(coFinancing: UpdateProjectPartnerCoFinancing, rate: CallFundRate?): Boolean {
    if (rate == null) {
        return true
    }
    return coFinancing.percentage!!.compareTo(rate.rate) == 0
}

private fun validateCoFinancingTotalPercentage(financing: Collection<UpdateProjectPartnerCoFinancing>) {
    if (financing.fold(BigDecimal.ZERO) { acc, e -> acc.add(e.percentage) }.compareTo(BigDecimal.valueOf(100.0)) != 0)
        invalid("project.partner.coFinancing.sum.invalid")
}

private fun validateSingleCoFinancing(financing: Collection<UpdateProjectPartnerCoFinancing>) {
    if (financing.count { it.fundId != null } + 1 != financing.count())
        invalid("project.partner.coFinancing.one.and.only.partner.contribution")
}

private fun validateMaxFundsPerPartner(
    financing: Collection<UpdateProjectPartnerCoFinancing>,
    allowedFundIds: Set<Long>
) {
    if (financing.count() > MAX_FUNDS || financing.count() > allowedFundIds.size + 1)
        invalid("project.partner.coFinancing.maximum.partner.contributions")
}

private fun validateUniqueCoFinancingFund(financing: Collection<UpdateProjectPartnerCoFinancing>) {
    if (financing.mapTo(HashSet()) { it.fundId }.size != financing.size)
        invalid("project.partner.coFinancing.fund.not.unique")
}

private fun validateFundAllowedForCall(
    financing: Collection<UpdateProjectPartnerCoFinancing>,
    allowedFundIds: Set<Long>
) {

    val fundIds = financing
        .filter { it.fundId != null }
        .mapTo(HashSet()) { it.fundId!! }

    if (!allowedFundIds.containsAll(fundIds))
        invalid("project.partner.coFinancing.fundId.not.allowed.for.call")
}
//end co financing block


//partner contribution block
private fun validateSinglePartnerContribution(partnerContributions: Collection<ProjectPartnerContribution>) {
    if (partnerContributions.count { it.isNotPartner() } + 1 != partnerContributions.count())
        invalid("project.partner.contribution.one.and.only.partner.contribution")
}

private fun validateMandatoryPartnerContributionName(partnerContributions: Collection<ProjectPartnerContribution>) {
    if (!partnerContributions.all { (it.isNotPartner() && !it.name.isNullOrBlank()) || it.isPartner })
        invalid("project.partner.contribution.name.is.mandatory")
}

private fun validateMandatoryPartnerContributionStatus(partnerContributions: Collection<ProjectPartnerContribution>) {
    if (!partnerContributions.all { it.status != null })
        invalid("project.partner.contribution.status.is.mandatory")
}

private fun validateMandatoryPartnerContributionAmount(partnerContributions: Collection<ProjectPartnerContribution>) {
    if (!partnerContributions.all { it.amount != null && it.amount >= BigDecimal.ZERO })
        invalid("project.partner.contribution.amount.is.mandatory")
}

private fun validateMandatoryPartnerContributionSpfName(partnerContributions: Collection<ProjectPartnerContributionSpf>) {
    if (!partnerContributions.all { (!it.name.isNullOrBlank()) })
        invalid("project.partner.contribution.name.is.mandatory")
}

private fun validateMandatoryPartnerContributionSpfStatus(partnerContributions: Collection<ProjectPartnerContributionSpf>) {
    if (!partnerContributions.all { it.status != null })
        invalid("project.partner.contribution.status.is.mandatory")
}

private fun validateMandatoryPartnerContributionSpfAmount(partnerContributions: Collection<ProjectPartnerContributionSpf>) {
    if (!partnerContributions.all { it.amount != null && it.amount >= BigDecimal.ZERO })
        invalid("project.partner.contribution.amount.is.mandatory")
}

private fun validatePartnerContributionStatusIsValid(partnerContributions: Collection<ProjectPartnerContribution>) {
    val partnerStatus = partnerContributions.find { it.isPartner }!!.status!!
    if (partnerStatus == ProjectPartnerContributionStatus.AutomaticPublic)
        invalid("project.partner.contribution.partner.status.invalid")
}
//end partner contribution block


private fun invalid(message: String) {
    throw I18nValidationException(
        httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        i18nKey = message
    )
}
