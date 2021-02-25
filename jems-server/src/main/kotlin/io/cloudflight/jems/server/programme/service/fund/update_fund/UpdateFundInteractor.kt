package io.cloudflight.jems.server.programme.service.fund.update_fund

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

interface UpdateFundInteractor {

    fun updateFunds(funds: List<ProgrammeFund>): List<ProgrammeFund>

}
