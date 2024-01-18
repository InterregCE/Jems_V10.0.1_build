package io.cloudflight.jems.server.project.repository.contracting.reporting

import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.report.project.base.ProjectReportRepository
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.reporting.ContractingReportingPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import kotlin.collections.HashSet

@Repository
class ContractingReportingPersistenceProvider(
    private val projectContractingReportingRepository: ProjectContractingReportingRepository,
    private val projectRepository: ProjectRepository,
    private val projectReportRepository: ProjectReportRepository
): ContractingReportingPersistence {

    @Transactional(readOnly = true)
    override fun getContractingReporting(projectId: Long): List<ProjectContractingReportingSchedule> {
        val linkedReportsByDeadline = projectReportRepository.findAllByProjectIdAndDeadlineNotNull(projectId)
            .groupBy { it.deadline!!.id }

        return projectContractingReportingRepository.findTop50ByProjectIdOrderByDeadline(projectId)
            .map { deadline ->
                deadline.toModel(linkedReports = linkedReportsByDeadline[deadline.id] ?: emptyList())
            }
    }


    @Transactional(readOnly = true)
    override fun getContractingReportingDeadline(projectId: Long, deadlineId: Long): ProjectContractingReportingSchedule {
        val linkedProjectReports = projectReportRepository.findAllByProjectIdAndDeadlineId(projectId, deadlineId)
        return projectContractingReportingRepository.findByProjectIdAndId(projectId, id = deadlineId)
            .toModel(linkedReports = linkedProjectReports)
    }


    @Transactional
    override fun updateContractingReporting(
        projectId: Long,
        deadlines: List<ProjectContractingReportingSchedule>,
    ): List<ProjectContractingReportingSchedule> {
        val toStayIds = deadlines.mapTo(HashSet()) { it.id }.minus(0L)

        val persistedDeadlines = projectContractingReportingRepository.findTop50ByProjectIdOrderByDeadline(projectId)
        val existingById = persistedDeadlines.associateBy { it.id }
        // remove those that were removed
        projectContractingReportingRepository.deleteAll(existingById.minus(toStayIds).values)

        val project = persistedDeadlines.firstOrNull()?.project ?: projectRepository.getById(projectId)

        // update existing or create new ones
        deadlines.forEach { deadline ->
            existingById.getById(deadline.id).let {
                when {
                    it.isPresent -> it.get().updateWith(deadline)
                    else -> projectContractingReportingRepository.save(deadline.toEntity(project))
                }
            }
        }
        return getContractingReporting(projectId)
    }

    @Transactional
    override fun clearPeriodAndDatesFor(ids: List<Long>) {
        projectContractingReportingRepository.updatePeriodAndDatesAsNullFor(ids)
    }

    @Transactional(readOnly = true)
    override fun getScheduleIdsWhosePeriodsAndDatesNotProper(projectId: Long, newMaxDuration: Int): List<Long> {
        return projectContractingReportingRepository.findAllByProjectIdAndPeriodNumberGreaterThan(
            projectId,
            newMaxDuration).map { s -> s.id }
    }

    @Transactional(readOnly = true)
    override fun getReportIdsByDeadlineId(projectId: Long, deadlineId: Long): List<Long> =
        projectReportRepository.findAllByProjectIdAndDeadlineId(projectId, deadlineId).map { it.id }

    private fun Map<Long, ProjectContractingReportingEntity>.getById(id: Long): Optional<ProjectContractingReportingEntity> {
        val value = this[id]
        return if (value != null)
            Optional.of(value)
        else
            Optional.empty()
    }

    private fun ProjectContractingReportingEntity.updateWith(newData: ProjectContractingReportingSchedule) {
        type = newData.type
        periodNumber = newData.periodNumber
        deadline = newData.date
        comment = newData.comment
    }

    private fun ProjectContractingReportingSchedule.toEntity(project: ProjectEntity) =
        ProjectContractingReportingEntity(
            project = project,
            type = type,
            periodNumber = periodNumber,
            deadline = date,
            comment = comment,
            number = number,
        )

}
