package io.cloudflight.jems.server.programme.service.fund.updateFunds

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

interface UpdateFundsInteractor {

    fun update(funds: List<ProgrammeFund>): List<ProgrammeFund>

}
