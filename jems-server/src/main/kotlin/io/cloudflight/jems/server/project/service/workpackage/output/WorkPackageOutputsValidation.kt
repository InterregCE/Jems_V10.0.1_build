package io.cloudflight.jems.server.project.service.workpackage.output

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import org.springframework.http.HttpStatus

private const val MAX_OUTPUTS_PER_WORK_PACKAGE = 10

fun validateWorkPackageOutputsLimit(workPackageOutputs: List<WorkPackageOutput>) {
    if (workPackageOutputs.size > MAX_OUTPUTS_PER_WORK_PACKAGE)
        throw I18nValidationException(
            i18nKey = "project.workPackage.outputs.max.allowed.reached",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        )
}
