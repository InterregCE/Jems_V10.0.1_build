package io.cloudflight.jems.server.programme.service.fund.update_funds

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.programmeFundsChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateFunds(
    private val persistence: ProgrammeFundPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher,
) : UpdateFundsInteractor {

    companion object {
        const val MAX_NUMBER_OF_FUNDS = 20
        const val MAX_FUND_ABBREVIATION_LENGTH = 127
        const val MAX_FUND_DESCRIPTION_LENGTH = 255
    }

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(UpdateFundsFailed::class)
    override fun update(funds: List<ProgrammeFund>): List<ProgrammeFund> {

        validateInput(funds)

        if (funds.any { it.id == 0L && it.type != ProgrammeFundType.OTHER })
            throw CreationOfFundUnderPreDefinedTypesIsNotAllowedException()

        val currentFunds = persistence.getMax20Funds()

        val toUpdateFunds = funds.filterTo(HashSet()) { it.id != 0L }

        throwIfAnyOfToUpdateFundsNotExists(currentFunds, toUpdateFunds)

        val toDeleteFundIds = currentFunds.map { it.id }
            .filterTo(HashSet()) { currentFundId -> toUpdateFunds.none { it.id == currentFundId } }

        throwIfChangesAreNotAllowed(currentFunds, toUpdateFunds, toDeleteFundIds)

        return persistence.updateFunds(toDeleteIds = toDeleteFundIds, funds.toSet()).also {
            auditPublisher.publishEvent(programmeFundsChanged(it))
        }
    }


    private fun validateInput(funds: Collection<ProgrammeFund>) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxSize(funds, MAX_NUMBER_OF_FUNDS, "funds"),
            *funds.map {
                generalValidator.maxLength(it.abbreviation, MAX_FUND_ABBREVIATION_LENGTH, "abbreviation")
            }.toTypedArray(),
            *funds.map {
                generalValidator.maxLength(it.description, MAX_FUND_DESCRIPTION_LENGTH, "description")
            }.toTypedArray()
        )

    private fun throwIfAnyOfToUpdateFundsNotExists(
        currentFunds: Collection<ProgrammeFund>,
        toUpdateFunds: Collection<ProgrammeFund>
    ) {
        if (currentFunds.map { it.id }
                .union(toUpdateFunds.map { it.id }).size != currentFunds.distinct().size)
            throw FundNotFoundException()
    }

    private fun throwIfChangesAreNotAllowed(
        currentFunds: Collection<ProgrammeFund>,
        toUpdateFunds: Collection<ProgrammeFund>,
        toDeleteFundIds: Collection<Long>
    ) {
        if (isProgrammeSetupLocked.isLocked() &&
            (toDeleteFundIds.isNotEmpty() || isAnyFundDeselected(currentFunds, toUpdateFunds))
        ) throw ChangesAreNotAllowedException()
    }

    private fun isAnyFundDeselected(currentFunds: Collection<ProgrammeFund>, toUpdateFunds: Collection<ProgrammeFund>) =
        toUpdateFunds.any { toUpdateFund ->
            currentFunds.any { currentFund -> toUpdateFund.id == currentFund.id && currentFund.selected && !toUpdateFund.selected }
        }
}
