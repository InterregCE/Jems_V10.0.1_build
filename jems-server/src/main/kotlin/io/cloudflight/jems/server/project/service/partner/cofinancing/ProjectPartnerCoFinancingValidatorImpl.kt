package io.cloudflight.jems.server.project.service.partner.cofinancing

import io.cloudflight.jems.api.project.dto.partner.cofinancing.InputProjectPartnerCoFinancing
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingValidator
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class ProjectPartnerCoFinancingValidatorImpl : ProjectPartnerCoFinancingValidator {

    override fun isCoFinancingFilledInCorrectly(finances: Set<InputProjectPartnerCoFinancing>, context: ConstraintValidatorContext): Boolean {
        val percentagesValid = finances.all { it.percentage != null && it.percentage!! >= 0 && it.percentage!! <= 100 }

        if (!percentagesValid)
            return throwValidationError(context, "project.partner.coFinancing.percentage.invalid")

        if (finances.sumBy { it.percentage!! } != 100)
            return throwValidationError(context, "project.partner.coFinancing.sum.invalid")

        // there needs to be exactly 1 fundId, which is null
        if (finances.count { it.fundId != null } + 1 != finances.count())
            return throwValidationError(context, "project.partner.coFinancing.one.and.only.partner.contribution")

        if (finances.mapTo(HashSet()) { it.fundId }.size != finances.size)
            return throwValidationError(context, "project.partner.coFinancing.fund.not.unique")

        return true
    }

    private fun throwValidationError(context: ConstraintValidatorContext, errorMsg: String): Boolean {
        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate(errorMsg).addConstraintViolation()
        return false
    }

}
