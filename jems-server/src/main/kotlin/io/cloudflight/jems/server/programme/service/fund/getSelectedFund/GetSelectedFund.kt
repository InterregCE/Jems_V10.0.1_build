package io.cloudflight.jems.server.programme.service.fund.getSelectedFund

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAudit
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetSelectedFund(
    private val persistence: ProgrammeFundPersistence,
) : GetSelectedFundInteractor {

    @CanRetrievePaymentsAudit
    @ExceptionWrapper(GetSelectedFundsException::class)
    @Transactional(readOnly = true)
    override fun getAvailableFunds(): List<ProgrammeFund> =
        this.persistence.getSelectedFunds()

}
