package io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output

import io.cloudflight.jems.server.project.authorization.CanUpdateProjectWorkPackage
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.validateWorkPackageOutputs
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateWorkPackageOutput(
    private val projectPersistence: ProjectPersistence,
    private val workPackagePersistence: WorkPackagePersistence,
) : UpdateWorkPackageOutputInteractor {

    @CanUpdateProjectWorkPackage
    @Transactional
    override fun updateOutputsForWorkPackage(
        projectId: Long,
        workPackageId: Long,
        outputs: List<WorkPackageOutput>,
    ): List<WorkPackageOutput> {
        validateWorkPackageOutputs(outputs)
        val status = projectPersistence.getApplicantAndStatusById(projectId).projectStatus

        if (status.isAlreadyContracted()) {
            val previousOutputs = workPackagePersistence.getWorkPackageOutputsForWorkPackage(workPackageId, projectId = projectId)
            return workPackagePersistence.updateWorkPackageOutputsAfterContracted(
                workPackageId = workPackageId,
                workPackageOutputs = outputs.prepareForUpdateWithoutDeletion(previousOutputs),
            )
        } else {
            return workPackagePersistence.updateWorkPackageOutputs(
                workPackageId = workPackageId,
                workPackageOutputs = outputs,
            )
        }
    }

    private fun List<WorkPackageOutput>.prepareForUpdateWithoutDeletion(previous: List<WorkPackageOutput>): List<WorkPackageOutput> {
        val previousOutputsByNr = previous.associateBy { it.outputNumber }

        val newValuesByNumber = groupBy { it.outputNumber }
        val toUpdate = previousOutputsByNr.values.updateWith { outputNr -> newValuesByNumber[outputNr]?.first() }
        val toCreate = newValuesByNumber.minus(previousOutputsByNr.keys).values.flatten()
            .toCreate(shiftIndex = previousOutputsByNr.keys.maxOf { it })
        return toUpdate.plus(toCreate)
    }

    private fun Collection<WorkPackageOutput>.updateWith(resolveNew: (Int) -> WorkPackageOutput?): List<WorkPackageOutput> = map {
        val newData = resolveNew.invoke(it.outputNumber)
        if (newData == null) {
            it.deactivated = true
            return@map it
        } else {
            it.programmeOutputIndicatorId = newData.programmeOutputIndicatorId
            it.targetValue = newData.targetValue
            it.periodNumber = newData.periodNumber
            it.title = newData.title
            it.description = newData.description
            it.deactivated = it.deactivated || newData.deactivated
            return@map it
        }
    }

    private fun Collection<WorkPackageOutput>.toCreate(shiftIndex: Int): List<WorkPackageOutput> = mapIndexed { index, output ->
        output.outputNumber = shiftIndex.plus(index).plus(1)
        return@mapIndexed output
    }
}
