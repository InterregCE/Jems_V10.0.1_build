package io.cloudflight.jems.server.project.service.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.InputWorkPackageOutput
import io.cloudflight.jems.api.project.dto.workpackage.workpackageoutput.OutputWorkPackageOutput

interface WorkPackageOutputPersistence {

    fun updateWorkPackageOutputs(projectId: Long, inputWorkPackageOutputs: Set<InputWorkPackageOutput>, workPackageId: Long): Set<OutputWorkPackageOutput>

    fun getWorkPackageOutputsForWorkPackage(workPackageId: Long): Set<OutputWorkPackageOutput>

}
