package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import io.cloudflight.jems.api.programme.dto.OutputProgrammeData
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.repository.ProgrammeDataRepository
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.nuts.service.toOutput
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgrammeDataServiceImpl(
    private val programmeDataRepository: ProgrammeDataRepository,
    private val nutsRegion3Repository: NutsRegion3Repository,
    private val auditService: AuditService
) : ProgrammeDataService {

    @Transactional(readOnly = true)
    override fun get(): OutputProgrammeData {
        return programmeDataRepository.findById(1).map { it.toOutputProgrammeData() }
            .orElseThrow { ResourceNotFoundException() }
    }

    @Transactional
    override fun update(basicData: InputProgrammeData): OutputProgrammeData {
        val oldProgrammeData = programmeDataRepository.findById(1).orElseThrow { ResourceNotFoundException() }
        val oldProgrammeBasicData = oldProgrammeData.toOutputProgrammeData()

        val savedProgrammeData = programmeDataRepository.save(
            basicData.toEntity(oldProgrammeData.programmeNuts)
        ).toOutputProgrammeData()

        programmeBasicDataChanged(changes = oldProgrammeBasicData.getChange(savedProgrammeData))
            .logWith(auditService)

        return savedProgrammeData
    }

    @Transactional
    override fun saveProgrammeNuts(regions: Collection<String>): OutputProgrammeData {
        val toBeSaved = programmeDataRepository.findById(1)
            .orElseThrow { ResourceNotFoundException() }
            .copy(programmeNuts = nutsRegion3Repository.findAllById(regions).toSet())

        val savedProgramme = programmeDataRepository.save(toBeSaved).toOutputProgrammeData()
        val updatedNuts = toBeSaved.programmeNuts.mapTo(HashSet()) { it.toOutput() }

        programmeNutsAreaChanged(updatedNuts).logWith(auditService)
        return savedProgramme
    }

}
