package io.cloudflight.jems.server.programme.service.validator

import io.cloudflight.jems.api.programme.validator.UniqueProgrammePriorityCodeAndTitleValidator
import io.cloudflight.jems.server.programme.service.ProgrammePriorityService
import org.springframework.stereotype.Component

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
