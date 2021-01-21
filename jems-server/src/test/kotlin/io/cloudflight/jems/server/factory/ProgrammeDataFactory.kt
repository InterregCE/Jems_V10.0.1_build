package io.cloudflight.jems.server.factory

import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.repository.ProgrammeLegalStatusRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProgrammeDataFactory(
    val legalStatusRepository: ProgrammeLegalStatusRepository
) {

    val legalStatus = ProgrammeLegalStatus(1, "public")

    @Transactional
    fun saveLegalStatus(): ProgrammeLegalStatus {
        return legalStatusRepository.save(legalStatus)
    }
}
