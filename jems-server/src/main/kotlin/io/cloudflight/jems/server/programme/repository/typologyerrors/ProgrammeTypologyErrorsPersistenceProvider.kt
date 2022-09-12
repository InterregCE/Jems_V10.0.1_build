package io.cloudflight.jems.server.programme.repository.typologyerrors

import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeTypologyErrorsPersistenceProvider(
    private val programmeTypologyErrorsRepository: ProgrammeTypologyErrorsRepository
) : ProgrammeTypologyErrorsPersistence {

    @Transactional(readOnly = true)
    override fun getAllTypologyErrors(): List<TypologyErrors> {
        return programmeTypologyErrorsRepository.findAll().toModel()
    }

    @Transactional
    override fun updateTypologyErrors(toDeleteIds: List<Long>, toPersist: List<TypologyErrors>): List<TypologyErrors> {
        programmeTypologyErrorsRepository.deleteAllByIdInBatch(toDeleteIds)
        programmeTypologyErrorsRepository.saveAll(toPersist.toEntity())
        return programmeTypologyErrorsRepository.findAll().toModel()
    }
}
