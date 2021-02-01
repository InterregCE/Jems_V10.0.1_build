package io.cloudflight.jems.server.programme.service.priority.create_priority

import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.validator.validateCreateHasUniqueCodeAndTitle
import io.cloudflight.jems.server.programme.service.priority.validator.validateCreateProgrammePriority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePriority(
    private val persistence: ProgrammePriorityPersistence,
) : CreatePriorityInteractor {

    @Transactional
    @CanUpdateProgrammeSetup
    override fun createPriority(priority: ProgrammePriority): ProgrammePriority {
        validateCreateProgrammePriority(
            programmePriority = priority,
            getPriorityIdByCode = { persistence.getPriorityIdByCode(it) },
            getPriorityIdByTitle = { persistence.getPriorityIdByTitle(it) },
            getPriorityIdForPolicyIfExists = { persistence.getPriorityIdForPolicyIfExists(it) },
            getSpecificObjectivesByCodes = { persistence.getSpecificObjectivesByCodes(it) }
        )
        return persistence.create(priority)
    }

}
