package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeFund
import io.cloudflight.jems.api.programme.dto.ProgrammeFundOutputDTO
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.programme.authorization.CanReadProgrammeSetup
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.repository.ProgrammeFundRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgrammeFundServiceImpl(
    private val programmeFundRepository: ProgrammeFundRepository,
    private val auditService: AuditService
) : ProgrammeFundService {

    companion object {
        val MAX_COUNT = 20

        private val TO_CREATE = true // isCreation() == true
        private val TO_JUST_UPDATE = false // isCreation() == false
    }

    @CanReadProgrammeSetup
    @Transactional(readOnly = true)
    override fun get(): List<ProgrammeFundOutputDTO> {
        return programmeFundRepository.findAll().map { it.toOutputProgrammeFund() }
    }

    @CanUpdateProgrammeSetup
    @Transactional
    override fun update(funds: Collection<InputProgrammeFund>): List<ProgrammeFundOutputDTO> {
        val groupOfCreatedAndExisting = funds.groupBy { it.isCreation() }

        // for existing Funds update only flag selected
        val existing = groupOfCreatedAndExisting[TO_JUST_UPDATE]?.map { it.id!! to it.selected }?.toMap() ?: emptyMap()
        val toBeSaved = programmeFundRepository.findAllById(existing.keys)
            .map { it.copy(selected = existing[it.id]!!) }
            .toMutableList()

        // for new ones create completely new entities and set flag selected as well
        toBeSaved.addAll(groupOfCreatedAndExisting[TO_CREATE]?.map { it.toEntity() } ?: emptySet())

        // persist both existing Funds as well as newly created Funds
        programmeFundRepository.saveAll(toBeSaved)
        validateMax20Funds()

        val result = programmeFundRepository.findAll()
        programmeFundsChanged(result).logWith(auditService)
        return result.map { it.toOutputProgrammeFund() }
    }

    private fun validateMax20Funds() {
        if (programmeFundRepository.count() > MAX_COUNT)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "programme.fund.wrong.size"
            )
    }

}
