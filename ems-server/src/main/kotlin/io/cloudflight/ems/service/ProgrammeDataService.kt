package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.ProgrammeSetup

interface ProgrammeDataService {

    fun get(): ProgrammeSetup

    fun update(setup: ProgrammeSetup): ProgrammeSetup

}
