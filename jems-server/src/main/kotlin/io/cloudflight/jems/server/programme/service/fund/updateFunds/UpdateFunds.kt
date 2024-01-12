package io.cloudflight.jems.server.programme.service.fund.updateFunds

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.payments.service.account.PaymentAccountPersistence
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.programmeFundsChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateFunds(
    private val persistence: ProgrammeFundPersistence,
    private val paymentAccountPersistence: PaymentAccountPersistence,
    private val isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor,
    private val generalValidator: GeneralValidatorService,
    private val auditPublisher: ApplicationEventPublisher
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

        val toDeleteFunds = currentFunds.filter { saved -> saved.id !in toUpdateFunds.map { it.id } }
        val toDeselectFundIds = currentFunds.filter { currentFund ->
            toUpdateFunds.any { toUpdateFund -> currentFund.id == toUpdateFund.id && currentFund.selected && !toUpdateFund.selected }
        }.map { it.id }
        throwIfChangesAreNotAllowed(toDeleteFunds, toDeselectFundIds)

        val fundsAlreadyInUse = persistence.getFundsAlreadyInUse()
        throwIfAnyOfToDeleteFundsInUse(toDeleteFunds, fundsAlreadyInUse)
        throwIfAnyOfDeselectedFundsInUse(toDeselectFundIds, fundsAlreadyInUse)

        val paymentAccountFundIdsToBeCreated = funds.filterNot{ it in currentFunds }
            .mapTo(HashSet()) { it.id }
        val paymentAccountFundIdsToBeDeleted = funds.filter { !it.selected }.filter{ it.id in fundsAlreadyInUse }
            .mapTo(HashSet()) { it.id }

        paymentAccountPersistence.persistPaymentAccountsByFunds(paymentAccountFundIdsToBeCreated)
        paymentAccountPersistence.deletePaymentAccountsByFunds(paymentAccountFundIdsToBeDeleted)

        return persistence.updateFunds(toDeleteIds = toDeleteFunds.map { it.id }.toSet(), funds.toSet()).also {
            auditPublisher.publishEvent(programmeFundsChanged(this, it))
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
                .union(toUpdateFunds.map { it.id }).size != currentFunds.distinct().size) {
            throw FundNotFoundException()
        }
    }

    private fun throwIfChangesAreNotAllowed(
        toDeleteFunds: Collection<ProgrammeFund>,
        toDeselectFundIds: Collection<Long>
    ) {
        if (isProgrammeSetupLocked.isLocked() && (toDeleteFunds.isNotEmpty() || toDeselectFundIds.isNotEmpty())) {
            throw ChangesAreNotAllowedException()
        }
        if (toDeleteFunds.any { it.type != ProgrammeFundType.OTHER }) {
            throw ChangesAreNotAllowedException()
        }
    }

    private fun throwIfAnyOfToDeleteFundsInUse(
        toDeleteFunds: Collection<ProgrammeFund>,
        fundsAlreadyInUse: Iterable<Long>
    ) {
        val fundsThatCannotBeRemoved = toDeleteFunds.filter { deselectedFund ->
            fundsAlreadyInUse.any { it == deselectedFund.id }
        }
        if (fundsThatCannotBeRemoved.isNotEmpty()) {
            throw UpdateFundDeletedFundAlreadyUsedInCallException()
        }
    }

    private fun throwIfAnyOfDeselectedFundsInUse(deselectedFundIds: Iterable<Long>, fundsAlreadyInUse: Iterable<Long>) {
        val fundsThatCannotBeDeselected = deselectedFundIds.filter { deselectedFundId ->
            fundsAlreadyInUse.any{ it == deselectedFundId }
        }
        if (fundsThatCannotBeDeselected.isNotEmpty()) {
            throw UpdateFundDeselectedFundAlreadyUsedInCallException()
        }
    }
}
