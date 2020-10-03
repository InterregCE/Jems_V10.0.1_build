package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeFund
import io.cloudflight.jems.api.programme.dto.OutputProgrammeFund

interface ProgrammeFundService {

    fun get(): List<OutputProgrammeFund>

    fun update(funds: Collection<InputProgrammeFund>): List<OutputProgrammeFund>
}
