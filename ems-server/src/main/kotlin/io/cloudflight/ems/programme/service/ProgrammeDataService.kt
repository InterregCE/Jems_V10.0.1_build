package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.ProgrammeBasicData

interface ProgrammeDataService {

    fun get(): ProgrammeBasicData

    fun update(basicData: ProgrammeBasicData): ProgrammeBasicData

}
