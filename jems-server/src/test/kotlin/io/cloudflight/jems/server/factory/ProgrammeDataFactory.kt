package io.cloudflight.jems.server.factory

import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProgrammeDataFactory(
    val legalStatusRepository: ProgrammeLegalStatusRepository
) {

    val legalStatus = ProgrammeLegalStatusEntity(id = 1)

    @Transactional
    fun saveLegalStatus(): ProgrammeLegalStatusEntity {
        return legalStatusRepository.save(legalStatus)
    }
}
