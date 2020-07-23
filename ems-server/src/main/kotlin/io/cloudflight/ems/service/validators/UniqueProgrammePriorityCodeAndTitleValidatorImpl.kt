package io.cloudflight.ems.service.validators

import io.cloudflight.ems.api.programme.validator.UniqueProgrammePriorityCodeAndTitleValidator
import io.cloudflight.ems.service.ProgrammePriorityService
import org.springframework.stereotype.Component
import javax.validation.ConstraintValidatorContext

@Component
class UniqueProgrammePriorityCodeAndTitleValidatorImpl(private val programmePriorityService: ProgrammePriorityService) :
    UniqueProgrammePriorityCodeAndTitleValidator {

    override fun isValid(id: Long?, code: String, title: String): Boolean {
        val priorityWithSameCode = programmePriorityService.getByCode(code)
        val priorityWithSameTitle = programmePriorityService.getByTitle(title)
        if (id == null) {
            return priorityWithSameCode == null && priorityWithSameTitle == null
        } else {
            return (priorityWithSameCode == null || priorityWithSameCode.id == id)
                && (priorityWithSameTitle == null || priorityWithSameTitle.id == id)
        }
    }
}
