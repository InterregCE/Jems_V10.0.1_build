package io.cloudflight.jems.server.programme.service.fund.getFund

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

interface GetFundInteractor {

    fun getFunds(): List<ProgrammeFund>

}
