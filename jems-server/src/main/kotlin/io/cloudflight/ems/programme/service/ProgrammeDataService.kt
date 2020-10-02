package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.InputProgrammeData
import io.cloudflight.ems.api.programme.dto.OutputProgrammeData

interface ProgrammeDataService {

    fun get(): OutputProgrammeData

    fun update(basicData: InputProgrammeData): OutputProgrammeData

    fun saveProgrammeNuts(regions: Collection<String>): OutputProgrammeData

}
