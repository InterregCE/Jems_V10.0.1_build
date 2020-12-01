package io.cloudflight.jems.server.programme.service.costoption.update_lump_sum

import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
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
        validateUpdateLumpSum(lumpSum)
        val saved = persistence.updateLumpSum(lumpSum)

        lumpSumChangedAudit(saved).logWith(audit)
        return saved
    }

    private fun lumpSumChangedAudit(lumpSum: ProgrammeLumpSum): AuditCandidate {
        return AuditBuilder(AuditAction.PROGRAMME_LUMP_SUM_CHANGED)
            .description("Programme lump sum (id=${lumpSum.id}) '${lumpSum.name}' has been changed")
            .build()
    }

}
