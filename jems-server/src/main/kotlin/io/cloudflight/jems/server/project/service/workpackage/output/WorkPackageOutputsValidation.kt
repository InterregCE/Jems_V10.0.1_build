package io.cloudflight.jems.server.project.service.workpackage.output

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import org.springframework.http.HttpStatus

fun validateWorkPackageOutputsLimit(workPackageOutputs: Set<WorkPackageOutputUpdate>) {
    if (workPackageOutputs.size > 10)
        throw I18nValidationException(
            i18nKey = "workpackage.outputs.limit.exceeded",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        )
}
