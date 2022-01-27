package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.dto.ProgrammeDataUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.ProgrammeDataDTO

interface ProgrammeDataService {

    fun get(): ProgrammeDataDTO

    fun update(updateRequestDTO: ProgrammeDataUpdateRequestDTO): ProgrammeDataDTO

    fun saveProgrammeNuts(regions: Collection<String>): ProgrammeDataDTO

    fun getAvailableNuts(): List<OutputNuts>
}
