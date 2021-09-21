package io.cloudflight.jems.server.programme.service.costoption.create_lump_sum

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.authorization.CanUpdateProgrammeSetup
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.validateCreateLumpSum
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateLumpSum(
    private val persistence: ProgrammeLumpSumPersistence,
    private val audit: AuditService,
    private val generalValidator: GeneralValidatorService,
) : CreateLumpSumInteractor {

    @CanUpdateProgrammeSetup
    @Transactional
    @ExceptionWrapper(CreateLumpSumException::class)
    override fun createLumpSum(lumpSum: ProgrammeLumpSum): ProgrammeLumpSum {
        validateInput(lumpSum)
        validateCreateLumpSum(lumpSumToValidate = lumpSum, currentCount = persistence.getCount())
        val saved = persistence.createLumpSum(lumpSum)

        lumpSumCreatedAudit(saved).logWith(audit)
        return saved
    }

    private fun lumpSumCreatedAudit(lumpSum: ProgrammeLumpSum): AuditCandidate {
        return AuditBuilder(AuditAction.PROGRAMME_LUMP_SUM_ADDED)
            .description("Programme lump sum (id=${lumpSum.id}) '${lumpSum.name}' has been added")
            .build()
    }

    private fun validateInput(programmeLumpSum: ProgrammeLumpSum) =
        generalValidator.throwIfAnyIsInvalid(
            generalValidator.maxLength(programmeLumpSum.name, 50, "name"),
            generalValidator.maxLength(programmeLumpSum.description, 255, "description"),
            generalValidator.notNull(programmeLumpSum.phase, "phase"),
        )

}
