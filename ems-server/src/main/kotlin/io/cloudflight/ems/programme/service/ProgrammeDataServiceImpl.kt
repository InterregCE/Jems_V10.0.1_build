package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.InputProgrammeData
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.ProgrammeDataRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgrammeDataServiceImpl(
    private val programmeDataRepository: ProgrammeDataRepository
) : ProgrammeDataService {

    @Transactional(readOnly = true)
    override fun get(): InputProgrammeData {
        return programmeDataRepository.findById(1).map { it.toInputProgrammeData() }
            .orElseThrow { ResourceNotFoundException() }
    }

    @Transactional
    override fun update(dataInput: InputProgrammeData): InputProgrammeData {
        return programmeDataRepository.save(dataInput.toEntity()).toInputProgrammeData()
    }

}
