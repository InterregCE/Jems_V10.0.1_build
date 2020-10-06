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
class ProgrammeLegalStatusServiceImpl(
    private val programmeLegalStatusRepository: ProgrammeLegalStatusRepository,
    private val auditService: AuditService
) : ProgrammeLegalStatusService {

    companion object {
        val MAX_COUNT = 20
    }

    @Transactional(readOnly = true)
    override fun get(): List<OutputProgrammeLegalStatus> {
        return programmeLegalStatusRepository.findAll().map { it.toOutputProgrammeLegalStatus() }
    }

    @Transactional
    override fun save(
        toPersist: Collection<InputProgrammeLegalStatus>,
        toDelete: Collection<InputProgrammeLegalStatus>
    ): List<OutputProgrammeLegalStatus> {
        val defaultStatuses = toPersist.filter { it.id == 1L || it.id == 2L }.size
        if (defaultStatuses != 2) {
            throw(ResourceNotFoundException("programme_legal_status"))
        }

        val toBeSaved = toPersist.map { it.toEntity() }
        programmeLegalStatusRepository.saveAll(toBeSaved)
        val toBeDeleted = toDelete.map { it.toEntity() }
        programmeLegalStatusRepository.deleteAll(toBeDeleted)

        validateMax20LegalStatuses()

        val result = programmeLegalStatusRepository.findAll()
        programmeLegalStatusesChanged(result).logWith(auditService)
        return result.map { it.toOutputProgrammeLegalStatus() }
    }

    private fun validateMax20LegalStatuses() {
        if (programmeLegalStatusRepository.count() > MAX_COUNT)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "programme.legal.status.wrong.size"
            )
    }

}
