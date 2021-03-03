package io.cloudflight.jems.server.programme.service.costoption.update_lump_sum

import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.UpdateLumpSumWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.validateUpdateLumpSum
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateLumpSum(
    private val persistence: ProgrammeLumpSumPersistence,
    private val audit: AuditService,
) : UpdateLumpSumInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    override fun updateLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum {
        if (lumpSum.id == null)
            throw NullPointerException("we need id of updated entity")

        validateUpdateLumpSum(lumpSum)

        val existingLumpSum  = persistence.getLumpSum(lumpSumId = lumpSum.id)
        if (persistence.isProgrammeSetupRestricted()) {
            lumpSumUpdateRestrictions(existingLumpSum = existingLumpSum, updatedLumpSum = lumpSum)
        }
        val saved = persistence.updateLumpSum(lumpSum)

        lumpSumChangedAudit(saved).logWith(audit)
        return saved
    }

    private fun lumpSumChangedAudit(lumpSum: ProgrammeLumpSum): AuditCandidate {
        return AuditBuilder(AuditAction.PROGRAMME_LUMP_SUM_CHANGED)
            .description("Programme lump sum (id=${lumpSum.id}) '${lumpSum.name}' has been changed")
            .build()
    }

    private fun lumpSumUpdateRestrictions(existingLumpSum: ProgrammeLumpSum, updatedLumpSum: ProgrammeLumpSum) {
        if (existingLumpSum.cost?.compareTo(updatedLumpSum.cost) != 0 ||
            existingLumpSum.splittingAllowed != updatedLumpSum.splittingAllowed ||
            existingLumpSum.phase != updatedLumpSum.phase ||
            existingLumpSum.categories != updatedLumpSum.categories
        )
            throw UpdateLumpSumWhenProgrammeSetupRestricted()
    }

}
