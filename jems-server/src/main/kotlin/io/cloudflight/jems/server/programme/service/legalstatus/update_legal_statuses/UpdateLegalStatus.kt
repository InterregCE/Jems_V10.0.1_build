package io.cloudflight.jems.server.programme.service.legalstatus.update_legal_statuses

import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.programmeLegalStatusesChanged
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLegalStatus(
    private val persistence: ProgrammeLegalStatusPersistence,
    private val audit: AuditService,
) : UpdateLegalStatusInteractor {

    companion object {
        private const val MAX_ALLOWED_AMOUNT_OF_LEGAL_STATUSES = 20L
        private const val MAX_DESCRIPTION_LENGTH = 127
    }

    @CanUpdateProgrammeSetup
    @Transactional
    override fun updateLegalStatuses(
        toDeleteIds: Set<Long>,
        toPersist: Collection<ProgrammeLegalStatus>
    ): List<ProgrammeLegalStatus> {

        if (persistence.isProgrammeSetupRestricted() && toDeleteIds.isNotEmpty())
            throw DeletionWhenProgrammeSetupRestricted()

        if (toPersist.any { legalStatus ->
                legalStatus.translatedValues.any {
                    it.description != null && it.description.length > MAX_DESCRIPTION_LENGTH
                }
            })
            throw LegalStatusesDescriptionTooLong()

        val legalStatuses = persistence.updateLegalStatuses(toDeleteIds, toPersist)

        if (legalStatuses.size > MAX_ALLOWED_AMOUNT_OF_LEGAL_STATUSES)
            throw MaxAllowedLegalStatusesReachedException(MAX_ALLOWED_AMOUNT_OF_LEGAL_STATUSES)

        programmeLegalStatusesChanged(legalStatuses).logWith(audit)
        return legalStatuses
    }

}
