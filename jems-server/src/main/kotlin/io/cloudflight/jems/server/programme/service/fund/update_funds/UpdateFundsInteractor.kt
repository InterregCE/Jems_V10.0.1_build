package io.cloudflight.jems.server.programme.service.fund.update_funds

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

interface UpdateFundsInteractor {

    fun update(funds: List<ProgrammeFund>): List<ProgrammeFund>

}
