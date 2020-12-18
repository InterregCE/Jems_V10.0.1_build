package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.indicator.IndicatorOutputRepository
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.repository.description.ProjectPeriodRepository
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

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
        workPackageOutputs: Set<WorkPackageOutputUpdate>,
    ): Set<WorkPackageOutput> {
        val workPackage = getWorkPackageOrThrow(workPackageId)

        workPackage.workPackageOutputs.clear()
        workPackageOutputs.forEach {
            val indicatorOutput =
                if (it.programmeOutputIndicatorId != null) indicatorOutputRepository.findById(it.programmeOutputIndicatorId)
                    .orElse(null)
                else null
            val projectPeriod =
                if (it.periodNumber != null)
                    projectPeriodRepository.findByIdProjectIdAndIdNumber(workPackage.project.id, it.periodNumber)
                else null

            workPackage.workPackageOutputs.add(it.toEntity(indicatorOutput, workPackage, projectPeriod))
        }

        return workPackage.workPackageOutputs.toWorkPackageOutputSet()

    }

    @Transactional(readOnly = true)
    override fun getWorkPackageOutputsForWorkPackage(workPackageId: Long): Set<WorkPackageOutput> =
        getWorkPackageOrThrow(workPackageId).workPackageOutputs
            .toWorkPackageOutputSet()

    @Transactional(readOnly = true)
    override fun getWorkPackageInvestment(workPackageInvestmentId: UUID) =
        getWorkPackageInvestmentOrThrow(workPackageInvestmentId).toWorkPackageInvestment()

    @Transactional(readOnly = true)
    override fun getWorkPackageInvestments(workPackageId: Long, pageable: Pageable) =
        workPackageInvestmentRepository.findAllByWorkPackageId(workPackageId, pageable).toWorkPackageInvestmentPage()

    @Transactional
    override fun addWorkPackageInvestment(workPackageId: Long, workPackageInvestment: WorkPackageInvestment) =
        getWorkPackageOrThrow(workPackageId).let {
            workPackageInvestmentRepository.save(workPackageInvestment.toWorkPackageInvestmentEntity(it)).id
        }

    @Transactional
    override fun updateWorkPackageInvestment(workPackageInvestment: WorkPackageInvestment) =
        if (workPackageInvestment.id != null)
            workPackageInvestmentRepository.findById(workPackageInvestment.id).ifPresentOrElse(
                {
                    it.investmentNumber = workPackageInvestment.investmentNumber
                    it.title = workPackageInvestment.title
                    it.justificationExplanation = workPackageInvestment.justificationExplanation
                    it.justificationTransactionalRelevance = workPackageInvestment.justificationTransactionalRelevance
                    it.justificationBenefits = workPackageInvestment.justificationBenefits
                    it.justificationPilot = workPackageInvestment.justificationPilot
                    it.address = workPackageInvestment.address?.toAddressEntity()
                    it.risk = workPackageInvestment.risk
                    it.documentation = workPackageInvestment.documentation
                    it.ownershipSiteLocation = workPackageInvestment.ownershipSiteLocation
                    it.ownershipRetain = workPackageInvestment.ownershipRetain
                    it.ownershipMaintenance = workPackageInvestment.ownershipMaintenance
                },
                { throw ResourceNotFoundException("WorkPackageInvestmentEntity") }
            )
        else throw ResourceNotFoundException("workPackageInvestment id is null")

    @Transactional
    override fun deleteWorkPackageInvestment(workPackageInvestmentId: UUID) =
        workPackageInvestmentRepository.deleteById(workPackageInvestmentId)

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
    override fun getProjectIdFromWorkPackageInvestment(workPackageInvestmentId: UUID): Long =
        getWorkPackageInvestmentOrThrow(workPackageInvestmentId).workPackage.project.id

    private fun getWorkPackageOrThrow(workPackageId: Long): WorkPackageEntity =
        workPackageRepository.findById(workPackageId).orElseThrow { ResourceNotFoundException("workPackage") }

    private fun getWorkPackageInvestmentOrThrow(workPackageInvestmentId: UUID): WorkPackageInvestmentEntity =
        workPackageInvestmentRepository.findById(workPackageInvestmentId)
            .orElseThrow { ResourceNotFoundException("WorkPackageInvestmentEntity") }

}
