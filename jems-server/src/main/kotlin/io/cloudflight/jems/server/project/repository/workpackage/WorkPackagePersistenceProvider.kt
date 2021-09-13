package io.cloudflight.jems.server.project.repository.workpackage

import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.repository.indicator.OutputIndicatorRepository
import io.cloudflight.jems.server.project.entity.workpackage.WorkPackageEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityPartnerId
import io.cloudflight.jems.server.project.entity.workpackage.investment.WorkPackageInvestmentEntity
import io.cloudflight.jems.server.project.repository.ApplicationVersionNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityPartnerRepository
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.workpackage.activity.toActivityHistoricalData
import io.cloudflight.jems.server.project.repository.workpackage.activity.toActivityPartnersHistoricalData
import io.cloudflight.jems.server.project.repository.workpackage.activity.toDeliverableHistoricalData
import io.cloudflight.jems.server.project.repository.workpackage.activity.toIndexedEntity
import io.cloudflight.jems.server.project.repository.workpackage.activity.toModel
import io.cloudflight.jems.server.project.repository.workpackage.activity.toSummaryModel
import io.cloudflight.jems.server.project.repository.workpackage.activity.toTimePlanActivityHistoricalData
import io.cloudflight.jems.server.project.repository.workpackage.investment.WorkPackageInvestmentRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.WorkPackageOutputRepository
import io.cloudflight.jems.server.project.repository.workpackage.output.toIndexedEntity
import io.cloudflight.jems.server.project.repository.workpackage.output.toModel
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.toApplicantAndStatus
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivity
import io.cloudflight.jems.server.project.service.workpackage.activity.model.WorkPackageActivitySummary
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackageFull
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageInvestment
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackageHistoricalData
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackageSimple
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackageSimpleHistoricalData
import io.cloudflight.jems.server.project.service.workpackage.toTimePlanWorkPackageHistoricalData
import io.cloudflight.jems.server.project.service.workpackage.toTimePlanWorkPackageOutputHistoricalData
import io.cloudflight.jems.server.project.service.workpackage.toWorkPackageOutputsHistoricalData
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Repository
class WorkPackagePersistenceProvider(
    private val workPackageRepository: WorkPackageRepository,
    private val workPackageActivityRepository: WorkPackageActivityRepository,
    private val workPackageActivityPartnerRepository: WorkPackageActivityPartnerRepository,
    private val workPackageOutputRepository: WorkPackageOutputRepository,
    private val workPackageInvestmentRepository: WorkPackageInvestmentRepository,
    private val outputIndicatorRepository: OutputIndicatorRepository,
    private val projectVersionUtils: ProjectVersionUtils
) : WorkPackagePersistence {

    @Transactional(readOnly = true)
    override fun getWorkPackagesWithOutputsAndActivitiesByProjectId(
        projectId: Long,
        version: String?
    ): List<ProjectWorkPackage> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                getWorkPackagesForTimePlanInCurrentVersion(projectId)
            },
            previousVersionFetcher = { timestamp ->
                getWorkPackagesForTimePlanInPreviousVersion(projectId, timestamp)
            }
        ) ?: emptyList()
    }

    @Transactional(readOnly = true)
    override fun getWorkPackagesWithAllDataByProjectId(projectId: Long): List<ProjectWorkPackageFull> {
        // fetch all work packages in 1 request
        val sort = Sort.by(Sort.Direction.ASC, "id")
        val workPackages = workPackageRepository.findAllByProjectId(projectId, sort)
        val workPackageIds = workPackages.mapTo(HashSet()) { it.id }

        // fetch all activities and deliverables in 1 request
        val activitiesByWorkPackages = workPackageActivityRepository.findAllByWorkPackageIdIn(workPackageIds)
            .groupBy { it.workPackageId }

        // fetch all outputs in 1 request
        val outputsByWorkPackages = workPackageOutputRepository.findAllByOutputIdWorkPackageIdIn(workPackageIds)
            .groupBy { it.outputId.workPackageId }

        // fetch projectPartnerIds in 1 request, maybe 2
        val activityIds =
            workPackageActivityRepository.findAllByWorkPackageIdIn(workPackageIds).mapTo(HashSet()) { it.id }
        val projectPartnerIds =
            workPackageActivityPartnerRepository.findAllByIdActivityIdIn(activityIds)
                .groupBy { it.id.activityId }
                .mapValues { it.value.groupBy({ it.id.activityId }, { it.id.projectPartnerId }) }

        // fetch all investments
        val investmentsByWorkPackages = workPackageInvestmentRepository.findInvestmentsByProjectId(projectId)
            .groupBy { it.workPackage.id }

        return workPackages.map { wp ->
            wp.toModelFull(
                getActivitiesForWorkPackageId = { id -> activitiesByWorkPackages[id] },
                getOutputsForWorkPackageId = { id -> outputsByWorkPackages[id] },
                getInvestmentsForWorkPackageId = { id -> investmentsByWorkPackages[id] },
                getActivityPartnersForWorkPackageId = { id -> projectPartnerIds[id] }
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
                workPackageRepository.findAllByProjectIdAsOfTimestamp(projectId, timestamp)
                    .toOutputWorkPackageSimpleHistoricalData()
            }
        ) ?: emptyList()
    }

    @Transactional(readOnly = true)
    override fun getWorkPackageById(workPackageId: Long, projectId: Long, version: String?): OutputWorkPackage {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                getWorkPackageOrThrow(workPackageId).toOutputWorkPackage()
            },
            previousVersionFetcher = { timestamp ->
                workPackageRepository.findByIdAsOfTimestamp(workPackageId, timestamp)
                    .toOutputWorkPackageHistoricalData()
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    @Transactional
    override fun updateWorkPackageOutputs(
        workPackageId: Long,
        workPackageOutputs: List<WorkPackageOutput>
    ): List<WorkPackageOutput> {

        throwIfWorkPackageNotFound(workPackageId)

        val outputsToBeSaved = workPackageOutputs.toIndexedEntity(
            workPackageId = workPackageId,
            resolveProgrammeIndicatorEntity = { getIndicatorOrThrow(it) }
        )
        val currentOutputs = workPackageOutputRepository.findAllByOutputIdWorkPackageId(workPackageId)

        workPackageOutputRepository.deleteAll(currentOutputs.filter {
            !outputsToBeSaved.map { it.outputId }.contains(it.outputId)
        })

        return workPackageOutputRepository.saveAll(outputsToBeSaved).toModel()
    }

    @Transactional(readOnly = true)
    override fun getWorkPackageOutputsForWorkPackage(
        workPackageId: Long,
        projectId: Long,
        version: String?
    ): List<WorkPackageOutput> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                getWorkPackageOrThrow(workPackageId).outputs.toModel()
            },
            previousVersionFetcher = { timestamp ->
                workPackageRepository.findOutputsByWorkPackageIdAsOfTimestamp(workPackageId, timestamp)
                    .toWorkPackageOutputsHistoricalData()
            }
        ) ?: emptyList()
    }

    @Transactional(readOnly = true)
    override fun throwIfInvestmentNotExistsInProject(projectId: Long, investmentId: Long) {
        if (!workPackageInvestmentRepository.existsByWorkPackageProjectIdAndId(projectId, investmentId))
            throw InvestmentNotFoundInProjectException(projectId, investmentId)
    }

    @Transactional(readOnly = true)
    override fun getWorkPackageInvestment(
        workPackageInvestmentId: Long,
        projectId: Long,
        version: String?
    ): WorkPackageInvestment {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                getWorkPackageInvestmentOrThrow(workPackageInvestmentId).toWorkPackageInvestment()
            },
            previousVersionFetcher = { timestamp ->
                workPackageInvestmentRepository.findByIdAsOfTimestamp(workPackageInvestmentId, timestamp)
                    .toWorkPackageInvestmentHistoricalData()
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun getWorkPackageInvestments(
        workPackageId: Long,
        projectId: Long,
        version: String?
    ): List<WorkPackageInvestment> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                workPackageInvestmentRepository.findAllByWorkPackageId(workPackageId).toWorkPackageInvestmentList()
            },
            previousVersionFetcher = { timestamp ->
                workPackageInvestmentRepository.findAllByWorkPackageIdAsOfTimestamp(workPackageId, timestamp)
                    .toWorkPackageInvestmentHistoricalList()
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
    override fun getWorkPackageActivitiesForWorkPackage(
        workPackageId: Long,
        projectId: Long,
        version: String?
    ): List<WorkPackageActivity> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                val workPackage = getWorkPackageOrThrow(workPackageId)
                workPackage.activities.toModel(
                    workPackageActivityPartnerRepository.findAllByIdActivityIdIn(workPackage.activities.map { it.id })
                        .groupBy({ it.id.activityId }, { it.id.projectPartnerId })
                )
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
        getWorkPackageOrThrow(workPackageId).let {
//            workPackageActivityPartnerRepository.deleteAllByIdActivityIdIn(it.activities.map { it.id })
            it.updateActivities(workPackageActivities.toIndexedEntity(workPackageId = workPackageId))
//            val partnersByActivities = workPackageActivityPartnerRepository.saveAll(workPackageActivities.toPartners())
//                .groupBy({ it.id.activityId }, { it.id.projectPartnerId })
//            it.activities.toModel(partnersByActivities)
            it.activities.toModel(emptyMap())
        }


    @Transactional(readOnly = true)
    override fun getWorkPackageActivitiesForProject(
        projectId: Long,
        version: String?
    ): List<WorkPackageActivitySummary> {
        // fetch all work packages in 1 request
        val sort = Sort.by(Sort.Direction.ASC, "id")
        val workPackages = workPackageRepository.findAllByProjectId(projectId, sort)
        val workPackageIds = workPackages.mapTo(HashSet()) { it.id }

        // fetch all activities and deliverables in 1 request
        return workPackageActivityRepository.findAllByWorkPackageIdIn(workPackageIds).toSummaryModel()
    }

    private fun List<WorkPackageActivity>.toPartners(): Collection<WorkPackageActivityPartnerEntity> {
        val result = mutableSetOf<WorkPackageActivityPartnerId>()
        this.forEach { activity ->
            activity.partnerIds.forEach {
                result.add(WorkPackageActivityPartnerId(activity.id, it))
            }
        }
        return result.map { WorkPackageActivityPartnerEntity(it) }
    }

    @Transactional(readOnly = true)
    override fun getProjectFromWorkPackageInvestment(workPackageInvestmentId: Long): ProjectApplicantAndStatus =
        getWorkPackageInvestmentOrThrow(workPackageInvestmentId).workPackage.project.toApplicantAndStatus()

    private fun getWorkPackageOrThrow(workPackageId: Long): WorkPackageEntity =
        workPackageRepository.findById(workPackageId).orElseThrow { ResourceNotFoundException("workPackage") }

    private fun throwIfWorkPackageNotFound(workPackageId: Long) {
        workPackageRepository.existsById(workPackageId).also {
            if (!it) ResourceNotFoundException("workPackage")
        }
    }

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

    private fun getActivitiesAndDeliverablesByWorkPackageId(
        workPackageId: Long,
        timestamp: Timestamp
    ): List<WorkPackageActivity> {
        val activities =
            workPackageActivityRepository.findAllActivitiesByWorkPackageIdAsOfTimestamp(workPackageId, timestamp)
                .toActivityHistoricalData()

        activities.forEach {
            it.deliverables =
                workPackageActivityRepository.findAllDeliverablesByActivityIdAsOfTimestamp(
                    it.id,
                    timestamp
                ).toDeliverableHistoricalData()
            it.partnerIds = workPackageActivityPartnerRepository.findAllByActivityIdAsOfTimestamp(
                it.id,
                timestamp
            ).toActivityPartnersHistoricalData()
        }

        return activities
    }

    private fun getWorkPackageInvestmentSummaryHistoricalData(
        projectId: Long,
        timestamp: Timestamp
    ): List<InvestmentSummary> {
        val workPackages = workPackageRepository.findAllByProjectIdAsOfTimestamp(projectId, timestamp)
            .toOutputWorkPackageSimpleHistoricalData()
        var investments = mutableListOf<InvestmentSummary>()

        workPackages.forEach {
            val investmentsForWorkPackage =
                workPackageInvestmentRepository.findAllSummariesByWorkPackageIdAsOfTimestamp(it.id, timestamp)
                    .toWorkPackageInvestmentSummaryList(it.number)
            investments = investments.plus(investmentsForWorkPackage) as MutableList<InvestmentSummary>
        }

        return investments.toList()
    }

    private fun getWorkPackagesForTimePlanInCurrentVersion(projectId: Long): List<ProjectWorkPackage> {
        // fetch all work packages in 1 request
        val workPackages = workPackageRepository.findAllByProjectId(projectId)
        val workPackageIds = workPackages.mapTo(HashSet()) { it.id }

        // fetch all activities and deliverables in 1 request
        val allActivities = workPackageActivityRepository.findAllByWorkPackageIdIn(workPackageIds)
        val activitiesGrouped = allActivities.groupBy { it.workPackageId }

        // fetch all outputs in 1 request
        val outputsByWorkPackages = workPackageOutputRepository.findAllByOutputIdWorkPackageIdIn(workPackageIds)
            .groupBy { it.outputId.workPackageId }

        // fetch projectPartnerIds in 1 request
        val activityIds = allActivities.mapTo(HashSet()) { it.id }
        val projectPartnerIds =
            workPackageActivityPartnerRepository.findAllByIdActivityIdIn(activityIds)
                .groupBy { it.id.activityId }
                .mapValues { it.value.groupBy({ it.id.activityId }, { it.id.projectPartnerId }) }

        return workPackages.map { wp ->
            wp.toModel(
                getActivitiesForWorkPackageId = { id -> activitiesGrouped[id] },
                getOutputsForWorkPackageId = { id -> outputsByWorkPackages[id] },
                getActivityPartnersForWorkPackageId = { id -> projectPartnerIds[id] }
            )
        }
    }

    private fun getWorkPackagesForTimePlanInPreviousVersion(
        projectId: Long,
        timestamp: Timestamp
    ): List<ProjectWorkPackage> {
        // fetch all work packages in 1 request
        val workPackages = workPackageRepository.findAllByProjectIdAsOfTimestamp(projectId, timestamp)
            .toTimePlanWorkPackageHistoricalData()
        val workPackageIds = workPackages.mapTo(HashSet()) { it.id }

        val activities =
            workPackageActivityRepository.findAllByActivityIdWorkPackageIdAsOfTimestamp(workPackageIds, timestamp)
                .toTimePlanActivityHistoricalData()
        // fetch projectPartnerIds in 1 request
        val partnerIdsGroupedBy =
            workPackageActivityPartnerRepository.findAllByWorkPackageIdsAsOfTimestamp(workPackageIds, timestamp)
                .groupBy { it.workPackageId }
                .mapValues { it.value.groupBy({ it.activityId }, { it.projectPartnerId }) }

        activities.forEach {
            it.deliverables =
                workPackageActivityRepository.findAllDeliverablesByActivityIdAsOfTimestamp(
                    it.id,
                    timestamp
                )
                    .toDeliverableHistoricalData()
            it.partnerIds = partnerIdsGroupedBy[it.workPackageId]?.get(it.id)?.toSet().orEmpty()
        }
        val activitiesByWorkPackages = activities.groupBy { it.workPackageId }

        // fetch all outputs in 1 request
        val outputsByWorkPackages =
            workPackageOutputRepository.findAllByOutputIdWorkPackageIdAsOfTimestamp(workPackageIds, timestamp)
                .toTimePlanWorkPackageOutputHistoricalData()
                .groupBy { it.workPackageId }

        workPackages.forEach {
            it.activities =
                if (activitiesByWorkPackages[it.id]?.isNotEmpty() == true) activitiesByWorkPackages[it.id]!! else emptyList()
            it.outputs =
                if (outputsByWorkPackages[it.id]?.isNotEmpty() == true) outputsByWorkPackages[it.id]!! else emptyList()
        }
        return workPackages
    }
}
