package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.dto.ProgrammeDataDTO
import io.cloudflight.jems.api.programme.dto.ProgrammeDataUpdateRequestDTO
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.common.entity.toYear
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.nuts.service.toOutput
import io.cloudflight.jems.server.programme.authorization.CanRetrieveNuts
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgrammeDataServiceImpl(
    private val programmeDataRepository: ProgrammeDataRepository,
    private val callRepository: CallRepository,
    private val nutsRegion3Repository: NutsRegion3Repository,
    private val auditService: AuditService,
    private val generalValidator: GeneralValidatorService
) : ProgrammeDataService {

    @Transactional(readOnly = true)
    @CanRetrieveProgrammeSetup
    override fun get(): ProgrammeDataDTO =
        getProgrammeDataOrThrow().toProgrammeDataDTO()

    @Transactional
    @CanUpdateProgrammeSetup
    override fun update(updateRequestDTO: ProgrammeDataUpdateRequestDTO): ProgrammeDataDTO {
        validateInputData(updateRequestDTO)
        val oldProgrammeData = getProgrammeDataOrThrow()
        val oldProgrammeBasicData = oldProgrammeData.toProgrammeDataDTO()

        val savedProgrammeData = programmeDataRepository.save(
            updateRequestDTO.toEntity(oldProgrammeData.programmeNuts, oldProgrammeData.defaultUserRoleId)
        ).toProgrammeDataDTO()

        programmeBasicDataChanged(changes = oldProgrammeBasicData.getChange(savedProgrammeData))
            .logWith(auditService)

        return savedProgrammeData
    }

    @Transactional
    @CanUpdateProgrammeSetup
    override fun saveProgrammeNuts(regions: Collection<String>): ProgrammeDataDTO {
        val programmeData = getProgrammeDataOrThrow()
        val oldNuts = programmeData.programmeNuts.mapTo(HashSet()) { it.id }

        if (callRepository.existsByStatus(CallStatus.PUBLISHED) && !regions.containsAll(oldNuts)) {
            throw UpdateProgrammeAreasWhenProgrammeSetupRestricted()
        }

        val toBeSaved = programmeData
            .copy(programmeNuts = nutsRegion3Repository.findAllById(regions).toSet())

        val savedProgramme = programmeDataRepository.save(toBeSaved).toProgrammeDataDTO()
        val updatedNuts = toBeSaved.programmeNuts.mapTo(HashSet()) { it.toOutput() }

        programmeNutsAreaChanged(updatedNuts).logWith(auditService)
        return savedProgramme
    }

    @Transactional(readOnly = true)
    @CanRetrieveNuts
    override fun getAvailableNuts(): List<OutputNuts> =
        getProgrammeDataOrThrow().programmeNuts.toDto()


    fun getProgrammeDataOrThrow(): ProgrammeDataEntity =
        programmeDataRepository.findById(1).orElseThrow { ResourceNotFoundException() }

    private fun validateInputData(updateRequestDTO: ProgrammeDataUpdateRequestDTO){
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.startDateBeforeEndDate(updateRequestDTO.firstYear.toYear(), updateRequestDTO.lastYear.toYear(),"firstYear","lastYear"),
            generalValidator.maxLength(updateRequestDTO.cci, 15, "cci"),
            generalValidator.maxLength(updateRequestDTO.title, 255, "title"),
            generalValidator.maxLength(updateRequestDTO.version, 255, "version"),
            generalValidator.numberBetween(updateRequestDTO.firstYear, 1000,9999, "firstYear"),
            generalValidator.numberBetween(updateRequestDTO.lastYear, 1000,9999, "lastYear"),
            generalValidator.maxLength(updateRequestDTO.commissionDecisionNumber, 255, "commissionDecisionNumber"),
            generalValidator.maxLength(updateRequestDTO.programmeAmendingDecisionNumber, 255, "programmeAmendingDecisionNumber"),
            generalValidator.maxLength(updateRequestDTO.projectIdProgrammeAbbreviation, 12, "projectIdProgrammeAbbreviation"),
        )
    }
}
