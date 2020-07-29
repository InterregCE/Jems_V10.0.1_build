package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.InputProgrammeData

interface ProgrammeDataService {

    fun get(): InputProgrammeData

    fun update(dataInput: InputProgrammeData): InputProgrammeData

}
