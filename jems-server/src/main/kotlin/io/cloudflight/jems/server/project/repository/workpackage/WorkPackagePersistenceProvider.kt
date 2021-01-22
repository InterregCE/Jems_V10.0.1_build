package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorOutputRepository
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageOutputEntity
import io.cloudflight.jems.server.project.repository.description.ProjectPeriodRepository
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Sort

@Repository
class WorkPackagePersistenceProvider(
    private val workPackageRepository: WorkPackageRepository,
    private val workPackageInvestmentRepository: WorkPackageInvestmentRepository,
    private val indicatorOutputRepository: IndicatorOutputRepository,
    private val projectPeriodRepository: ProjectPeriodRepository
) : WorkPackagePersistence {

    @Transactional
    override fun updateWorkPackageOutputs(
        workPackageId: Long,
        workPackageOutputs: List<WorkPackageOutput>
    ): List<WorkPackageOutput> {
        val workPackage = getWorkPackageOrThrow(workPackageId)
        val outputsToSave = mutableListOf<WorkPackageOutputEntity>()

        for ((index, it) in workPackageOutputs.withIndex()) {
                    val indicatorOutput =
                        if (it.programmeOutputIndicatorId != null) indicatorOutputRepository.findById(it.programmeOutputIndicatorId)
                            .orElse(null)
                        else null
                    val projectPeriod =
                        if (it.periodNumber != null)
                            projectPeriodRepository.findByIdProjectIdAndIdNumber(
                                workPackage.project.id,
                                it.periodNumber
                            )
                        else null

                    outputsToSave.add(it.toEntity(indicatorOutput, workPackage, projectPeriod, index))
                }

        return workPackageRepository.save(
            getWorkPackageOrThrow(workPackageId).copy(
                workPackageOutputs = outputsToSave
            )
        ).workPackageOutputs.toWorkPackageOutputList()

    }


    @Transactional(readOnly = true)
    override fun getWorkPackageOutputsForWorkPackage(workPackageId: Long): List<WorkPackageOutput> =
        getWorkPackageOrThrow(workPackageId).workPackageOutputs
            .toWorkPackageOutputList()

    @Transactional(readOnly = true)
    override fun getWorkPackageInvestment(workPackageInvestmentId: Long) =
        getWorkPackageInvestmentOrThrow(workPackageInvestmentId).toWorkPackageInvestment()

    @Transactional(readOnly = true)
    override fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable) =
        workPackageInvestmentRepository.findAllByWorkPackageId(workPackageId, pageable).toWorkPackageInvestmentPage()

    @Transactional(readOnly = true)
    override fun getProjectInvestmentSummaries(projectId: Long) =
        workPackageInvestmentRepository.findInvestmentsByProjectId(projectId).toInvestmentSummaryList()

    @Transactional
    override fun addWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment) =
        getWorkPackageOrThrow(workPackageId).let {
            val savedWorkPackage =
                workPackageInvestmentRepository.save(workPackageInvestment.toWorkPackageInvestmentEntity(it))
            updateSortOnNumber(workPackageId)
            savedWorkPackage.id
        }

    @Transactional
    override fun updateWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment) =
        if (workPackageInvestment.id != null) {
            workPackageInvestmentRepository.findById(workPackageInvestment.id).ifPresentOrElse(
                {
                    getWorkPackageOrThrow(workPackageId).let {
                        workPackageInvestmentRepository.save(workPackageInvestment.toWorkPackageInvestmentEntity(it))
                    }
                },
                { throw ResourceNotFoundException("WorkPackageInvestmentEntity") }
            )
            updateSortOnNumber(workPackageId)
        } else throw ResourceNotFoundException("workPackageInvestment id is null")

    @Transactional
    override fun deleteWorkPackageInvestment(workPackageId: Long, workPackageInvestmentId: Long) {
        workPackageInvestmentRepository.deleteById(workPackageInvestmentId)
        updateSortOnNumber(workPackageId)
    }

    @Transactional(readOnly = true)
    override fun getWorkPackageActivitiesForWorkPackage(workPackageId: Long): List<WorkPackageActivity> =
        getWorkPackageOrThrow(workPackageId).activities.toModel()

    @Transactional
    override fun updateWorkPackageActivities(
        workPackageId: Long,
        workPackageActivities: List<WorkPackageActivity>
    ): List<WorkPackageActivity> =
        workPackageRepository.save(
            getWorkPackageOrThrow(workPackageId).copy(
                activities = workPackageActivities.toIndexedEntity(workPackageId)
            )
        ).activities.toModel()

    @Transactional(readOnly = true)
    override fun getProjectIdFromWorkPackageInvestment(workPackageInvestmentId: Long): Long =
        getWorkPackageInvestmentOrThrow(workPackageInvestmentId).workPackage.project.id

    private fun getWorkPackageOrThrow(workPackageId: Long): WorkPackageEntity =
        workPackageRepository.findById(workPackageId).orElseThrow { ResourceNotFoundException("workPackage") }

    private fun getWorkPackageInvestmentOrThrow(workPackageInvestmentId: Long): WorkPackageInvestmentEntity =
        workPackageInvestmentRepository.findById(workPackageInvestmentId)
            .orElseThrow { ResourceNotFoundException("WorkPackageInvestmentEntity") }

    private fun updateSortOnNumber(workPackageId: Long) {
        val sort = Sort.by(Sort.Direction.ASC, "id")

        val workPackageInvestments = workPackageInvestmentRepository.findAllByWorkPackageId(workPackageId, sort)
            .mapIndexed { index, old -> old.copy(investmentNumber = index.plus(1)) }
        workPackageInvestmentRepository.saveAll(workPackageInvestments)
    }

}
