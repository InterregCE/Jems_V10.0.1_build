package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeLegalStatus
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLegalStatus
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.exception.I18nValidationException
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.ProgrammeLegalStatusRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgrammeLegalStatusServiceImpl (
    private val programmeLegalStatusRepository: ProgrammeLegalStatusRepository,
    private val auditService: AuditService
) : ProgrammeLegalStatusService {

    companion object {
        val MAX_COUNT = 20

        private val TO_CREATE = true // isCreation() == true
        private val TO_JUST_UPDATE = false // isCreation() == false
    }

    @Transactional(readOnly = true)
    override fun get(): List<OutputProgrammeLegalStatus> {
        return programmeLegalStatusRepository.findAll().map { it.toOutputProgrammeLegalStatus() }
    }

    @Transactional
    override fun save(legalStatuses: Collection<InputProgrammeLegalStatus>): List<OutputProgrammeLegalStatus> {
        val toBeSaved = legalStatuses.map { it.toEntity() }

        // persist both existing Legal Statuses as well as newly created Legal Statuses
        programmeLegalStatusRepository.saveAll(toBeSaved)
        validateMax20LegalStatuses()

        val result = programmeLegalStatusRepository.findAll()
        programmeLegalStatusesChanged(result).logWith(auditService)
        return result.map { it.toOutputProgrammeLegalStatus() }
    }

    @Transactional
    override fun delete(legalStatusId: Long) {
        if (legalStatusId == 1L || legalStatusId == 2L) {
            throw(ResourceNotFoundException("programme_legal_status"))
        }
        programmeLegalStatusRepository.delete(
            programmeLegalStatusRepository.findById(legalStatusId)
                .orElseThrow { ResourceNotFoundException("programme_legal_status") }
        )
    }

    private fun validateMax20LegalStatuses() {
        if (programmeLegalStatusRepository.count() > MAX_COUNT)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "programme.legal.status.wrong.size"
            )
    }

}
