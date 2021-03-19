package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import io.cloudflight.jems.api.programme.dto.OutputProgrammeData

interface ProgrammeDataService {

    fun get(): OutputProgrammeData

    fun update(basicData: InputProgrammeData): OutputProgrammeData

    fun saveProgrammeNuts(regions: Collection<String>): OutputProgrammeData

    fun getAvailableNuts(): List<OutputNuts>
}
