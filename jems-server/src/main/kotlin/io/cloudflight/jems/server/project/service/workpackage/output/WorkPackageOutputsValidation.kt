package io.cloudflight.jems.server.project.service.workpackage.output

import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import java.math.BigDecimal

private const val MAX_OUTPUTS_PER_WORK_PACKAGE = 10
private val MAX_TARGET_VALUE = BigDecimal.valueOf(999_999_999_99, 2)

fun validateWorkPackageOutputs(workPackageOutputs: List<WorkPackageOutput>) {
    if (workPackageOutputs.size > MAX_OUTPUTS_PER_WORK_PACKAGE)
        throw I18nValidationException(i18nKey = "project.workPackage.outputs.max.allowed.reached")

    if (workPackageOutputs.mapNotNull { it.targetValue }.any {
            it < BigDecimal.ZERO || it > MAX_TARGET_VALUE || it.scale() > 2
        })
        throw I18nValidationException(i18nKey = "project.workPackage.targetValue.not.valid")
}
