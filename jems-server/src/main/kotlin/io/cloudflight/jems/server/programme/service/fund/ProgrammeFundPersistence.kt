package io.cloudflight.jems.server.programme.service.fund

import io.cloudflight.jems.server.programme.service.ProgrammePersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund

interface ProgrammeFundPersistence : ProgrammePersistence {

    fun getMax20Funds(): List<ProgrammeFund>

    fun updateFunds(toDeleteIds: Set<Long>, funds: Set<ProgrammeFund>): List<ProgrammeFund>

}
