package io.cloudflight.jems.server.programme.service.fund.get_selected_fund

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetSelectedFund(
    private val persistence: ProgrammeFundPersistence,
) : GetSelectedFundInteractor {

    //    @CanViewPaymentsAudit
    @ExceptionWrapper(GetSelectedFundsException::class)
    @Transactional(readOnly = true)
    override fun getAvailableFunds(): List<ProgrammeFundType> =
        this.persistence.getSelectedFunds().map { it.type }

}
