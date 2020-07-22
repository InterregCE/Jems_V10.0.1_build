package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.ProgrammeSetup
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.ProgrammeDataRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgrammeDatatServiceImpl(
    private val programmeDataRepository: ProgrammeDataRepository
) : ProgrammeDataService {

    @Transactional(readOnly = true)
    override fun get(): ProgrammeSetup {
        return programmeDataRepository.findById(1).map { it.toProgrammeSetup() }
            .orElseThrow { ResourceNotFoundException() }
    }

    @Transactional
    override fun update(setup: ProgrammeSetup): ProgrammeSetup {
        return programmeDataRepository.save(setup.toEntity()).toProgrammeSetup()
    }

}
