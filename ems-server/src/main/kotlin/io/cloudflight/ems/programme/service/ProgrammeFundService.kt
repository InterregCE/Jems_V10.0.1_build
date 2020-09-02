package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.InputProgrammeFund
import io.cloudflight.ems.api.programme.dto.OutputProgrammeFund

interface ProgrammeFundService {

    fun get(): List<OutputProgrammeFund>

    fun update(funds: Collection<InputProgrammeFund>): List<OutputProgrammeFund>
}
