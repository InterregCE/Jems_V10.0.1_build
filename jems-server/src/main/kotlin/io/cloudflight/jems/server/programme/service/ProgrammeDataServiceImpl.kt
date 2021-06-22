package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import io.cloudflight.jems.api.programme.dto.OutputProgrammeData
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.common.entity.toYear
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.nuts.service.toOutput
import io.cloudflight.jems.server.programme.authorization.CanRetrieveNuts
import io.cloudflight.jems.server.programme.authorization.CanRetrieveProgrammeSetup
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.entity.ProgrammeData
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
    override fun get(): OutputProgrammeData =
        getProgrammeDataOrThrow().toOutputProgrammeData()

    @Transactional
    @CanUpdateProgrammeSetup
    override fun update(basicData: InputProgrammeData): OutputProgrammeData {
        validateInputProgrammeData(basicData)
        val oldProgrammeData = getProgrammeDataOrThrow()
        val oldProgrammeBasicData = oldProgrammeData.toOutputProgrammeData()

        val savedProgrammeData = programmeDataRepository.save(
            basicData.toEntity(oldProgrammeData.programmeNuts)
        ).toOutputProgrammeData()

        programmeBasicDataChanged(changes = oldProgrammeBasicData.getChange(savedProgrammeData))
            .logWith(auditService)

        return savedProgrammeData
    }

    @Transactional
    @CanUpdateProgrammeSetup
    override fun saveProgrammeNuts(regions: Collection<String>): OutputProgrammeData {
        val programmeData = getProgrammeDataOrThrow()
        val oldNuts = programmeData.programmeNuts.mapTo(HashSet()) { it.id }

        if (callRepository.existsByStatus(CallStatus.PUBLISHED) && !regions.containsAll(oldNuts)) {
            throw UpdateProgrammeAreasWhenProgrammeSetupRestricted()
        }

        val toBeSaved = programmeData
            .copy(programmeNuts = nutsRegion3Repository.findAllById(regions).toSet())

        val savedProgramme = programmeDataRepository.save(toBeSaved).toOutputProgrammeData()
        val updatedNuts = toBeSaved.programmeNuts.mapTo(HashSet()) { it.toOutput() }

        programmeNutsAreaChanged(updatedNuts).logWith(auditService)
        return savedProgramme
    }

    @Transactional(readOnly = true)
    @CanRetrieveNuts
    override fun getAvailableNuts(): List<OutputNuts> =
        getProgrammeDataOrThrow().programmeNuts.toDto()


    private fun getProgrammeDataOrThrow(): ProgrammeData =
        programmeDataRepository.findById(1).orElseThrow { ResourceNotFoundException() }

    private fun validateInputProgrammeData(basicData: InputProgrammeData){
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.startDateBeforeEndDate(basicData.firstYear.toYear(), basicData.lastYear.toYear(),"firstYear","lastYear"),
            generalValidator.maxLength(basicData.cci, 15, "cci"),
            generalValidator.maxLength(basicData.title, 255, "title"),
            generalValidator.maxLength(basicData.version, 255, "version"),
            generalValidator.numberBetween(basicData.firstYear, 1000,9999, "firstYear"),
            generalValidator.numberBetween(basicData.lastYear, 1000,9999, "lastYear"),
            generalValidator.maxLength(basicData.commissionDecisionNumber, 255, "commissionDecisionNumber"),
            generalValidator.maxLength(basicData.programmeAmendingDecisionNumber, 255, "programmeAmendingDecisionNumber"),

        )
    }
}
