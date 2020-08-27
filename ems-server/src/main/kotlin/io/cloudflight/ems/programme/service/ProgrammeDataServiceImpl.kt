package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.InputProgrammeData
import io.cloudflight.ems.api.programme.dto.OutputProgrammeData
import io.cloudflight.ems.audit.entity.Audit
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.nuts.repository.NutsRegion3Repository
import io.cloudflight.ems.repository.ProgrammeDataRepository
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.audit.service.AuditService
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
            .logWithService(auditService)

        return savedProgrammeData
    }

    @Transactional
    override fun saveProgrammeNuts(regions: Collection<String>): OutputProgrammeData {
        val toBeSaved = programmeDataRepository.findById(1)
            .orElseThrow { ResourceNotFoundException() }
        return programmeDataRepository.save(toBeSaved.copy(
            programmeNuts = nutsRegion3Repository.findAllById(regions).toSet()
        )).toOutputProgrammeData()
    }

}
