package io.cloudflight.jems.server.programme.service.fund.update_fund

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.programmeFundsChanged
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateFund(
    private val persistence: ProgrammeFundPersistence,
    private val auditService: AuditService,
) : UpdateFundInteractor {

    companion object {
        const val MAX_FUNDS = 20
        const val MAX_FUND_ABBREVIATION_LENGTH = 127
        const val MAX_FUND_DESCRIPTION_LENGTH = 255
        private const val NEW = false // ID == 0
        private const val EXISTING = true // ID != 0
    }

    @CanUpdateProgrammeSetup
    @Transactional
    override fun updateFunds(funds: List<ProgrammeFund>): List<ProgrammeFund> {
        validateSize(funds)
        validateTexts(funds)

        val existingFundsById = persistence.getMax20Funds().associateBy { it.id }
        val fundsByStatus = funds.groupBy { it.id != 0L }

        val toUpdateFunds = extractFundsToUpdate(
            shouldExist = fundsByStatus.extract(EXISTING),
            existingIds = existingFundsById.keys
        )
        val toUpdateFundIds = toUpdateFunds.mapTo(HashSet()) { it.id }
        val toDeleteFundIds = existingFundsById.keys.filterTo(HashSet()) { !toUpdateFundIds.contains(it) }

        if (persistence.isProgrammeSetupRestricted()) {
            if (toDeleteFundIds.isNotEmpty()
                || toUpdateFunds.any { fund -> fund.selectionChanged(existingFundsById[fund.id]) })
                throw MakingChangesWhenProgrammeSetupRestricted()
        }

        val result = persistence.updateFunds(
            toDeleteIds = toDeleteFundIds,
            funds = toUpdateFunds union fundsByStatus.extract(NEW),
        )
        programmeFundsChanged(result).logWith(auditService)
        return result
    }

    private fun validateSize(funds: Collection<ProgrammeFund>) {
        if (funds.size > MAX_FUNDS)
            throw MaxAllowedFundsReachedException(MAX_FUNDS)
    }

    private fun validateTexts(funds: Collection<ProgrammeFund>) {
        val longAbbreviations = funds.filter { fund ->
            fund.translatedValues.any {
                it.abbreviation != null && it.abbreviation.length > MAX_FUND_ABBREVIATION_LENGTH
            }
        }
        if (longAbbreviations.isNotEmpty())
            throw FundAbbreviationTooLong()

        val longDescriptions = funds.filter { fund ->
            fund.translatedValues.any {
                it.description != null && it.description.length > MAX_FUND_DESCRIPTION_LENGTH
            }
        }
        if (longDescriptions.isNotEmpty())
            throw FundDescriptionTooLong()
    }

    private fun extractFundsToUpdate(shouldExist: List<ProgrammeFund>, existingIds: Set<Long>): Set<ProgrammeFund> {
        val toUpdateFunds = mutableSetOf<ProgrammeFund>()

        shouldExist.forEach { fundThatShouldExist ->
            if (existingIds.contains(fundThatShouldExist.id))
                toUpdateFunds.add(fundThatShouldExist)
            else
                throw FundNotFound()
        }

        return toUpdateFunds.toSet()
    }

    private fun Map<Boolean, List<ProgrammeFund>>.extract(key: Boolean) = get(key) ?: emptyList()

}
