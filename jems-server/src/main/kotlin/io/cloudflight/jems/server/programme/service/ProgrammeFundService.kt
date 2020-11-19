package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeFund
import io.cloudflight.jems.api.programme.dto.ProgrammeFundOutputDTO

interface ProgrammeFundService {

    fun get(): List<ProgrammeFundOutputDTO>

    fun update(funds: Collection<InputProgrammeFund>): List<ProgrammeFundOutputDTO>
}
