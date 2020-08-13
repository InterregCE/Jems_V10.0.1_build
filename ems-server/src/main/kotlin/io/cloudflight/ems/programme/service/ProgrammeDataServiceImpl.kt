package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.ProgrammeBasicData
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.repository.ProgrammeDataRepository
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.AuditService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgrammeDataServiceImpl(
    private val programmeDataRepository: ProgrammeDataRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : ProgrammeDataService {

    @Transactional(readOnly = true)
    override fun get(): ProgrammeBasicData {
        return programmeDataRepository.findById(1).map { it.toProgrammeBasicData() }
            .orElseThrow { ResourceNotFoundException() }
    }

    @Transactional
    override fun update(basicData: ProgrammeBasicData): ProgrammeBasicData {
        val oldBasicProgrammeData = programmeDataRepository.findById(1).map { it.toProgrammeBasicData() }
            .orElseThrow { ResourceNotFoundException() }

        val savedProgrammeData = programmeDataRepository.save(basicData.toEntity()).toProgrammeBasicData()

        auditService.logEvent(
            Audit.programmeBasicDataChanged(
                currentUser = securityService.currentUser,
                changes = oldBasicProgrammeData.getChange(savedProgrammeData)
            )
        )

        return savedProgrammeData
    }
}
