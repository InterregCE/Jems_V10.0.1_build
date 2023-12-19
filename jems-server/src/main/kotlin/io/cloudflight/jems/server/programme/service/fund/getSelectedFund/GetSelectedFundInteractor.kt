package io.cloudflight.jems.server.programme.service.fund.getSelectedFund

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

interface GetSelectedFundInteractor {

    fun getAvailableFunds(): List<ProgrammeFund>

}
