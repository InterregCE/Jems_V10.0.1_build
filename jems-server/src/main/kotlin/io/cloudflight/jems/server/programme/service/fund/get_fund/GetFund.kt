package io.cloudflight.jems.server.programme.service.fund.get_fund

import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetFund(
    private val persistence: ProgrammeFundPersistence,
) : GetFundInteractor {

    @CanRetrieveProgrammeSetup
    @Transactional(readOnly = true)
    override fun getFunds(): List<ProgrammeFund> =
        persistence.getMax20Funds()

}
