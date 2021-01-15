package io.cloudflight.jems.server.project.service.workpackage.output

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import org.springframework.http.HttpStatus

private const val WORK_PACKAGE_SIZE_LIMIT = 10

fun validateWorkPackageOutputsLimit(workPackageOutputs: List<WorkPackageOutput>) {
    if (workPackageOutputs.size > WORK_PACKAGE_SIZE_LIMIT)
        throw I18nValidationException(
            i18nKey = "workpackage.outputs.limit.exceeded",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
        )
}
