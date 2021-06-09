package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.repository.indicator.OutputIndicatorRepository
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.repository.ApplicationVersionNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.workpackage.investment.WorkPackageInvestmentRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.WorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.toIndexedEntity
import io.cloudflight.jems.server.project.repository.workpackage.output.toModel
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.toApplicantAndStatus
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackageHistoricalData
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackageSimple
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackageSimpleHistoricalData
import io.cloudflight.jems.server.project.service.workpackage.toWorkPackageOutputsHistoricalData
import java.sql.Timestamp
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Sort

@Repository
class WorkPackagePersistenceProvider(
    private val workPackageRepository: WorkPackageRepository,
    private val workPackageActivityRepository: WorkPackageActivityRepository,
    private val workPackageOutputRepository: WorkPackageOutputRepository,
    private val workPackageInvestmentRepository: WorkPackageInvestmentRepository,
    private val outputIndicatorRepository: OutputIndicatorRepository,
    private val projectVersionUtils: ProjectVersionUtils,
) : WorkPackagePersistence {

    @Transactional(readOnly = true)
    override fun getWorkPackagesWithOutputsAndActivitiesByProjectId(projectId: Long): List<ProjectWorkPackage> {
        // fetch all work packages in 1 request
        val workPackagesPaged = workPackageRepository.findAllByProjectId(projectId)
        val workPackageIds = workPackagesPaged.mapTo(HashSet()) { it.id }

        // fetch all activities and deliverables in 1 request
        val activitiesByWorkPackages = workPackageActivityRepository.findAllByActivityIdWorkPackageIdIn(workPackageIds)
            .groupBy { it.activityId.workPackageId }

        // fetch all outputs in 1 request
        val outputsByWorkPackages = workPackageOutputRepository.findAllByOutputIdWorkPackageIdIn(workPackageIds)
            .groupBy { it.outputId.workPackageId }

        return workPackagesPaged.map { wp ->
            wp.toModel(
                getActivitiesForWorkPackageId = { id -> activitiesByWorkPackages[id] },
                getOutputsForWorkPackageId = { id -> outputsByWorkPackages[id] },
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getWorkPackagesWithAllDataByProjectId(projectId: Long): List<ProjectWorkPackageFull> {
        // fetch all work packages in 1 request
        val sort = Sort.by(Sort.Direction.ASC, "id")
        val workPackages = workPackageRepository.findAllByProjectId(projectId, sort)
        val workPackageIds = workPackages.mapTo(HashSet()) { it.id }

        // fetch all activities and deliverables in 1 request
        val activitiesByWorkPackages = workPackageActivityRepository.findAllByActivityIdWorkPackageIdIn(workPackageIds)
            .groupBy { it.activityId.workPackageId }

        // fetch all outputs in 1 request
        val outputsByWorkPackages = workPackageOutputRepository.findAllByOutputIdWorkPackageIdIn(workPackageIds)
            .groupBy { it.outputId.workPackageId }

        // fetch all investments
        val investmentsByWorkPackages = workPackageInvestmentRepository.findInvestmentsByProjectId(projectId)
            .groupBy { it.workPackage.id }

        return workPackages.map { wp ->
            wp.toModelFull(
                getActivitiesForWorkPackageId = { id -> activitiesByWorkPackages[id] },
                getOutputsForWorkPackageId = { id -> outputsByWorkPackages[id] },
                getInvestmentsForWorkPackageId = { id -> investmentsByWorkPackages[id] }
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getWorkPackagesByProjectId(projectId: Long, version: String?): List<OutputWorkPackageSimple> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                workPackageRepository.findAllByProjectId(projectId).map { it.toOutputWorkPackageSimple() }
            },
            previousVersionFetcher = { timestamp ->
                workPackageRepository.findAllByProjectIdAsOfTimestamp(projectId, timestamp).toOutputWorkPackageSimpleHistoricalData()
            }
        ) ?: emptyList()
    }

    @Transactional(readOnly = true)
    override fun getWorkPackageById(workPackageId: Long, version: String?): OutputWorkPackage {
        val workPackage = getWorkPackageOrThrow(workPackageId)
        return projectVersionUtils.fetch(version, workPackage.project.id,
            currentVersionFetcher = {
                workPackage.toOutputWorkPackage()
            },
            previousVersionFetcher = { timestamp ->
                workPackageRepository.findByIdAsOfTimestamp(workPackageId, timestamp).toOutputWorkPackageHistoricalData()
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    @Transactional
    override fun updateWorkPackageOutputs(
        workPackageId: Long,
        workPackageOutputs: List<WorkPackageOutput>
    ): List<WorkPackageOutput> {
        val workPackage = getWorkPackageOrThrow(workPackageId = workPackageId)

        val outputsUpdated = workPackageOutputs.toIndexedEntity(
            workPackageId = workPackageId,
            resolveProgrammeIndicatorEntity = { getIndicatorOrThrow(it) }
        )
        return workPackageRepository.save(workPackage.copy(outputs = outputsUpdated)).outputs.toModel()
    }

    @Transactional(readOnly = true)
    override fun getWorkPackageOutputsForWorkPackage(workPackageId: Long, version: String?): List<WorkPackageOutput>{
        val workPackage = getWorkPackageOrThrow(workPackageId)
        return projectVersionUtils.fetch(version, workPackage.project.id,
            currentVersionFetcher = {
                workPackage.outputs.toModel()
            },
            previousVersionFetcher = { timestamp ->
                workPackageRepository.findOutputsByWorkPackageIdAsOfTimestamp(workPackageId, timestamp).toWorkPackageOutputsHistoricalData()
            }
        ) ?: emptyList()
    }

    @Transactional(readOnly = true)
    override fun getWorkPackageInvestment(workPackageInvestmentId: Long, version: String?): WorkPackageInvestment {
        val workPackageInvestment = getWorkPackageInvestmentOrThrow(workPackageInvestmentId)
        return projectVersionUtils.fetch(version, workPackageInvestment.workPackage.project.id,
            currentVersionFetcher = {
                workPackageInvestment.toWorkPackageInvestment()
            },
            previousVersionFetcher = { timestamp ->
                workPackageInvestmentRepository.findByIdAsOfTimestamp(workPackageInvestmentId, timestamp).toWorkPackageInvestmentHistoricalData()
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun getWorkPackageInvestments(workPackageId: Long, version: String?): List<WorkPackageInvestment> {
        val workPackage = getWorkPackageOrThrow(workPackageId)
        return projectVersionUtils.fetch(version, workPackage.project.id,
            currentVersionFetcher = {
                workPackageInvestmentRepository.findAllByWorkPackageId(workPackageId).toWorkPackageInvestmentList()
            },
            previousVersionFetcher = { timestamp ->
                workPackageInvestmentRepository.findAllByWorkPackageIdAsOfTimestamp(workPackageId, timestamp).toWorkPackageInvestmentHistoricalList()
            }
        ) ?: emptyList()
    }


    @Transactional(readOnly = true)
    override fun getProjectInvestmentSummaries(projectId: Long, version: String?): List<InvestmentSummary> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                workPackageInvestmentRepository.findInvestmentsByProjectId(projectId).toInvestmentSummaryList()
            },
            previousVersionFetcher = { timestamp ->
                getWorkPackageInvestmentSummaryHistoricalData(projectId, timestamp)
            }
        ) ?: emptyList()
    }

    @Transactional(readOnly = true)
    override fun countWorkPackageInvestments(workPackageId: Long): Long =
        workPackageInvestmentRepository.countAllByWorkPackageId(workPackageId)

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
    override fun getWorkPackageActivitiesForWorkPackage(workPackageId: Long, version: String?): List<WorkPackageActivity> {
        val workPackage = getWorkPackageOrThrow(workPackageId)
        return projectVersionUtils.fetch(version, workPackage.project.id,
            currentVersionFetcher = {
                workPackage.activities.toModel()
            },
            previousVersionFetcher = { timestamp ->
                getActivitiesAndDeliverablesByWorkPackageId(workPackageId, timestamp)
            }
        ) ?: emptyList()
    }

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
    override fun getProjectFromWorkPackageInvestment(workPackageInvestmentId: Long): ProjectApplicantAndStatus =
        getWorkPackageInvestmentOrThrow(workPackageInvestmentId).workPackage.project.toApplicantAndStatus()

    private fun getWorkPackageOrThrow(workPackageId: Long): WorkPackageEntity =
        workPackageRepository.findById(workPackageId).orElseThrow { ResourceNotFoundException("workPackage") }

    private fun getWorkPackageInvestmentOrThrow(workPackageInvestmentId: Long): WorkPackageInvestmentEntity =
        workPackageInvestmentRepository.findById(workPackageInvestmentId)
            .orElseThrow { ResourceNotFoundException("WorkPackageInvestmentEntity") }

    private fun getIndicatorOrThrow(indicatorId: Long?): OutputIndicatorEntity? =
        if (indicatorId == null)
            null
        else
            outputIndicatorRepository.findById(indicatorId).orElseThrow { ResourceNotFoundException("indicatorOutput") }

    private fun updateSortOnNumber(workPackageId: Long) {
        val sort = Sort.by(Sort.Direction.ASC, "id")

        val workPackageInvestments = workPackageInvestmentRepository.findAllByWorkPackageId(workPackageId, sort)
            .mapIndexed { index, old -> old.copy(investmentNumber = index.plus(1)) }
        workPackageInvestmentRepository.saveAll(workPackageInvestments)
    }

    private fun getActivitiesAndDeliverablesByWorkPackageId(workPackageId: Long, timestamp: Timestamp): List<WorkPackageActivity> {
        val activities = workPackageActivityRepository.findAllActivitiesByWorkPackageIdAsOfTimestamp(workPackageId, timestamp).toActivityHistoricalData()

        activities.forEach{
            it.deliverables = workPackageActivityRepository.findAllDeliverablesByWorkPackageIdAndActivityIdAsOfTimestamp(workPackageId, it.activityNumber, timestamp).toDeliverableHistoricalData()
        }

        return activities
    }

    private fun getWorkPackageInvestmentSummaryHistoricalData(projectId: Long, timestamp: Timestamp): List<InvestmentSummary> {
        val workPackages = workPackageRepository.findAllByProjectIdAsOfTimestamp(projectId, timestamp)
            .toOutputWorkPackageSimpleHistoricalData()
        var investments = mutableListOf<InvestmentSummary>()

        workPackages.forEach {
            val investmentsForWorkPackage = workPackageInvestmentRepository.findAllSummariesByWorkPackageIdAsOfTimestamp(it.id, timestamp).toWorkPackageInvestmentSummaryList(it.number)
            investments = investments.plus(investmentsForWorkPackage) as MutableList<InvestmentSummary>
        }

        return investments.toList()
    }
}
